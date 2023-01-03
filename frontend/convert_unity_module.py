# Utility script to help with coverting a Unity android export into the format required for the app
# SH-22 Moxie Energy Saving unity game
#
# Follows this procedure:
# - delete unity-module and reexport from unity
# - change build.gradle (launcher) 
# 	- "apply plugin:application" to "apply plugin:library"
# 	- delete application id line
# 	- delete bundle section at bottom
# - in order to only have one app icon installed on the phones home screen
# 	- comment out <intent-filter> section on unityLibrary's AndroidManifest.xml file (lines 5 to 8 for me)
# - remove android app icon from launcher's AndroidManifest.xml
# - add exported:true for unity player's manifest
# - check if source.properties exists in unity-module folder and replace ndk version
#   - then set ndkVersion "x.y.z" at bottom of unityLibrary's build gradle (in android section)(currently 21.3.6528147)

import os

UNITY_MODULE_FOLDER = './unity-module'
NDK_VERSION = "21.3.6528147" # This can be changed if source.properties is present in unity-module dir (happens in CI pipeline)
    
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
    # First check for a source.properties
    ndk_version_filename = f"{UNITY_MODULE_FOLDER}/source.properties"
    if os.path.exists(ndk_version_filename):
        with open(ndk_version_filename, "r") as file:
            lines = file.readlines()
            for line in lines:
                if 'Pkg.Revision' in line:
                    line = line.split("=")
                    NDK_VERSION = line[-1].strip()
    
    filename = f"{UNITY_MODULE_FOLDER}/unityLibrary/build.gradle"
    print(f"Editing {filename}")
    print(f"Using NDK version {NDK_VERSION}")
    
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
    
    
def remove_app_icon():
    filename = f"{UNITY_MODULE_FOLDER}/launcher/src/main/AndroidManifest.xml"
    print(f"Editing {filename}")
    
    # Check file exists
    if not os.path.exists(filename):
        raise SystemExit(f"{filename} does not exist, did you export the unity project?")
    
    # Open file in read mode
    with open(filename, "r") as file:
        # Find intent filter start
        data = file.readlines()
        
        for line_idx in range(len(data)):
            # Just replace any occurances of the icon specification with nothing
            data[line_idx] = data[line_idx].replace('android:icon="@mipmap/app_icon"', '')
    
    # Open again and write to file
    with open(filename, "w") as file:
        file.write(''.join(data))
        
    print(f"Edited {filename}")
    
def define_exported_attribute():
    filename = f"{UNITY_MODULE_FOLDER}/unityLibrary/src/main/AndroidManifest.xml"
    print(f"Editing {filename}")
    
    # Check file exists
    if not os.path.exists(filename):
        raise SystemExit(f"{filename} does not exist, did you export the unity project?")
    
    # Open file in read mode
    with open(filename, "r") as file:
        # Find application tag
        data = file.readlines()

        for line_idx in range(len(data)):
            if '<activity' in data[line_idx]:
                data[line_idx] = data[line_idx].replace('<activity', '<activity android:exported="true"')
    
    # Open again and write to file
    with open(filename, "w") as file:
        file.write(''.join(data))
        
    print(f"Edited {filename}")
            
if __name__ == "__main__":
    print("Moxie Unity game Build script")
    
    # Now edit the launcher gradle build
    edit_launcher_gradle_build()
    
    # Now add the NDK version to the unityLibrary build.gradle file
    add_ndk_version()
    
    # Remove the intent-filter XML element in unityLibrary's AndroidManifest so that 
    #   we don't get two icons on the phones home screen
    remove_intent_filter() 
    
    # Remove unity launcher's app icon
    remove_app_icon()
    
    # Set the android:exported attribute in unity player's manifest
    define_exported_attribute()
    
    print("Complete. You can now rebuild in android studio")