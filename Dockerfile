# Dockerfile for the android build section of the pipeline
FROM openjdk:8-jdk

ENV ANDROID_NDK_VERSION="19.0.5232133"
ENV ANDROID_COMPILE_SDK="33"
ENV ANDROID_BUILD_TOOLS="30.0.3"
ENV ANDROID_SDK_TOOLS="9123335"
ENV ANDROID_HOME=/root/android
ENV ANDROID_EMULATOR_VERSION="system-images;android-30;google_apis;x86"

RUN apt-get update -y && apt-get upgrade -y
RUN apt-get install -y wget tar unzip lib32stdc++6 lib32z1 python3 python3-pip python3-venv pulseaudio libnss3-dev libgdk-pixbuf2.0-dev libgtk-3-dev libxss-dev
RUN mkdir -p /root/android
WORKDIR /root
RUN echo $ANDROID_SDK_TOOLS
# Download and extract the sdkmanager
RUN wget --output-document=$ANDROID_HOME/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip
WORKDIR $ANDROID_HOME
RUN unzip -d cmdline-tools cmdline-tools.zip
ENV PATH $PATH:${ANDROID_HOME}/cmdline-tools/cmdline-tools/bin/
# Confirm sdkmanager installed and download dependancies
RUN sdkmanager --version
RUN echo "Using NDK version - $ANDROID_NDK_VERSION"
RUN yes | sdkmanager --sdk_root=${ANDROID_HOME} --licenses || true # Accept all licenses
RUN sdkmanager --sdk_root=${ANDROID_HOME} "platforms;android-${ANDROID_COMPILE_SDK}"
RUN sdkmanager --sdk_root=${ANDROID_HOME} "platform-tools"
RUN sdkmanager --sdk_root=${ANDROID_HOME} "build-tools;${ANDROID_BUILD_TOOLS}"
RUN sdkmanager --sdk_root=${ANDROID_HOME} "ndk;${ANDROID_NDK_VERSION}"
RUN sdkmanager --sdk_root=${ANDROID_HOME} "cmake;3.22.1"
# Create emulator
RUN sdkmanager --sdk_root=${ANDROID_HOME} ${ANDROID_EMULATOR_VERSION}
RUN avdmanager --silent create avd -n moxie_emulator -k ${ANDROID_EMULATOR_VERSION} --device "pixel_6_pro"
ENV PATH=$PATH:${ANDROID_HOME}/emulator/:${ANDROID_HOME}/tools/