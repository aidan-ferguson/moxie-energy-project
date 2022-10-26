from django.contrib.staticfiles.storage import staticfiles_storage
from django.contrib.auth import authenticate, login, logout
from django.views.decorators.csrf import csrf_exempt
from django.middleware import csrf
from api.utils import *

TEST_DATA_CSV = "datasets/single_value_30m_intervals.csv"

# A function used to test the connection to the API
def test_connection(request):
    return return_success({})

# For now this is a dummy view that will read data from a CSV file and send a response
def electricity_usage(request):
    print(f"Request for electricity data for user id: {request.user.username}")
    
    # Open the file and store the 10 first readings in an array
    filepath = staticfiles_storage.path(TEST_DATA_CSV)
    reading_values = []
    columns = []
    with open(filepath, "r") as file:
        columns = file.readline().strip().split(",")
        while len(reading_values) < 10:
            reading_values.append(file.readline().strip().split(","))
            
    # Next bundle the readings into an array of dictionaries (JSON format)
    json_reading_values = []
    for value in reading_values:
        reading = {}
        for idx in range(len(value)):
            reading[columns[idx]] = value[idx]
        json_reading_values.append(reading)
        
    return return_success({"usage":json_reading_values})

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