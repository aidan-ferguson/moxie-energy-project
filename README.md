# SH22 Moxie Energy Project
## Team members
- Conor Brown - 2397922b@student.gla.ac.uk
- Joshua Ko - 261298k@student.gla.ac.uk
- Raphael Memmi - 256317m@student.gla.ac.uk
- Amie Macmillan - 2472510m@student.gla.ac.uk
- Aidan Ferguson - 2566198f@student.gla.ac.uk
- Jamie Valentine - 2560013@student.gla.ac.uk

## Setup Instructions
### Backend
In order to run the backend locally, you will need to have `python3.8` or greater installed.
Note, on Linux and MacOS in all of the following commands `python` should be replaced with `python3`

1. Open a terminal in the `backend` folder.
2. Run `pip install --user pipenv` to install the program required for virtual enviroments
3. Create a virtual enviroment and install project dependancies using the following command: `python -m pipenv install`
4. Then run `python -m pipenv shell` to activate the virtual enviroment.
5. Then `python setup.py` followed by `python reset_db.py`
6. Optionally, if you want to use ChatGPT for tips generation you need to get an API key from the [OpenAI dashboard](https://beta.openai.com/account/api-keys) and run: `export OPENAI_API_KEY="yourkey"` on MacOS/Linux or `$env:OPENAI_API_KEY="yourkey"` on Windows
7. Finally `python manage.py runserver`

### Frontend
If you just want the latest fully built APK file, [you can download the latest one built by the CI pipeline here](https://stgit.dcs.gla.ac.uk/api/v4/projects/6006/jobs/artifacts/main/raw/frontend/main-app/app/build/outputs/apk/debug/app-debug.apk?job=android:build).

If you want to build the app without the ecosystem (much easier), you can skip to the [Android Studio section](#android-studio)

#### Unity
If you want to build with the ecosystem view enabled:
1. Download unity hub from https://unity.com/download
2. In the unity hub, after setting up an account, download unity version `2020.3.41f1` and check the `Android Build Support` option.
3. After setting up an account, open the `frontend/moxie-unity` folder.
4. When the editor opens, select `File->Build Settings->Android->Switch Platform`
5. Then, check `Export Project` and press the `Export` button. Export the project to the `frontend/unity-module` folder
6. Open a terminal in the `frontend` folder, and run `python convert_unity_module.py`, this will change the files in the exported project so that they are compatible with the app.

#### Android Studio
1. Download android studio from https://developer.android.com/studio
2. Open the `frontend/main-app` folder.
3. If you are building the app without unity, then ensure the configuration at the top of the editor is `app` and not `appUnity` and press the run button to start the app in the emulator. (if you have not installed an emulator, you may need to create one, android studio should prompt you to do so. Note: API levels >30 do not work)
4. If you want to build with unity
4.1. Change the current configuration from `app` to `appUnity`.
4.2. Open `Tools->Sdk Manager->Sdk Tools` and select `NDK (Side by side) 21.3.6528147` and `CMake`
4.3. Press the run button and the app should hopefully build without errors