"""
Data provider to parse UK-DALE dataset files and return them in the backend standardised format

Each house in the dataset contains different channels, where each channel is a device as described in lables.dat
Each channel has two columns: the UNIX timestamp of the reading and the wattage that channel is using

Note: every house has an aggregate channel that is total power draw (not just the sum of all devices)
"""

from api.data_providers.data_provider import DataProvider
from django.contrib.staticfiles.storage import staticfiles_storage
from datetime import datetime
import time
import os

# Resolution of the DALE dataset in seconds
DALE_RESOLUTION = 6

DALE_FOLDER = staticfiles_storage.path("datasets/dale")

class DALEDataProvider(DataProvider):
    @staticmethod
    def get_energy_data(house, start_date, end_date):
        # Read the labels file to get a dictionary mapping integers to channel names
        DATASET_FOLDER = os.path.join(DALE_FOLDER, house)
        labels = {}
        with open(os.path.join(DATASET_FOLDER, "labels.dat"), "r") as file:
            labels = {int(line.split(" ")[0]): line.split(" ")[1].strip() for line in file.readlines()}
            
        # Split up the start date and end date into 6 second segments so that we can 
        #   attribute 1 timestamp to readings that were probably taken at the same time
        start_unix = int(time.mktime(start_date.timetuple()))
        end_unix = int(time.mktime(end_date.timetuple()))
        
        # Create array which will be returned as part of response at the end
        data = [[0 for _ in range(len(labels))] for _ in range(start_unix, end_unix+DALE_RESOLUTION, DALE_RESOLUTION)]
        
        # If it needs to be faster we should preprocess the data into a format similar to ours
        # Go through each file and add the data for the corresponding timestamps
        for channel_idx in labels.keys():
            with open(os.path.join(DATASET_FOLDER, f"channel_{channel_idx}.dat"), "r") as file:
                while line := file.readline():
                    # Check the current line is within the date range
                    line = list(map(lambda x: int(x), line.split(" ")))
                    if line[0] > end_unix:
                        break
                    if line[0] < start_unix:
                        continue
                    
                    # Get the index of the data it should be in
                    data_idx = (line[0] - start_unix)//DALE_RESOLUTION
                    
                    # We minus 1 as the channel id's are not 0 indexed
                    data[data_idx][channel_idx-1] = line[1]
                    
        # Get lables into array format
        labels = [labels[idx] for idx in range(1, len(labels)+1)]
                    
        # Construct final response
        ret_val = {
            "labels": labels,
            "starting timestamp": start_unix,
            "interval": DALE_RESOLUTION,
            "data": data
        }
                        
        return ret_val