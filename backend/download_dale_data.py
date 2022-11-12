"""
    SH22 Utility script to download data provided by the UK-DALE dataset
    It will be placed in the dataset directory
"""
import requests
import os

# URL, which files we want from the server and the depth of files we want to download
BASE_URL = "https://data.ukedc.rl.ac.uk/mget/edc/efficiency/residential/EnergyConsumption/Domestic/UK-DALE-2015/UK-DALE-disaggregated/house_3.tgz"
FILES_TO_DOWNLOAD = "*"
DEPTH = 1
FINAL_URL = f"{BASE_URL}?glob={FILES_TO_DOWNLOAD}&depth={DEPTH}"
COMPRESSION_RATIO = 3 # For printing accurate(ish) percentages

# Filename information
FILENAME = BASE_URL.split("/")[-1]
DATASET_FOLDER = "./static/datasets/dale"
DATASET_FILEPATH = os.path.join(DATASET_FOLDER, FILENAME)

# Get the filesize of the file we want, not essential and is specific to the UK-DALE website
def get_file_size():
    response = requests.get(FINAL_URL).content.decode('utf-8')
    # Just get the first occurance of the word bytes and get the next number after it
    byte_idx = response.index("bytes")
    digits = []
    for idx in range(byte_idx, len(response)):
        if response[idx].isdigit() or response[idx] == ',':
            digits.append(response[idx])
        else:
            if len(digits) > 0:
                break
    return int(''.join(digits).replace(',',''))

# Will download the file in chuncks, periodically saving them to disk
def download_file(file_size):
    download_url = f"{FINAL_URL}&action=Download"
    chunk_size = 8192
    counter = 0
    with requests.get(download_url, stream=True) as r:
        r.raise_for_status()
        with open(DATASET_FILEPATH, 'wb') as f:
            for chunk in r.iter_content(chunk_size=chunk_size):
                # Print out progress every chunk
                print("{:.2f} ish%".format(100 * COMPRESSION_RATIO * (counter*chunk_size)/file_size), end='\r')
                f.write(chunk)
                counter += 1
                
    return DATASET_FILEPATH

if __name__ == "__main__":
    file_size = get_file_size()
    download_file(file_size)