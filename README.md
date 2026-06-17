<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# FlexBrowser - Full-Featured Android Browser

A modern, feature-rich Android browser built with Jetpack Compose and Material Design 3. FlexBrowser provides a complete browsing experience with customizable search engines, download management, bookmarks, and an intuitive user interface.

## ✨ Features

### 🌐 Core Browsing
- **Full WebView Integration** - Complete web browsing capabilities with JavaScript support
- **Navigation Controls** - Back, forward, and refresh buttons with smart state management
- **Progress Indicator** - Visual loading progress for page loads
- **Zoom Support** - Built-in zoom controls for better readability
- **Page Title Display** - Shows current page title in the interface

### 🔍 Search Engine Selection
Choose your preferred search engine from multiple options:
- **Google** (default)
- **Bing**
- **DuckDuckGo**
- **Yahoo**

The search engine preference is used when typing queries in the address bar.

### 📚 Bookmarks Management
- **Add/Remove Bookmarks** - Save your favorite pages with a single tap
- **Bookmarks Bottom Sheet** - Easy access to all saved bookmarks
- **Visual Indicators** - Filled bookmark icon for saved pages
- **Quick Access** - Bookmark button in the quick access toolbar

### 📜 History Tracking
- **Automatic History** - All visited pages are automatically tracked
- **History Bottom Sheet** - Browse and revisit past pages
- **Clear History** - One-tap option to clear all browsing history
- **Smart Navigation** - History integrates with back button handling

### ⬇️ Download Manager
- **Toggle Control** - Enable/disable downloads in settings
- **Download Icon** - Quick access toggle in the toolbar
- **System Integration** - Uses Android's DownloadManager for reliable downloads
- **Notification Support** - Get notified when downloads complete
- **File Detection** - Automatic detection of downloadable content

### 🎨 Modern UI/UX
- **Material Design 3** - Beautiful, modern interface following Material You guidelines
- **Flexible Header** - Rounded corners with status bar integration
- **Dropdown Menu** - Three-dot menu with all browser options
- **Quick Access Toolbar** - Frequently used features at your fingertips
- **Responsive Design** - Adapts to different screen sizes
- **Dark/Light Theme** - Supports system theme preferences

### ⚙️ Settings Panel
- **Downloads Toggle** - Enable or disable file downloads
- **Search Engine Selection** - Choose default search provider
- **Clean Interface** - Settings presented in a modal bottom sheet

## 🏗️ Architecture

Built with modern Android development best practices:

- **Jetpack Compose** - Declarative UI framework
- **Material 3 Components** - Latest Material Design components
- **State Management** - Compose state holders with `mutableStateOf` and `remember`
- **WebView Integration** - AndroidView for embedding WebView
- **MVVM-inspired** - Clean separation of UI and logic

## 🚀 Getting Started

### Prerequisites

- **Android Studio** - Arctic Fox or later recommended
- **Android SDK** - Minimum API level 21 (Android 5.0)
- **Kotlin** - Version 1.9+
- **Gradle** - Version 8.0+

### Installation Steps

1. **Clone or Download** the project

2. **Open in Android Studio**
   - Launch Android Studio
   - Select **File > Open**
   - Navigate to the project directory

3. **Configure API Key** (if using Gemini features)
   - Create a file named `.env` in the project root
   - Add your API key: `GEMINI_API_KEY=your_key_here`
   - See `.env.example` for reference

4. **Sync Gradle**
   - Android Studio will prompt to sync
   - Wait for dependencies to download

5. **Run the App**
   - Connect a device or start an emulator
   - Click the **Run** button
   - Or use `Ctrl+R` (Windows/Linux) / `Cmd+R` (Mac)

## 📱 Usage Guide

### Basic Browsing
1. Enter a URL or search query in the address bar
2. Press **Go** on the keyboard or tap the refresh button
3. Use **Back** and **Forward** buttons to navigate history
4. Tap **Refresh** to reload the current page

### Managing Bookmarks
1. Navigate to a page you want to save
2. Tap the **Bookmark icon** in the quick access toolbar
3. Access bookmarks via the menu or quick access button
4. Tap any bookmark to navigate to that page

### Changing Search Engine
1. Tap the **Menu button** (⋮) in the top right
2. Select **Settings** or **Search Engine**
3. Choose your preferred search engine from the dialog
4. Your selection is applied immediately

### Downloading Files
1. Ensure downloads are enabled in **Settings**
2. Navigate to a page with downloadable content
3. Tap on a download link
4. The file will be saved to your **Downloads** folder
5. Check the notification panel for download progress

### Clearing History
1. Open the **History** bottom sheet
2. Tap the **Close/Clear icon** in the header
3. Confirm to clear all browsing history

## 🛠️ Technical Details

### Dependencies

Key libraries used in this project:
- `androidx.activity:activity-compose` - Compose integration with Activity
- `androidx.compose.material3` - Material Design 3 components
- `androidx.compose.ui` - Core Compose UI functionality
- `androidx.webkit:webkit` - WebView support

### Project Structure

```
app/
├── src/main/
│   ├── java/com/example/
│   │   ├── MainActivity.kt          # Main activity entry point
│   │   └── ui/
│   │       ├── BrowserScreen.kt     # Main browser UI and logic
│   │       └── theme/               # Theme configuration
│   │           ├── Color.kt
│   │           ├── Theme.kt
│   │           └── Type.kt
│   ├── res/                         # Resources
│   │   ├── drawable/               # Icons and graphics
│   │   ├── values/                 # Colors, strings, themes
│   │   └── mipmap-anydpi-v26/      # Launcher icons
│   └── AndroidManifest.xml         # App manifest
├── build.gradle.kts                # App-level build configuration
└── proguard-rules.pro             # ProGuard rules
```

### Key Components

**BrowserApp()** - Main composable function containing:
- State management for URL, navigation, and features
- WebView configuration and lifecycle
- Bottom sheets for history, bookmarks, and settings
- Dropdown menu for additional options
- Search engine selection dialog

**processUrl()** - Intelligent URL processing:
- Detects valid URLs vs search queries
- Adds https:// prefix when needed
- Routes searches through selected engine

**downloadFile()** - Download handler:
- Integrates with Android DownloadManager
- Handles cookies and user agents
- Sets appropriate notifications

## 🎯 Roadmap

Future enhancements planned:
- [ ] Multiple tabs support
- [ ] Incognito/Private browsing mode
- [ ] Reader mode for articles
- [ ] Ad blocking capabilities
- [ ] Custom home page
- [ ] Sync across devices
- [ ] Password manager integration
- [ ] Reading list feature

## 📄 License

This project is provided as-is for educational and demonstration purposes.

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest features
- Submit pull requests
- Improve documentation

## 📞 Support

For issues or questions:
1. Check existing documentation
2. Review the code comments
3. File an issue on the repository

---

**Built with ❤️ using Jetpack Compose and Material Design 3**
