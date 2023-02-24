from api.utils import json_error, json_success, prompt_gpt3, Prompts, get_user_energy_data, calculate_energy_score
import datetime
from api.serialisers import RegisterSerializer
from rest_framework import permissions
from rest_framework import views
from rest_framework.response import Response
from rest_framework import status
from django.contrib.staticfiles.storage import staticfiles_storage
import json
import openai
from api import models
import math


# A view used to test an authenticated connection to the server
class TestView(views.APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request):
        content = {'message': 'Successfully connected as ' + request.user.username}
        return Response(json_success(content))
    

# User registration endpoint
class RegisterView(views.APIView):
    # Display view to unauthenticated users
    permission_classes = (permissions.AllowAny,)
    
    def post(self, request):
        serializer = RegisterSerializer(data=self.request.data, context={'request': self.request})
        serializer.is_valid(raise_exception=True)
        return Response(None, status=status.HTTP_202_ACCEPTED)
    
    
# For getting and updating user info
class UserInfoView(views.APIView):
    permission_classes = (permissions.IsAuthenticated,)
    
    def get(self, request):
        content = {'id': request.user.id,
                    'username': request.user.username,
                    'firstname': request.user.first_name,
                    'surname': request.user.last_name,
                    'data_provider': request.user.data_provider}
        return Response(json_success(content))
    
    def post(self, request):
        # Check if user wants to update
        if request.data.get("action", None) == "update":
            # Allow only certain actions
            update_parameters = {}
            update_parameters["first_name"] = request.data.get("firstname", None)
            update_parameters["last_name"] = request.data.get("last_name", None)
            update_parameters["data_provider"] = request.data.get("data_provider", None)
            
            # Remove None elements
            update_parameters = {elem: update_parameters[elem] for elem in update_parameters if update_parameters[elem] is not None}
            models.User.objects.filter(id=request.user.id).update(**update_parameters)
            return Response(status=status.HTTP_200_OK)
            
        return Response(status=status.HTTP_400_BAD_REQUEST)
    

# View for returning the national averages of devices
class NationalAverageView(views.APIView):
    permission_classes = (permissions.AllowAny,)
    
    def get(self, request):
        with open(staticfiles_storage.path("datasets/dale/house_averages.dat"), "r") as file:
            try:
                return Response(json_success(json.loads(file.read())))
            except FileNotFoundError:
                return Response(json_error("Could not load national averages"))
        

# View to get the tip of the day
class TOTDView(views.APIView):
    permission_classes = (permissions.IsAuthenticated, )
    
    def get(self, request):
        # Attempt to fetch local cache first
        cached = models.TOTD.objects.filter(date=datetime.date.today())
        if cached.exists():
            return Response(json_success(cached.first().tip.text))
        
        # If none in cache, generate one and then cache it
        try:
            prompt = Prompts.get_tipoftheday_prompt()
            response = prompt_gpt3(prompt).strip()
            tip = models.Tip.objects.create(device="totd", text=response)
            models.TOTD.objects.create(tip=tip)
            return Response(json_success(response))
        except openai.OpenAIError as e:
            print(f"{str(e.__class__.__name__ )}: {e}")
            return Response(json_error("An internal error occured with generating tips"))

#TODO: all json_errors explicitly 
# View for generating energy reports
class EnergyReportView(views.APIView):
    permission_classes = (permissions.IsAuthenticated, )
    
    def get(self, request):
        # Check if the user already has an energy report for today
        cached = models.Tip.objects.filter(date=datetime.date.today(), user=request.user, device="aggregate")
        if cached.exists():
            return Response(json_success(cached.first().text))
        
        try:
            prompt = Prompts.get_energy_report_prompt(get_user_energy_data(request.user)['data'])
            response = prompt_gpt3(prompt).strip()
            models.Tip.objects.create(device="aggregate", text=response, user=request.user)
            return Response(json_success(response))
        except openai.OpenAIError as e:
            print(f"{str(e.__class__.__name__ )}: {e}")
            return Response(json_error("An internal error occured with generating tips"))


# View responsible for returning the usage of appliances
class AppliancesView(views.APIView):
    permission_classes = (permissions.IsAuthenticated, )
    
    # Returns the difference in aggreate power usage for different devices compared to last month
    def get(self, request):
        energy_data = get_user_energy_data(request.user)
        if energy_data["success"]:
            score = calculate_energy_score(energy_data["data"])
            if not (math.isnan(score) or math.isinf(score)):
                energy_data["data"]["energy_score"] = score
            else:
                energy_data["data"]["energy_score"] = 0.0
            
        # get_user_energy_data already returns a json object so we don't need to use the utility functions
        return Response(energy_data)


# Will delete the currently stored token of a user
class LogoutView(views.APIView):
    permission_classes = (permissions.IsAuthenticated,)
    
    def post(self, request):
        request.user.auth_token.delete()
        return Response(status=status.HTTP_200_OK)


# Get or add new freinds
class FriendView(views.APIView):
    permission_classes = (permissions.IsAuthenticated, )
    
    # Get list of friends and incoming friend requests
    def get(self, request):
        ret_val = {}
        
        ret_val["friends"] = [models.Friendship.friendship_to_json(friendship, request.user) for friendship in models.Friendship.get_user_friends(request.user)]
        ret_val["requests"] = [models.Friendship.friendship_to_json(friendship, request.user) for friendship in models.Friendship.get_friend_requests(request.user)]
        
        return Response(json_success(ret_val))
    
    def post(self, request):
        action = request.data.get("action", None)
        other_user_id = request.data.get("user_id", "")
        try:
            other_user_id = int(other_user_id)
        except ValueError:
            return Response(status=status.HTTP_400_BAD_REQUEST, data=json_error("User ID must be an integer"))

        if (action is None) or (other_user_id is None):
            return Response(status=status.HTTP_400_BAD_REQUEST, data=json_error("You need to send a user id and an action"))
        
        other_user = models.User.objects.filter(id=other_user_id)
        if not other_user.exists():
            return Response(status=status.HTTP_404_NOT_FOUND, data=json_error("Could not find a user with that ID"))
        other_user = other_user.first()
        
        if action == "make_request":
            # User wants to make friend request
            # Check there is not already a request
            if models.Friendship.objects.filter(from_user=request.user, to_user=other_user):
                return Response(status=status.HTTP_400_BAD_REQUEST, data=json_error("You have already sent a friend request"))
            
            # In the case that a request from the other user to the current user already exists, then just accept it
            previous_request = models.Friendship.objects.filter(from_user=other_user, to_user=request.user)
            if previous_request.exists():
                previous_request.update(has_accepted=True)
            else:
                models.Friendship.objects.get_or_create(from_user=request.user, to_user=other_user, has_accepted=False)
            return Response(json_success("Added request"))
        
        elif action == "accept_request":
            # User wants to accept request
            existing_request = models.Friendship.objects.filter(from_user=other_user, to_user=request.user)
            if not existing_request.exists():
                return Response(status=status.HTTP_400_BAD_REQUEST, data=json_error("That friend request does not exist"))
            existing_request.update(has_accepted=True)
            return Response(json_success("Accepted friend request"))
        
        elif action == "deny_request":
            models.Friendship.objects.filter(from_user=other_user, to_user=request.user).delete()
            return Response(json_success("Deleted"))
        
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST, data=json_error("You must send a valid action"))
