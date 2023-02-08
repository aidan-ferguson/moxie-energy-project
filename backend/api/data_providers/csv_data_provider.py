from api.data_providers.data_provider import DataProvider
from django.contrib.staticfiles.storage import staticfiles_storage
from datetime import datetime

TEST_DATA_CSV = "datasets/test_data2.csv"


# TODO: make sure conform to new format
class CSVDataProvider(DataProvider):

    @staticmethod
    def get_energy_data(start_date, end_date):
        # Read data from the file
        data = CSVDataProvider.read_csv_file(staticfiles_storage.path(TEST_DATA_CSV))

        # Only return the dates in the range
        filtered_data = []
        for entry in data:
            date = datetime.strptime(entry[0], "%d/%m/%Y")
            if date >= start_date and date <= end_date:
                filtered_data.append(entry)

        return filtered_data

    @staticmethod
    def read_csv_file(filename):
        with open(filename, "r") as file:
            # Discard first 2 lines
            _ = file.readline()
            _ = file.readline()
            lines = file.readlines()

        # Change all the columns to numbers (except for the first one, it is a timestamp)
        return list(map(lambda x: [x[0].strip()] + list(map(lambda y: float(y), x[1:])), [line.strip().split(",") for line in lines]))
