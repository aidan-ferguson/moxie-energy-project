"""
    SH22 Utility script to download national average electricity consumption data
    It will be placed in the dataset directory
"""
import requests
import os

# URL, which files we want from the server and the depth of files we want to download
BASE_URLS = ["https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/1050244/Postcode_Level_Standard_Electricity_2020_A_to_K.csv",
             "https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/1050245/Postcode_Level_Standard_Electricity_2020_K_to_Z.csv"]

FINAL_FILENAME = "postcode_usage.csv"

# Will download the file in chuncks, periodically saving them to disk
def postcode_download_file(folder):
    if not os.path.exists(folder):
        os.mkdir(folder)
    
    for url in BASE_URLS:
        filename = url.split("/")[-1]
        dataset_filepath = os.path.join(folder, filename)
        chunk_size = 8192
        counter = 0
        with requests.get(url, stream=True) as r:
            file_size = int(r.headers['Content-length'])
            r.raise_for_status()
            with open(dataset_filepath, 'wb') as f:
                for chunk in r.iter_content(chunk_size=chunk_size):
                    # Print out progress every chunk
                    print("{:.2f} %".format(100 * (counter*chunk_size)/file_size), end='\r')
                    f.write(chunk)
                    counter += 1
                
    return dataset_filepath

# The files are downloaded as two seperate CSVs, simply concatenate them into one
def postcode_combine_files(folder):
    combined = []
    header = ""
    # For each CSV already in the folder that is in the downloads list, concatenate them
    potential_filenames = [url.split("/")[-1] for url in BASE_URLS]
    for filename in os.listdir(folder):
        filepath = os.path.join(folder, filename)
        if os.path.isfile(filepath) and filename in potential_filenames:
            with open(filepath, "r") as file:
                # We want to consume the header first as we only need one
                header = file.readline()
                combined += file.readlines()

    # Output the combined CSVs to a file
    with open(os.path.join(folder, FINAL_FILENAME), "w") as file:
        file.write(header + ''.join(combined))
            

if __name__ == "__main__":
    postcode_download_file(".")
    postcode_combine_files("./static/datasets/postcodes")