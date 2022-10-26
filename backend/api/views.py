from django.contrib.staticfiles.storage import staticfiles_storage
from utils import *

TEST_DATA_CSV = "datasets/single_value_30m_intervals.csv"

# A function used to test the connection to the API
def test_connection(request):
    return return_success({})

# For now this is a dummy view that will read data from a CSV file and send a response
def electricity_usage(request, user_id):
    print(f"Request for electricity data for user id: {user_id}")
    
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
        
    return return_json({"success":True, "usage":json_reading_values})

# Login endpoint
def login(request):
    # Ensure the request is a POST request
    if request.method == 'POST':
        # Attempt to get the credentials from the request
        username = request.POST.get('username', None)
        password = request.POST.get('password', None)
        
        if username != None or password != None:
            return return_json({"success":False})
        
        
    else:
        return return_json({"success":False, "reason":"You must use a POST method to login"})