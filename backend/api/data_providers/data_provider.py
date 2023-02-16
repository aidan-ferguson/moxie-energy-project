"""
The DataProvider base class guarentees that every sub-class provides the following functionality:

- get_energy_data()

Note the function can take any parameters it requires (such as the user)

This function returns an dictionary in the following format:

{
    "success": True,
    "data":{
        "labels": ["aggregate", "electric heater", ...],             # The names of the columns of data
        "initial_usage": [..., ..., ...],                            # Initial usage of the device (or just start of the dataset)
        "previous_week": [..., ..., ...],                            # Previous weeks average usage for each device
        "today": [..., ..., ...]                                     # Todays avergae usage for each device
    }
}

or

{
    "success":False,
    "reason": "..."
}

Note that all arrays must be the same length.

The subclasses can interpret data from any source (CSV, external API, etc...) so long as it is returned in this format
"""


class DataProvider(object):
    # Function to be overridden
    @staticmethod
    def get_energy_data():
        raise NotImplementedError("get_energy_data must be implemented for each DataProvider")
