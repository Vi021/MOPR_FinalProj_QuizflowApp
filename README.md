# Quizflow - Mobile Quiz App

## Overview
Quizflow is a mobile quiz application developed for Android that allows users to create, share, and play quizzes. The app provides both single-player and multiplayer quiz modes, making learning interactive and engaging.

## Features
- **User Authentication**: Sign up, sign in, and account management
- **Profile Management**: Edit profile, change password, view user details
- **Quiz Creation**: Create and edit quizzes with the built-in quiz editor
- **Quiz Participation**: Play quizzes in single-player mode
- **Multiplayer Mode**: Compete with other users in real-time
- **Topic-based Organization**: Quizzes organized by topics for easy discovery
- **Search Functionality**: Find quizzes and topics that interest you
- **Score Tracking**: View your performance after completing quizzes

## Technologies Used
- Java (Android SDK)
- Retrofit2 for API communication
- STOMP for WebSocket connections
- RxJava/RxAndroid for reactive programming
- Glide for image loading and caching
- Material Design components
- CircleImageView for circular profile images
- ChipNavBar for navigation

## Requirements
- Android 7.0 (API level 24) or higher
- Internet connection for multiplayer features and content synchronization

## Installation
1. Clone the repository
   ```
   git clone https://github.com/yourusername/MOPR_FinalProj_QuizflowApp.git
   ```
2. Open the project in Android Studio
3. Connect your device or use an emulator
4. Build and run the application

## Usage
1. Launch the app and sign in or create a new account
2. Browse available quizzes by topic or use the search function
3. Play quizzes in single-player mode or invite friends for multiplayer sessions
4. Create your own quizzes using the quiz editor
5. View your scores and track your progress

## Project Structure
- `activities/`: Contains all the app's activities (screens)
- `adapters/`: RecyclerView adapters for displaying lists
- `fragments/`: UI fragments for reusable components
- `models/`: Data models representing entities like quizzes and users
- `requests/`: API request models
- `respones/`: API response models
- `utils/`: Utility classes and helper functions

## Development
This project was developed as a final project for the Mobile Programming course (Semester II, 2024-2025) at HCMC University of Technology and Education.

## License
This project is licensed under the terms specified in the LICENSE file.
