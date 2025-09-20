# GWeather - Weather Forecast App

A modern Android weather application that provides current weather information and weather history using the OpenWeather API.

## Features

- **User Authentication**
  - Secure registration and login
  - User session management

- **Current Weather**
  - Displays current weather conditions
  - Shows location (City and Country)
  - Current temperature in Celsius
  - Sunrise and sunset times
  - Weather-appropriate icons with day/night theming

- **Weather History**
  - View past weather data
  - Tracks weather changes over time

## Prerequisites

- Android Studio Giraffe or later
- Android SDK 24+
- Kotlin 1.8.0+
- An OpenWeather API key

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/itsmeRonjie/GWeather.git
   ```

2. Open the project in Android Studio

3. Get your OpenWeather API key:
   - Go to [OpenWeather](https://openweathermap.org/api) and sign up for a free API key

4. Add your API key:
   - Create a new file `local.properties` in the root directory if it doesn't exist
   - Add your API key:
     ```properties
     OPENWEATHER_API_KEY=your_api_key_here
     ```

## Architecture

- **MVVM** (Model-View-ViewModel) Architecture
- **Repository Pattern** for data management
- **Jetpack Compose** for modern UI
- **Hilt** for dependency injection
- **Retrofit** for API calls

## Libraries Used

- Jetpack Compose
- Hilt
- Retrofit
- Coil
- DataStore
- Location Services
- JUnit & MockK for testing

## Screenshots

[To Be Added]

## Acknowledgments

- OpenWeather for the weather data
- All open-source libraries used in this project
