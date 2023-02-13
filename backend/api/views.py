from django.contrib.auth import logout
from django.middleware import csrf
from api.utils import return_error, return_success
from api.data_providers.dale_data_provider import DALEDataProvider
import datetime
import time
from dateutil.relativedelta import relativedelta
import numpy as np
import openai
import os
from api.serialisers import RegisterSerializer
from rest_framework import permissions
from rest_framework import views
from rest_framework.response import Response
from rest_framework import status
from django.contrib.staticfiles.storage import staticfiles_storage
import json

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
        return Response(content)
    
class NationalAverage(views.APIView):
    permission_classes = (permissions.AllowAny,)
    
    def get(self, request):
        with open(staticfiles_storage.path("datasets/dale/house_averages.dat"), "r") as file:
            return Response(json.loads(file.read()))
        

# Returns the difference in aggreate power usage for different devices compared to last month 
def get_appliances(request):
    # Right now the bounds of the house_4 dataset are defined so we can loop the data
    DALE_START_DATE = 1362840007
    DALE_END_DATE = 1380602255
    
    # Initial energy usage
    start_time = DALE_START_DATE
    start_time = datetime.datetime.fromtimestamp(start_time)
    end_time = start_time + relativedelta(weeks=4)
    initial_usage = data_provider.get_energy_data("house_4", start_time, end_time)
    if len(initial_usage["data"]) == 0:
        return return_error("Data for current month could not be loaded")
    
    start_time = DALE_START_DATE + (int(time.time()) % (DALE_END_DATE - DALE_START_DATE))
    start_time = datetime.datetime.fromtimestamp(start_time)
    end_time = start_time + relativedelta(days=1)
    curr_day = data_provider.get_energy_data("house_4", start_time, end_time)
    if len(curr_day["data"]) == 0:
        return return_error("Data for current month could not be loaded")

    end_time = start_time
    start_time = start_time - relativedelta(days=7)
    prev_week = data_provider.get_energy_data("house_4", start_time, end_time)
    if len(prev_week["data"]) == 0:
        return return_error("Data for previous month could not be loaded")

    # Calculate averages for each device in the current and previous months
    initial_usage_averages = np.mean(initial_usage["data"], axis=0)
    prev_week_averages = np.mean(prev_week["data"], axis=0)
    curr_day_averages = np.mean(curr_day["data"], axis=0)

    return return_success({"labels": prev_week["labels"], "initial_usage": list(initial_usage_averages), "previous_week": list(prev_week_averages), "today": list(curr_day_averages)})


# View to generate and return unique energy saving tips to the user
def get_tips(request):
    # Generate the tip prompt
    prompt = "A bullet point list of tips for a household with a family of 4 that used 10kWh of electricity today:\n"

    # Get the list of tips from OpenAI
    openai.api_key = os.getenv("OPENAI_API_KEY")
    response = openai.Completion.create(
        model="text-davinci-003",
        prompt=prompt,
        temperature=0.7,
        max_tokens=128,
        top_p=1,
        frequency_penalty=0,
        presence_penalty=0
    )

    # Parse the response and split on the bullet point
    if len(response["choices"]) == 0:
        print(response)
        return return_error("Failed to generate any tips")
    tips = response["choices"][0]["text"].replace("\u2013", "\2022").replace("\n-", "\u2022").split("\u2022")
    tips = [tip.strip() for tip in tips]

    # Typically the last bullet point will be cut off as it runs out of characters, so exlcude it
    return return_success({"tips": tips})


# Logout endpoint
def logout_user(reqeust):
    logout(reqeust)
    return return_success({})
