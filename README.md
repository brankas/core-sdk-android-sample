# core-sdk-android-sample

## Table of Contents

  1. [Minimum Requirements](#requirements)
  2. [Installation](#installation)
  3. [Notes](#notes)

***

## Minimum Requirements

1. **Android Studio Hedgehog 2023.1.1** but preferably the latest version
2. Minimum Android SDK: **API 21** or **Android 5.0**
3. Target SDK Version: **API 34** or **Android 14**

## Installation

Just checkout the source code and import using Android Studio. All of the necessary libraries will be downloaded automatically via Gradle.

## Notes
1. Do not forget to update the API Keys under **Constants** Class (These are provided by Brankas)
2. Update the Github Credentials under the **build.gradle** project file to enable the access and download of the SDK. Any username or password will be suffice. If the Github account to be used has Two Factor Authentication, use **personal access token** instead of the account password. Ensure that the **personal access token** has no expiration for the download of the SDK to proceed.
3. To run the app on a lower version of Android Studio, change the gradle version accordingly. Update the gradle version within **Project build.gradle**:

	````gradle
	dependencies {
    	classpath 'com.android.tools.build:gradle:8.2.0'
	}
	````

	and distribution URL within **gradle-wrapper.properties: distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip**

Refer to this link for the correct Gradle Version of the Android Studio being used: **https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility**
	