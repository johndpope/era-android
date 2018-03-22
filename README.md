# RapidSOS Emergency Reference Application (ERA)

## Overview

| ![](https://s3.amazonaws.com/rapidsos-static-files/android-era/welcome_screen.png) | ![](https://s3.amazonaws.com/rapidsos-static-files/android-era/home_screen.png) |
|:---|:---|

The purpose of this reference application is to demonstrate the steps necessary to interface with each part of the RapidSOS Emergency APIs.

Specific RapidSOS APIs used to build ERA

* Emergency Flow API - [Midas](https://github.com/RapidSOS/era-android/tree/master/midas)
* Emergency Data SDK - [Emergency Data SDK](https://github.com/RapidSOS/emergency-data-sdk-android)

## Getting Started

To build and run the Emergency Reference App:

1. Clone the Emergency Reference App repository:
    ```sh
    > git clone https://github.com/RapidSOS/era-android.git
    ```
2. In the application level `build.gradle` file, change the application id and application secret to the ones provided from the [RapidSOS Emergency Console](https://rec.rapidsos.com/). On the bottom of the file change the return of `getEraClientID()` and `getEraClientSecret()` accordingly.
3. Build and Run the project.

# Guide

To better understand the project and how each piece is implemented, a designated module for Emergency Data SDK and Midas has been created.

This guide covers the following topics:

#### Table of contents
- [Client Keys](#client-id-and-client-secret)
- [Modules explained](#modules-explalined)
- [Emergency Data SDK](#emergency-data-sdk)
- [Midas](#midas)

### Client ID And Client Secret

Request a **Client ID** and **Client Secret** from the [RapidSOS Emergency Console](https://rec.rapidsos.com/). These are the credentials your client application will use to access the RapidSOS Emergency APIs.

Keep references to your **Client ID** and **Client Secret** somewhere in your code base. In the ERA, the **Client ID** and **Client Secret** are kept in the `build.gradle` file. Three things can be done.

1. Replace `${getEraClientID()}` and `${getEraClientSecret()}` with the Client ID and the Client Secret.
2. Return the Client ID and Client Secret from the `getEraClientID()` and `getEraClientSecret()` functions
3. **(Recommended)** Add the Client ID and Client Secret to your local gradle.properties directly, which won’t require any changes in the build.gradle files. To do this, run the following commands from the command line.

**Note: Replace `your_client_id` and `your_secret_key` accordingly.**

```shell
echo "ERA_CLIENT_ID=your_client_id"$'\r'"ERA_CLIENT_SECRET=your_secret_key" >> ~/.gradle/gradle.properties
```

![](https://s3.amazonaws.com/rapidsos-static-files/android-era/build_gradle_client_id_secret.png)

### Modules explained

#### Bluetooth
The Bluetooth module holds all of the logic and implementation used to connect to a bluetooth button to trigger a Midas Call Flow. This shows how it is done for a specific Bluetooth button we use for demonstrations. Your implementation may change depending on the bluetooth device used.

#### Database
The Database module holds all of our database related implementation components. This module holds our database, the data access objects, and database handlers. Room is the database used.

#### Shared
The Shared module holds code that is most likely shared among all of the other modules in the project.

#### Utils
The Utils module holds a set of Utility, Helper and Extension classes used throughout the project.

#### Wear
The Wear module shows how to utilize a wearable device/watch to trigger a Midas Call Flow.

## Emergency Data SDK
The Emergency Data API is an interface for provisioning emergency profile data for one of your applications users. This data will be made available to public safety officials and other trusted integration partners via data APIs made specifically for public safety partner usage during emergency scenarios only -- for example, if they call 9-1-1.

### Modifying profile information

 - To add/edit additional information in the ERA application, select the "EDIT PROFILE" button in the home screen.

![](https://s3.amazonaws.com/rapidsos-static-files/android-era/edit_profile_button_highlighted.png)

 - To add/edit emergency contacts, scroll to the bottom of the home screen and expand the “Emergency contacts” section, select the “Add contacts” button, to be taken to the emergency contact screen.

![](https://s3.amazonaws.com/rapidsos-static-files/android-era/emg_contacts_highlighted.png)

## Midas
The midas flows may vary depending on organization, but it is recommended that an application level
fail safe is implemented in case the midas flow fails for any reason, either in the app level or
backend level. The fail safe used in ERA is the following:

 - Trigger a midas flow
    - Start a local 2 minute timer
        - If a SUCCESS push notification was NOT received, open native dialer
        - If a SUCCESS push notification WAS received, cancel the local timer. Everything went well.

To see how this is performed, take a look at the following classes:
 - [MidasFlow](https://github.com/RapidSOS/era-android/blob/master/midas/src/main/java/com/rapidsos/midas/flow/MidasFLow.kt)
    - This is what triggers the Midas Call Flows and initiates the local FailSafe timer
 - The [FailSafeTimer](https://github.com/RapidSOS/era-android/blob/master/midas/src/main/java/com/rapidsos/midas/fail_safe/FailSafeTimer.kt)
    - A very simple timer
 - The [FCMService](https://github.com/RapidSOS/era-android/blob/master/app/src/main/java/com/rapidsos/era/midas/service/FCMService.kt)
    - The push notification listener

### Triggering a call flow

A Call Flow can be triggered in a number of ways:
 - A V-ALRT bluetooth button
 - From a wearable watch
 - Home screen widget
 - Lock screen/on-going notification

NOTE: In order to trigger a Call Fow properly, you must be signed in and your profile information filled out completely. To fill in your profile, open up the ERA application, from the home screen select “EDIT PROFILE“

#### Home screen widget

| ![](https://s3.amazonaws.com/rapidsos-static-files/android-era/home_screen_widget.png)  |  ![](https://s3.amazonaws.com/rapidsos-static-files/android-era/home_screen_widget_confirmation_dialog.png)  |
|:---|:---|

Let’s take a look at how to trigger a Call Flow from the home screen widget and the ongoing notification.

1. Open your device widget drawer.
2. Look for the ERA widget (Big red SOS button)
3. Place it somewhere on your home screen.

To trigger a Call Flow,  press the home screen widget and confirm you want to call 911.  Wait a few seconds, and pickup the incoming call.

#### Lock screen/on-going notification

Let’s take a look at how to trigger a Call Flow from the ongoing notification.

1. On your phone, navigate to the settings screen
  - Tap the 3 dots in the upper right corner
  - Select settings.
2. Enable the lock screen widget
3. Pull down the notification pane to access the ERA lock screen widget.

To trigger a Call Flow,  Tap the down arrow next to ERA, tap the panic button and confirm you want to call 911. Wait a few seconds, and pickup the incoming call.

| ![](https://s3.amazonaws.com/rapidsos-static-files/android-era/home_with_settings_menu.png) | ![](https://s3.amazonaws.com/rapidsos-static-files/android-era/settings_screen.png) | ![](https://s3.amazonaws.com/rapidsos-static-files/android-era/notification.png) |
|:--|:--|:---|
