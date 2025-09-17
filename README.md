# Android Demo Task

A modern Android application built with Jetpack Compose, Clean Architecture, and Material Design 3. The app features three main tabs: Home (news feed), Messages (local chat simulation), and Profile (user profile management).

## Features

### 🏠 Home Tab
- **News API Integration**: Fetches top headlines from NewsAPI.org
- **Search Functionality**: Search news articles with real-time filtering
- **Featured Section**: Horizontal scrollable featured articles
- **Latest News**: Vertical list of news articles
- **Offline Support**: Cached data displayed when offline
- **Pull-to-Refresh**: Refresh news content
- **Pagination**: Load more articles as needed

### 💬 Messages Tab
- **Local Chat Simulation**: Fully offline chat functionality
- **Message Types**: Support for text and image messages
- **Chat Bubbles**: Different styles for sent/received messages
- **Image Sharing**: Pick images from gallery and send
- **Auto-scroll**: Automatically scroll to latest messages
- **Timestamps**: Display message timestamps
- **Simulate Messages**: Button to simulate received messages

### 👤 Profile Tab
- **Profile Management**: Display and update user profile
- **Camera Integration**: Take photos directly from the app
- **Gallery Access**: Select existing photos from gallery
- **Location Services**: Get and display current location
- **Permission Handling**: Proper Android SDK 6-15 permission management
- **Image Display**: Show profile image with fallback

## Tech Stack

- **Language**: Kotlin
- **Architecture**: Clean Architecture (Data, Domain, Presentation layers)
- **UI**: Jetpack Compose with Material Design 3
- **Navigation**: Navigation Compose
- **Networking**: Retrofit + Coroutines/Flow
- **Dependency Injection**: Hilt
- **Local Storage**: Room Database + DataStore
- **Background Work**: WorkManager
- **Image Loading**: Coil
- **Permissions**: Accompanist Permissions
- **Location**: Google Play Services Location
- **Camera**: CameraX
- **Testing**: JUnit, Mockito, Coroutines Test

## Project Structure

```
app/src/main/java/com/example/androiddemotask/
├── data/
│   ├── local/           # Room database and DAOs
│   ├── model/           # Data models
│   ├── network/         # Connectivity manager
│   ├── remote/          # API services
│   └── repository/      # Repository implementations
├── domain/
│   └── usecase/         # Use cases
├── presentation/
│   ├── home/            # Home screen and ViewModel
│   ├── messages/        # Messages screen and ViewModel
│   ├── profile/         # Profile screen and ViewModel
│   └── navigation/      # Navigation components
├── di/                  # Hilt modules
└── ui/theme/            # Theme and styling
```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.7.20+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AndroidDemotask
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project folder

3. **Configure API Key**
   - Get a free API key from [NewsAPI.org](https://newsapi.org/)
   - Replace `YOUR_API_KEY_HERE` in `HomeViewModel.kt` with your actual API key

4. **Build and Run**
   - Sync project with Gradle files
   - Build the project (Build → Make Project)
   - Run on device or emulator

### API Configuration

The app uses NewsAPI.org for fetching news articles. To get your API key:

1. Visit [https://newsapi.org/](https://newsapi.org/)
2. Sign up for a free account
3. Get your API key from the dashboard
4. Replace the placeholder in `HomeViewModel.kt`:

```kotlin
// In HomeViewModel.kt
getTopHeadlinesUseCase("YOUR_ACTUAL_API_KEY_HERE")
searchNewsUseCase(query, "YOUR_ACTUAL_API_KEY_HERE")
```

## API Endpoints Used

### NewsAPI.org
- **Base URL**: `https://newsapi.org/`
- **Top Headlines**: `GET /v2/top-headlines?country=us&apiKey={API_KEY}`
- **Search**: `GET /v2/everything?q={query}&apiKey={API_KEY}`

## Permissions Required

The app requires the following permissions:

- `INTERNET` - For API calls
- `ACCESS_NETWORK_STATE` - For connectivity monitoring
- `CAMERA` - For taking profile pictures
- `READ_EXTERNAL_STORAGE` - For accessing gallery (Android < 13)
- `READ_MEDIA_IMAGES` - For accessing gallery (Android 13+)
- `ACCESS_FINE_LOCATION` - For getting precise location
- `ACCESS_COARSE_LOCATION` - For getting approximate location

## Testing

### Unit Tests
Run unit tests using:
```bash
./gradlew test
```

### Test Coverage
- ViewModel layer tests
- Repository layer tests
- Use case tests
- Mockito for mocking dependencies
- Coroutines test for async operations

## Architecture Details

### Clean Architecture
The app follows Clean Architecture principles with clear separation of concerns:

- **Presentation Layer**: UI components, ViewModels, and navigation
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories, data sources, and models

### Dependency Injection
Hilt is used for dependency injection with modules for:
- Database (Room)
- Network (Retrofit)
- Repository bindings

### State Management
- StateFlow for reactive state management
- Compose state collection with `collectAsStateWithLifecycle`
- Proper lifecycle-aware state handling

## Features Implementation

### Offline Support
- Room database for local caching
- Connectivity manager for network state monitoring
- Graceful fallback to cached data when offline

### Theme Support
- Light/Dark theme support
- Material Design 3 color schemes
- Dynamic color support (Android 12+)

### Animations
- Custom fragment transition animations
- Smooth navigation transitions
- Loading states and progress indicators

## Troubleshooting

### Common Issues

1. **Build Errors**
   - Ensure all dependencies are properly synced
   - Check that API key is correctly configured

2. **Permission Issues**
   - Grant required permissions when prompted
   - Check device settings for permission status

3. **Network Issues**
   - Verify internet connectivity
   - Check API key validity
   - Review network security policies

### Debug Mode
Enable debug logging by setting log level in `NetworkModule.kt`:
```kotlin
level = HttpLoggingInterceptor.Level.BODY
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- NewsAPI.org for providing the news API
- Android team for Jetpack Compose
- Material Design team for design guidelines
- Open source community for various libraries used
