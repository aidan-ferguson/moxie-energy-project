"""
Data provider to parse UK-DALE dataset files and return them in the backend standardised format

Each house in the dataset contains different channels, where each channel is a device as described in lables.dat
Each channel has two columns: the UNIX timestamp of the reading and the wattage that channel is using

Note: every house has an aggregate channel that is total power draw (not just the sum of all devices)
"""

from api.data_providers.data_provider import DataProvider
from django.contrib.staticfiles.storage import staticfiles_storage
import time
import os
import datetime
from dateutil.relativedelta import relativedelta
import numpy as np

# Resolution of the DALE dataset in seconds
DALE_RESOLUTION = 30*60
DALE_FOLDER = staticfiles_storage.path("datasets/dale")


class DALEDataProvider(DataProvider):
    @staticmethod
    def get_energy_data(house):
        # Get the start and end timestamps of the dataset
        with open(os.path.join(os.path.join(DALE_FOLDER, house), f"channel_1.dat"), "r") as file:
            data = file.readlines()
        DALE_START_DATE = int(data[0].split(" ")[0])
        DALE_END_DATE = int(data[-1].split(" ")[0])
        
        # Initial energy usage
        start_time = DALE_START_DATE
        start_time = datetime.datetime.fromtimestamp(start_time)
        end_time = start_time + relativedelta(weeks=4)
        initial_usage = DALEDataProvider.read_dale_data("house_4", start_time, end_time)
        if len(initial_usage["data"]) == 0:
            return {"success":False, "reason":"Data for inital month could not be loaded"}
        
        # Loop the data
        start_time = DALE_START_DATE + (int(time.time()) % (DALE_END_DATE - DALE_START_DATE))
        start_time = datetime.datetime.fromtimestamp(start_time)
        end_time = start_time + relativedelta(days=1)
        curr_day = DALEDataProvider.read_dale_data("house_4", start_time, end_time)
        if len(curr_day["data"]) == 0:
            return {"success":False, "reason":"Data for today could not be loaded"}

        end_time = start_time
        start_time = start_time - relativedelta(days=7)
        prev_week = DALEDataProvider.read_dale_data("house_4", start_time, end_time)
        if len(prev_week["data"]) == 0:
            return {"success":False, "reason":"Data for previous week could not be loaded"}

        # Calculate averages for each device in the current and previous months
        initial_usage_averages = np.mean(initial_usage["data"], axis=0)
        prev_week_averages = np.mean(prev_week["data"], axis=0)
        curr_day_averages = np.mean(curr_day["data"], axis=0)
        
        return {"success":True, "data":{"labels": prev_week["labels"], "initial_usage": list(initial_usage_averages), "previous_week": list(prev_week_averages), "today": list(curr_day_averages)}}
        
    @staticmethod
    def read_dale_data(house, start_date, end_date):
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
