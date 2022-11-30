from django.contrib.auth import authenticate, login, logout
from django.middleware import csrf
from django.contrib.staticfiles.storage import staticfiles_storage
from api.utils import *
from api.data_providers.csv_data_provider import CSVDataProvider
from api.data_providers.dale_data_provider import DALEDataProvider
import datetime
from dateutil.relativedelta import relativedelta
import numpy as np

data_provider = DALEDataProvider

# A function used to test the connection to the API
def test_connection(request):
    return return_success({})

# View that returns raw data for the past month of energy usage
def electricity_usage(request):
    print(f"Request for electricity data for user id: {request.user.username}")
    
    # Treat the start of the data as the current time minus 9 1/2 years  to simulate live data
    start_date = datetime.datetime.now() - relativedelta(years=9, months=3)
    end_date = start_date + relativedelta(months=1)
    
    reading_values = data_provider.get_energy_data("house_4", start_date, end_date)
    return return_success({"usage":reading_values})

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
        
        if username == None or password == None:
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
    # Compute kWh for a month and compare to the average monthly kWh for the same postcode
    #   then express this as a eco-score in range [0, 1] and return it
    
    # Treat the start of the data as the current time minus 9 and a half years to simulate live data
    start_date = datetime.datetime.now() - relativedelta(years=9, months=3)
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

# Get the device usages for a user aswell as the national average
def device_usages(request):
    pass
    
# Logout endpoint
def logout_user(reqeust):
    logout(reqeust)
    return return_success({})