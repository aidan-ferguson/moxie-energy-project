"""
SH22 backend setup script
For:
- downloading datasets

"""
from scripts.download_dale_data import dale_download_files, dale_calculate_averages
from scripts.download_national_averages import postcode_download_file, postcode_combine_files
import os
import argparse

DATASET_FOLDER = os.path.realpath(f"{os.path.dirname(os.path.abspath(__file__))}/static/datasets")

if __name__ == "__main__":
    parser = argparse.ArgumentParser("python setup.py")
    parser.add_argument("--exclude-dale", nargs="?", help="Specify dale datasets to be skipped over, comma seperated", type=str, default=None)
    args = parser.parse_args()
    
    if args.exclude_dale:
        args.exclude_dale = args.exclude_dale.split(",")
    
    # First download & extract the DALE data
    print("Downloading DALE dataset")
    dale_folder = os.path.join(DATASET_FOLDER, "dale")
    dale_download_files(dale_folder, args.exclude_dale)
    dale_calculate_averages(dale_folder)

    # Then download & combine the national average postcode datasets
    print("Downloading national average dataset")
    postcode_folder = os.path.join(DATASET_FOLDER, "postcodes")
    postcode_download_file(postcode_folder)
    print("Combining files")
    postcode_combine_files(postcode_folder)
    print(f"Downloaded & Combined UK postcode usage datasets to {postcode_folder}")