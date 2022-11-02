from django.contrib.auth import authenticate, login, logout
from django.middleware import csrf
from api.utils import *
from api.data_providers.csv_data_provider import CSVDataProvider
from datetime import datetime

# A function used to test the connection to the API
def test_connection(request):
    return return_success({})

# For now this is a dummy view that will read data from a CSV file and send a response
def electricity_usage(request):
    print(f"Request for electricity data for user id: {request.user.username}")
    
    start_date = datetime.strptime(request.GET["start"], "%d/%m/%Y")
    end_date = datetime.strptime(request.GET["end"], "%d/%m/%Y")
    
    reading_values = CSVDataProvider.get_energy_data(start_date, end_date)
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
    
# Logout endpoint
def logout_user(reqeust):
    logout(reqeust)
    return return_success({})