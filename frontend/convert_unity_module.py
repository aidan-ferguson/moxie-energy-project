# Utility script to help with coverting a Unity android export into the format required for the app
# SH-22 Moxie Energy Saving unity game
#
# Follows this procedure:
# - delete unity-module and reexport from unity
# - change build.gradle (launcher) 
# 	- "apply plugin:application" to "apply plugin:library"
# 	- delete application id line
# 	- delete bundle section at bottom
# - set ndkVersion "x.y.z" at bottom of unityLibrary's build gradle (in android section)(currently 21.3.6528147)
# - in order to only have one app icon installed on the phones home screen
# 	- comment out <intent-filter> section on unityLibrary's AndroidManifest.xml file (lines 5 to 8 for me)

import os

UNITY_MODULE_FOLDER = './unity-module'
NDK_VERSION = "21.3.6528147"

def delete_unity_module():
    choice = ""
    while choice != "y" and choice != "n":
        choice = input(f"Delete old unity module folder: {UNITY_MODULE_FOLDER}? (y/n) ").strip().lower()
    
    if choice == "y":
        if os.path.exists(UNITY_MODULE_FOLDER):
            for root, dirs, files in os.walk(UNITY_MODULE_FOLDER, topdown=False):
                for name in files:
                    os.remove(os.path.join(root, name))
                for name in dirs:
                    os.rmdir(os.path.join(root, name))
                    
        print(f"Deleted {UNITY_MODULE_FOLDER}")
    
def edit_launcher_gradle_build():
    # Check file exists
    filename = f"{UNITY_MODULE_FOLDER}/launcher/build.gradle"
    print(f"Editing {filename}")
    
    if not os.path.exists(filename):
        raise SystemExit(f"{filename} does not exist, did you export the unity project?")
    
    # Open file in r mode
    with open(filename, "r") as file:
        data = file.readlines()
        
        # Replace first occurance of 'application' with 'library'
        for idx, line in enumerate(data):
            if 'application' in line:
                data[idx] = line.replace('application', 'library')
                break
        
        # Delete the first line with 'applicationId' in it
        for line in data:
            if 'applicationId' in line:
                data.remove(line)
                break
            
        # Now remove the bundle section
        # depends on the opening bracket being on the same line as the word bundle 
        bundle_section_start = -1
        for idx, line in enumerate(data):
            if 'bundle' in line and '{' in line:
                bundle_section_start = idx
                break
            
        if bundle_section_start < 0:
            print(f"No bundle section found in {filename}")
            return
        
        # Count the number of open a close brackets and when the bundle section closes, delete it
        bracket_level = 0
        bundle_section_end = -1
        for line in range(bundle_section_start, len(data)):
            if '{' in data[line]:
                bracket_level += 1
            if '}' in data[line]:
                bracket_level -= 1
            if bracket_level == 0:
                bundle_section_end = line
                break
        
        if bundle_section_end < 0:
            print(f"Bundle section could not be deleted in {filename}")
            return
        else:
            data = data[:bundle_section_start] + data[bundle_section_end+1:]
        
    # Open again and write to file
    with open(filename, "w") as file:
        file.write(''.join(data))
        
    print(f"Edited {filename}")
    
def add_ndk_version():
    filename = f"{UNITY_MODULE_FOLDER}/unityLibrary/build.gradle"
    print(f"Editing {filename}")
    
    # Check file exists
    if not os.path.exists(filename):
        raise SystemExit(f"{filename} does not exist, did you export the unity project?")
    
    # Open file in read mode
    with open(filename, "r") as file:
        data = file.readlines()
        
        # Find line that starts the android section
        # depends on the opening bracket being on the same line as the word bundle 
        android_section_start = -1
        for idx, line in enumerate(data):
            if 'android' in line and '{' in line:
                android_section_start = idx
                break
            
        if android_section_start < 0:
            print(f"No android section found in {filename}")
            return
        
        # Count the number of open a close brackets in order to get the end of the android section
        bracket_level = 0
        android_section_end = -1
        for line in range(android_section_start, len(data)):
            if '{' in data[line]:
                bracket_level += 1
            if '}' in data[line]:
                bracket_level -= 1
            if bracket_level == 0:
                android_section_end = line
                break
        
        if android_section_end < 0:
            print(f"Could not determine end of android section in {filename}")
            return
        
        data[android_section_end] = f"    ndkVersion '{NDK_VERSION}'\n }}\n"
        
    # Open again and write to file
    with open(filename, "w") as file:
        file.write(''.join(data))
        
    print(f"Edited {filename}")
    
def remove_intent_filter():
    filename = f"{UNITY_MODULE_FOLDER}/unityLibrary/src/main/AndroidManifest.xml"
    print(f"Editing {filename}")
    
    # Check file exists
    if not os.path.exists(filename):
        raise SystemExit(f"{filename} does not exist, did you export the unity project?")
    
    # Open file in read mode
    with open(filename, "r") as file:
        # Find intent filter start
        data = file.readlines()
        
        intent_start = -1
        intent_end = -1
        for idx, line in enumerate(data):
            if '<intent-filter>' in line:
                intent_start = idx
            elif '</intent-filter>' in line:
                intent_end = idx
        
        if intent_start < 0 or intent_end < 0:
            print(f"Couldn't find intent start or end in {filename}")
            return
        else:
            data = data[:intent_start] + data[intent_end+1:]
        
    # Open again and write to file
    with open(filename, "w") as file:
        file.write(''.join(data))
        
    print(f"Edited {filename}")
        
            
if __name__ == "__main__":
    print("Moxie Unity game Build script")
    
    # First delete the unity-module folder to be replaced by the unity export
    delete_unity_module();
    
    # Now prompt to compile new unity module into directory
    input(f"Now export the unity project to {UNITY_MODULE_FOLDER} and press enter when done: ")
    
    # Now edit the launcher gradle build
    edit_launcher_gradle_build()
    
    # Now add the NDK version to the unityLibrary build.gradle file
    add_ndk_version()
    
    # Finally, remove the intent-filter XML element in unityLibrary's AndroidManifest so that 
    #   we don't get two icons on the phones home screen
    remove_intent_filter() 
    
    print("Complete. You can now rebuild in android studio")