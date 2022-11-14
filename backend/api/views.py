from django.contrib.auth import authenticate, login, logout
from django.middleware import csrf
from django.contrib.staticfiles.storage import staticfiles_storage
from api.utils import *
from api.data_providers.csv_data_provider import CSVDataProvider
from api.data_providers.dale_data_provider import DALEDataProvider
from datetime import datetime

data_provider = DALEDataProvider

# A function used to test the connection to the API
def test_connection(request):
    return return_success({})

# For now this is a dummy view that will return the raw electricity usage for a given time period
def electricity_usage(request):
    print(f"Request for electricity data for user id: {request.user.username}")
    
    start_date = datetime.strptime(request.GET["start"], "%d/%m/%Y")
    end_date = datetime.strptime(request.GET["end"], "%d/%m/%Y")
    
    reading_values = data_provider.get_energy_data(start_date, end_date)
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

# Return an eco score based on energy usage compared with the national average  
def get_eco_score(request):
    # Compute kWh for a month and compare to the average monthly kWh for the same postcode
    #   then express this as a eco-score in range [0, 1] and return it
    
    # For now use hardcoded dates
    start_date = datetime.strptime("01/03/2013", "%d/%m/%Y")
    end_date = datetime.strptime("01/04/2013", "%d/%m/%Y")
    data = data_provider.get_energy_data(start_date, end_date)
    if len(data["data"]) == 0:
        return return_error("Data could not be loaded")
    
    # Calculate total aggregate for the month
    aggregate_idx = data["labels"].index("aggregate")
    
    # Now calculate kWh = ((sum(power) / 1000) * time inverval) / 3600
    # This is sligtly inaccurate as the device used for the aggregate uses apparent power but we don't know the power factor so we can't convert
    # https://electronics.stackexchange.com/questions/237025/converting-watt-values-over-time-to-kwh
    w_month_total = sum([reading[aggregate_idx] for reading in data["data"]]) / (1000 * data["interval"])
    house_kwh = w_month_total / 3600.0
    
    # Now get the annual average kWh usage in the same area (hardcoded for now) and return the difference scaled by some value
    postcode = "G13 2JU"
    postcode_usage = 0
    gov_filepath = staticfiles_storage.path("datasets/postcodes/postcode_usage.csv")
    with open(gov_filepath, "r") as file:
        while line := file.readline():
            # Postcode is column 0, mean consumption is 3
            line = line.split(",")
            if line[0] == postcode:
                postcode_usage = int(float(line[3]))
                
    # Get monthly usage from annual
    postcode_usage /= 12

    return return_success({"difference":(house_kwh-postcode_usage)})

# Get the device usages for a user aswell as the national average
def device_usages(request):
    pass
    
# Logout endpoint
def logout_user(reqeust):
    logout(reqeust)
    return return_success({})