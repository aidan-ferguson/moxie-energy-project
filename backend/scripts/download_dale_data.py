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
COMPRESSION_RATIO = 3  # For printing accurate(ish) percentages during downloading
TIME_RESOLUTION = 30*60  # Resolution that the data will be stored at in seconds


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
    return int(''.join(digits).replace(',', ''))


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

        if extract_dale_data(folder, filename) == True:
            # Then, decrease the time resolution (just take the average)
            print(f"Changing time resolution of {house}")
            decrease_dale_resolution(os.path.join(folder, house))

            # Finally clean up (delete the downloaded .tgz file)
            print(f"Deleting {house} archive")
            os.remove(dataset_filepath)


# Attempt to extract the downloaded archive using tar, fail and provide message otherwise
def extract_dale_data(folder, filename):
    print(f"Extracting {filename}")
    # Extract to a folder with the same name of the house
    new_folder = os.path.join(folder, filename.split(".")[0])
    if not os.path.exists(new_folder):
        os.mkdir(new_folder)

    try:
        subprocess.check_output(["tar", "-xf", os.path.join(folder, filename), "-C", new_folder])
        return True
    except (FileNotFoundError, subprocess.CalledProcessError):
        # The weird codes here are colour ANSI escape sequences, used so the user can see that something has gone wrong
        print(f"\033[91m File could not be extracted, please manually extract {os.path.join(folder, filename)} to {new_folder} \033[0m")
        return False


def decrease_dale_resolution(house_folder):
    for filename in os.listdir(house_folder):
        if "channel" in filename:
            # Get old file data
            old_data = None
            with open(os.path.join(house_folder, filename), "r") as file:
                old_data = file.readlines()

            # Calculate the old time resolution
            reading_1 = int(old_data[0].split(" ")[0])
            reading_2 = int(old_data[1].split(" ")[0])

            # If we are not reducing the resolution of the dataset, skip this file
            if TIME_RESOLUTION <= (reading_2-reading_1):
                continue

            # Else iterate through the file adding the readings to a list, then when the current reading
            #   and the first one differ by more than the required resolution, take the average and add
            #   it to the new file
            start_time = 0
            old_readings = []
            new_readings = []
            for line in old_data:
                if len(old_readings) == 0:
                    start_time = int(line.split(" ")[0])
                current_time = int(line.split(" ")[0])

                old_readings.append(int(line.split(" ")[1]))

                if (current_time-start_time) > TIME_RESOLUTION:
                    # Average currently gets rounded
                    average = round(sum(old_readings)/len(old_readings))
                    new_readings.append(f"{start_time} {average}")
                    old_readings = []

            with open(os.path.join(house_folder, filename), "w") as file:
                file.write('\n'.join(new_readings))


if __name__ == "__main__":
    dale_download_file(".")
