# GWeather - Weather Forecast App 

<img width="100" height="100" alt="app_icon" src="https://github.com/user-attachments/assets/0ebe72ba-9d01-4198-9f6b-dc26b67ff7af" />

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
- **Room** for database
- **Firebase** for authentication
- **Retrofit** for API calls

## Libraries Used

- Jetpack Compose
- Hilt
- Retrofit
- Coil
- DataStore
- Room Database
- Firebase
- Location Services
- JUnit & MockK for testing

## Screenshots
<img width="320" height="720" alt="SignIn" src="https://github.com/user-attachments/assets/73b9cd8d-7a07-4ad0-87ff-70a135abcdec" />
<img width="320" height="720" alt="SignUp" src="https://github.com/user-attachments/assets/50597570-0c34-4e11-962a-1e35a36659a4" />
<img width="320" height="720" alt="Home" src="https://github.com/user-attachments/assets/6b3cc8da-d0c7-4658-9f06-b10618bef683" />
<img width="320" height="720" alt="History" src="https://github.com/user-attachments/assets/5916cd04-4915-48a0-a8f2-2f2dc57c9456" />
<img width="320" height="720" alt="Permission" src="https://github.com/user-attachments/assets/a16ac0ec-e478-4403-9711-ba9b55b44d4f" />

## Acknowledgments

- OpenWeather for the weather data
- All open-source libraries used in this project
