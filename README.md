# CrowdBeat Android Application

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Instruction](#instruction)


## Introduction
CrowdBeat is an Android application designed to streamline event management and user interaction. The app features a sleek UI theme, multi-activity architecture, and specific features for guest and host interactions.

## Features
- **Multiple Activities**:
  - **Player Page** (`.Player_Page`): The main activity for playing content.
  - **Guest Preferences** (`.Guest_Preference_Page`): Tailored settings for guest users.
  - **Host Main Page** (`.Host_Main_Page`): Primary interface for event hosts.
  - **Guest Join Event** (`.Guest_Join_Event_Page`): Interface for guests to join events.
  - **Host Create Event** (`.Host_Create_Event_Page`): Allows hosts to create new events.
  - **Main Activity** (`.MainActivity`): An additional main entry point.
  - **Guest Main Page** (`.Guest_Main_Page`): Core activity for guest users.
  - **Add Song Page** (`.Add_Song_Page`): Enables users to add songs to the playlist.
- **Custom Themes**: Incorporates `@style/Theme.CrowdBeat` for a modern aesthetic and `@style/LauncherTheme` for the launch screen.
- **App Icon and Labeling**: Uses `@mipmap/ic_launcher` for the app icon and `@string/app_name` for dynamic app labeling.

## Installation
1. Clone the repository to your local system:
   ```bash
   git clone https://github.com/your-repo/crowdbeat.git

## Instruction
1. drag the app-debug.apk into your android device emulator and enjoy your crowdbeat feature of host and guest. (The player page may not work in your emulator because you need Spotify authorization)
