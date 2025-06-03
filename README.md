# Mood Tracker App

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg" alt="Platform: Android">
  <img src="https://img.shields.io/badge/Language-Kotlin-orange.svg" alt="Language: Kotlin">
  <img src="https://img.shields.io/badge/Architecture-MVVM-blue.svg" alt="Architecture: MVVM">
</div>

## 📸 Preview

<div align="center">
  <img src="https://github.com/user-attachments/assets/06a621b9-66ef-435e-958b-1fe00a965451" width="350" alt="MoodTracker Preview">
</div>

## 🎨 About The Project

MoodTracker is a modern Android application designed to help users track and monitor their emotional states over time. The app provides a simple, intuitive interface for recording daily moods, identifying patterns, and gaining insights into emotional well-being. With customizable mood entries and comprehensive statistics, MoodTracker makes emotional self-awareness accessible and engaging.

## ✨ Key Features

- **📊 Daily Mood Logging**  
  Record your emotional state with customizable mood options and intensity levels
- **📝 Journal Entries**  
  Add detailed notes to each mood entry to capture thoughts and triggers
- **📈 Statistics & Trends**  
  Visualize your emotional patterns with intuitive charts and graphs
- **📱 Modern UI**  
  Clean, intuitive interface designed with Material Design principles

## 🛠️ Tech Stack

- **Kotlin** as the primary programming language
- **MVVM Architecture** for clean separation of concerns
- **GSON** for data serialization and persistence
- **ViewModel & LiveData** for reactive UI updates
- **Coroutines** for asynchronous operations
- **Material Design Components** for modern UI elements
- **Bottom Navigation** for intuitive app navigation
- **MPAndroidChart** for visualization of mood data
- **kizitonwose/Calendar** for calendar functionality

## 🚀 Getting Started

### Prerequisites

- Android Studio
- Android SDK
- JDK 11 or compatible version

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/whoisridze/MoodTracker.git
   cd MoodTracker
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository and open

3. **Build the project:**
   - Wait for Gradle sync to complete
   - Run `Build > Make Project`

4. **Run on emulator or physical device:**
   - Select your target device 
   - Click the Run button (▶️)

## 📂 Project Structure

```
MoodTracker/
├── app/
│   ├── manifests/
│   │   └── AndroidManifest.xml # App manifest
│   ├── kotlin+java/
│   │   └── com.whoisridze.moodtracker/
│   │       ├── data/
│   │       │   ├── local.gson/  # Local data persistence using GSON
│   │       │   └── repository/  # Data repositories implementation
│   │       ├── domain/
│   │       │   ├── model/       # Domain models
│   │       │   ├── repository/  # Repository interfaces
│   │       │   └── usecase/     # Business logic use cases
│   │       ├── ui/
│   │       │   ├── custom/      # Custom UI components
│   │       │   ├── dashboard/   # Dashboard/home screen
│   │       │   ├── settings/    # Settings screen
│   │       │   ├── social/      # Social sharing features
│   │       │   └── stats/       # Statistics and analytics screens
│   │       └── MainActivity.kt  # Main entry point
│   └── res/
│       ├── anim/               # Animation resources
│       ├── color/              # Color resources
│       ├── drawable/           # Drawable resources
│       ├── font/               # Font resources
│       ├── layout/             # Layout XML files
│       ├── menu/               # Menu XML files
│       ├── mipmap/             # App icon resources
│       ├── navigation/         # Navigation graph resources
│       ├── values/             # String, style, and other values
│       └── xml/                # Other XML resources
```

## 🤝 Contributing

Contributions are very welcome!

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m "Add YourFeature"`)
4. Push to your branch (`git push origin feature/YourFeature`)
5. Open a Pull Request
