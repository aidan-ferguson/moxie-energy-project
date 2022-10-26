"""
The DataProvider base class guarentees that every sub-class provides the following functionality:

- get_energy_data(start_date, end_date)

This function returns data in the following format:

[
    [timestamp, total energy consumption, %power used by appliance 1, ..., %power used by appliance n, kWh by appliance 1, ..., kWh by appliance 2]
    ...
]

The subclasses can interpret data from any source (CSV, external API, etc...) so long as it is returned in this format
"""


class DataProvider(object):
    # Function to be overridden
    @staticmethod
    def get_energy_data(start_date, end_date):
        raise NotImplementedError("get_energy_data must be implemented for each DataProvider")