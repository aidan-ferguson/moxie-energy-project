"""
SH22 backend setup script
For:
- downloading datasets

"""
from scripts.download_dale_data import dale_download_file
from scripts.download_national_averages import postcode_download_file, postcode_combine_files
import os

DATASET_FOLDER = os.path.realpath("./static/datasets")

if __name__ == "__main__":
    # First download & extract the DALE data
    print("Downloading DALE dataset")
    dale_folder = os.path.join(DATASET_FOLDER, "dale")
    dale_download_file(dale_folder)

    # Then download & combine the national average postcode datasets
    print("Downloading national average dataset")
    postcode_folder = os.path.join(DATASET_FOLDER, "postcodes")
    postcode_download_file(postcode_folder)
    print("Combining files")
    postcode_combine_files(postcode_folder)
    print(f"Downloaded & Combined UK postcode usage datasets to {postcode_folder}")