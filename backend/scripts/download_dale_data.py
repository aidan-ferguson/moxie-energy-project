"""
    SH22 Utility script to download data provided by the UK-DALE dataset
    It will be placed in the dataset directory
"""
import requests
import os
import subprocess

# URL, which files we want from the server and the depth of files we want to download
BASE_URL = "https://data.ukedc.rl.ac.uk/mget/edc/efficiency/residential/EnergyConsumption/Domestic/UK-DALE-2015/UK-DALE-disaggregated"
FILES_TO_DOWNLOAD = "*"
DEPTH = 1
COMPRESSION_RATIO = 3 # For printing accurate(ish) percentages

# Get the filesize of the file we want, not essential and is specific to the UK-DALE website
def dale_get_file_size(url):
    response = requests.get(url).content.decode('utf-8')
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
def dale_download_file(folder):
    for house in ["house_3", "house_4"]:
        print(f"Downloading {house} dataset")
        base_url = f"{BASE_URL}/{house}.tgz"
        final_url = f"{base_url}?glob={FILES_TO_DOWNLOAD}&depth={DEPTH}"
        
        # Filename information
        filename = base_url.split("/")[-1]

        if not os.path.exists(folder):
            os.mkdir(folder)

        file_size = dale_get_file_size(final_url)
        dataset_filepath = os.path.join(folder, filename)
        download_url = f"{final_url}&action=Download"
        chunk_size = 8192
        counter = 0
        with requests.get(download_url, stream=True) as r:
            r.raise_for_status()
            with open(dataset_filepath, 'wb') as f:
                for chunk in r.iter_content(chunk_size=chunk_size):
                    # Print out progress every chunk
                    print("{:.2f} ish%".format(100 * COMPRESSION_RATIO * (counter*chunk_size)/file_size), end='\r')
                    f.write(chunk)
                    counter += 1
                    
        extract_dale_data(folder, filename)


# Attempt to extract the downloaded archive using tar, fail and provide message otherwise
def extract_dale_data(folder, filename):
    print(f"Extracting {filename}");
    # Extract to a folder with the same name of the house
    new_folder = os.path.join(folder, filename.split(".")[0])
    if not os.path.exists(new_folder):
        os.mkdir(new_folder)
        
    try:
        subprocess.call(["tar", "-xf", os.path.join(folder, filename), "-C", new_folder])
    except FileNotFoundError:
        # The weird codes here are colour ANSI escape sequences, used so the user can see that something has gone wrong
        print(f"\033[91m File could not be extracted, please manually extract the archive to {new_folder} \033[0m")

if __name__ == "__main__":
    dale_download_file(".")