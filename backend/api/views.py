from django.contrib.auth import logout
from django.middleware import csrf
from api.utils import *
from api.data_providers.dale_data_provider import DALEDataProvider
import datetime
from dateutil.relativedelta import relativedelta
import numpy as np
import openai
import os
from api.serialisers import RegisterSerializer
from rest_framework import permissions
from rest_framework import views
from rest_framework.response import Response
from rest_framework import status

data_provider = DALEDataProvider

# A view used to test an authenticated connection to the server
class TestView(views.APIView):
    permission_classes = (permissions.IsAuthenticated,)

    def get(self, request):
        content = {'message': 'Successfully connected'}
        return Response(content)
    
# User registration endpoint
class RegisterView(views.APIView):
    # Display view to unauthenticated users
    permission_classes = (permissions.AllowAny,)
    
    def post(self, request):
        serializer = RegisterSerializer(data=self.request.data, context={ 'request': self.request })
        serializer.is_valid(raise_exception=True)
        return Response(None, status=status.HTTP_202_ACCEPTED)

# View that returns raw data for the past month of energy usage
def electricity_usage(request):
    print(f"Request for electricity data for user id: {request.user.username}")
    
    # Treat the start of the data as the current time minus 9 1/2 years  to simulate live data
    start_date = datetime.datetime.now() - relativedelta(years=9, months=9)
    end_date = start_date + relativedelta(months=1)
    
    reading_values = data_provider.get_energy_data("house_4", start_date, end_date)
    print(reading_values)
    return return_success({"usage":reading_values})

# Function that will send a valid CSRF token to the client for inclusion in POST requests
def get_csrf_token(request):
    return return_success({"token": csrf.get_token(request)})

# Returns the difference in aggreate power usage for different devices compared to last month 
def get_appliances(request):
    # Compute kWh for a month and compare to the average monthly kWh for the same postcode
    #   then express this as a eco-score in range [0, 1] and return it
    
    # Treat the start of the data as the current time minus 9 and a half years to simulate live data
    start_date = datetime.datetime.now() - relativedelta(years=9, months=9, days=1)
    end_date = start_date + relativedelta(days=1)
    curr_day = data_provider.get_energy_data("house_4", start_date, end_date)
    if len(curr_day["data"]) == 0:
        return return_error("Data for current month could not be loaded")
    
    end_date   = start_date
    start_date = start_date - relativedelta(days=7)
    prev_week = data_provider.get_energy_data("house_4", start_date, end_date)
    if len(prev_week["data"]) == 0:
        return return_error("Data for previous month could not be loaded")
    
    # Calculate averages for each device in the current and previous months
    prev_week_averages = np.mean(prev_week["data"], axis=0)
    curr_day_averages = np.mean(curr_day["data"], axis=0)
    
    return return_success({"labels":prev_week["labels"], "previous_week":list(prev_week_averages), "today":list(curr_day_averages)})

# View to generate and return unique energy saving tips to the user
def get_tips(request):
    # Generate the tip prompt
    prompt = f"A bullet point list of tips for a household with a family of 4 that used 10kWh of electricity today:\n"
    
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
    tips = response["choices"][0]["text"].replace("\u2013", "\2022").replace("\n-","\u2022").split("\u2022")
    tips = [tip.strip() for tip in tips]
    
    # Typically the last bullet point will be cut off as it runs out of characters, so exlcude it
    
    return return_success({"tips": tips})
    
# Logout endpoint
def logout_user(reqeust):
    logout(reqeust)
    return return_success({})