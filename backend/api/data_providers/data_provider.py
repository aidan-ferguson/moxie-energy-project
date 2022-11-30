"""
The DataProvider base class guarentees that every sub-class provides the following functionality:

- get_energy_data(start_date, end_date)

This function returns an dictionary in the following format:

{
    "labels": ["aggregate", "electric heater", ...]             # The names of the columns of data
    "starting timestamp": n                                     # In UNIX seconds format
    "interval": n                                               # The interval of the data (each array element is n seconds apart) 
    "data": [
        [aggregate usage, electric heater usage, ...],
        [aggregate usage, electric heater usage, ...],
        ...
    ]
}

The subclasses can interpret data from any source (CSV, external API, etc...) so long as it is returned in this format
"""


class DataProvider(object):
    # Function to be overridden
    @staticmethod
    def get_energy_data(house, start_date, end_date):
        raise NotImplementedError("get_energy_data must be implemented for each DataProvider")