from django.contrib.auth import authenticate, login, logout
from django.middleware import csrf
from api.utils import return_error, return_success
from api.data_providers.dale_data_provider import DALEDataProvider
import datetime
import time
from dateutil.relativedelta import relativedelta
import numpy as np
import openai
import os

data_provider = DALEDataProvider


# A function used to test the connection to the API
def test_connection(request):
    return return_success({})


# Function that will send a valid CSRF token to the client for inclusion in POST requests
def get_csrf_token(request):
    return return_success({"token": csrf.get_token(request)})


# Login endpoint
def login_user(request):
    # Ensure the request is a POST request
    if request.method == 'POST':
        # Attempt to get the credentials from the request
        username = request.POST.get('username', None)
        password = request.POST.get('password', None)

        if username is None or password is None:
            return return_error("You must provide a username and password in the request")

        user = authenticate(username=username, password=password)
        if user:
            if user.is_active:
                login(request, user)
                return return_success({})
            else:
                return return_error("This account has been disabled")
        else:
            return return_error("Invalid credentials")
 
    else:
        return return_error("You must use a POST method to login")


# Returns the difference in aggreate power usage for different devices compared to last month
def get_appliances(request):
    # Right now the bounds of the house_4 dataset are defined so we can loop the data
    DALE_START_DATE = 1362840007
    DALE_END_DATE = 1380602255
    
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
    prev_week_averages = np.mean(prev_week["data"], axis=0)
    curr_day_averages = np.mean(curr_day["data"], axis=0)

    return return_success({"labels": prev_week["labels"], "previous_week": list(prev_week_averages), "today": list(curr_day_averages)})


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
