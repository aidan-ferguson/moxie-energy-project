from django.contrib.auth import logout
from api.utils import json_error, json_success, prompt_gpt3, Prompts
from api.data_providers.dale_data_provider import DALEDataProvider
import datetime
import time
from dateutil.relativedelta import relativedelta
import numpy as np
from api.serialisers import RegisterSerializer
from rest_framework import permissions
from rest_framework import views
from rest_framework.response import Response
from rest_framework import status
from django.contrib.staticfiles.storage import staticfiles_storage
import json
import openai
from api import models

data_provider = DALEDataProvider

# A view used to test an authenticated connection to the server
class TestView(views.APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request):
        content = {'message': 'Successfully connected as ' + request.user.username}
        return Response(content)
    
# User registration endpoint
class RegisterView(views.APIView):
    # Display view to unauthenticated users
    permission_classes = (permissions.AllowAny,)
    
    def post(self, request):
        serializer = RegisterSerializer(data=self.request.data, context={ 'request': self.request })
        serializer.is_valid(raise_exception=True)
        return Response(None, status=status.HTTP_202_ACCEPTED)
    
# For getting and updating user info
class UserInfoView(views.APIView):
    permission_classes = (permissions.IsAuthenticated,)
    
    def get(self, request):
        content = {'user_data': {'username':request.user.username,
                                 'firstname':request.user.first_name,
                                 'surname':request.user.last_name}}
        # TODO: Change to json success/failure
        return Response(content)
    
# View for returning the national averages of devices 
class NationalAverageView(views.APIView):
    permission_classes = (permissions.AllowAny,)
    
    def get(self, request):
        with open(staticfiles_storage.path("datasets/dale/house_averages.dat"), "r") as file:
            # TODO: Change to json success/failure
            return Response(json.loads(file.read()))
        
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

# View for generating energy reports
class EnergyReportView(views.APIView):
    permission_classes = (permissions.IsAuthenticated, )
    
    def get(self, request):
        # Check if the user already has an energy report for today
        cached = models.Tip.objects.filter(date=datetime.date.today(), user=request.user, device="aggregate")
        if cached.exists():
            return Response(json_success(cached.first().text))
        
        try:
            prompt = Prompts.get_energy_report_prompt(json.loads(AppliancesView.get(request).content)['data'])
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
        # Right now the bounds of the house_4 dataset are defined so we can loop the data
        DALE_START_DATE = 1362840007
        DALE_END_DATE = 1380602255
        
        # Initial energy usage
        start_time = DALE_START_DATE
        start_time = datetime.datetime.fromtimestamp(start_time)
        end_time = start_time + relativedelta(weeks=4)
        initial_usage = data_provider.get_energy_data("house_4", start_time, end_time)
        if len(initial_usage["data"]) == 0:
            return Response(status=status.HTTP_500_INTERNAL_SERVER_ERROR, data=json_error("Data for current month could not be loaded"))
        
        start_time = DALE_START_DATE + (int(time.time()) % (DALE_END_DATE - DALE_START_DATE))
        start_time = datetime.datetime.fromtimestamp(start_time)
        end_time = start_time + relativedelta(days=1)
        curr_day = data_provider.get_energy_data("house_4", start_time, end_time)
        if len(curr_day["data"]) == 0:
            return Response(status=status.HTTP_500_INTERNAL_SERVER_ERROR, data=json_error("Data for current month could not be loaded"))

        end_time = start_time
        start_time = start_time - relativedelta(days=7)
        prev_week = data_provider.get_energy_data("house_4", start_time, end_time)
        if len(prev_week["data"]) == 0:
            return Response(status=status.HTTP_500_INTERNAL_SERVER_ERROR, data=json_error("Data for previous month could not be loaded"))

        # Calculate averages for each device in the current and previous months
        initial_usage_averages = np.mean(initial_usage["data"], axis=0)
        prev_week_averages = np.mean(prev_week["data"], axis=0)
        curr_day_averages = np.mean(curr_day["data"], axis=0)

        return Response(json_success({"labels": prev_week["labels"], "initial_usage": list(initial_usage_averages), "previous_week": list(prev_week_averages), "today": list(curr_day_averages)}))

# Will delete the currently stored token of a user
class LogoutView(views.APIView):
    permission_classes = (permissions.IsAuthenticated,)
    
    def post(self, request):
        request.user.auth_token.delete()
        return Response(status=status.HTTP_200_OK)
