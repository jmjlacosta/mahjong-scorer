# Mahjong Scorer

An Android app that uses computer vision to detect mahjong tiles from a photo and calculate the score for Beijing-style Mahjong.

## Features

- **Tile Detection**: Point your camera at a mahjong hand and the app identifies all 14 tiles
- **Automatic Scoring**: Calculates the score based on Beijing Mahjong rules
- **No Flowers**: Designed for Beijing-style play (34 tile types, no flower tiles)
- **Score Breakdown**: Shows exactly why you got your score

## Beijing Mahjong Rules

This app implements Beijing-style Mahjong scoring:

- **Winning Hand**: 14 tiles (no flowers)
- **Tile Types**: 34 unique tiles
  - Dots (筒子): 1-9
  - Bamboo (条子): 1-9
  - Characters (万子): 1-9
  - Winds (风牌): East (东), South (南), West (西), North (北)
  - Dragons (箭牌): Red (中), Green (发), White (白)

### Scoring Reference

| Pattern | Chinese | Points |
|---------|---------|--------|
| Basic Win | 基本胡 | 1 |
| Self-Draw | 自摸 | +1 |
| Concealed Hand | 门清 | +1 |
| Kong (each) | 杠 | +1 |
| All Pongs | 碰碰胡 | +2 |
| Half Flush | 混一色 | +3 |
| All Chows | 平胡 | +1 |
| Pure Hand | 清一色 | +6 |
| All Honors | 字一色 | +8 |
| Thirteen Orphans | 十三幺 | +13 |

---

## Implementation Plan

### Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| Min SDK | 24 (Android 7.0) |
| UI | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| Camera | CameraX |
| ML | Google ML Kit |
| Build | Gradle (Kotlin DSL) |

### Project Structure

```
mahjong-scorer/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/mahjongscorer/
│   │   │   ├── camera/           # CameraX integration
│   │   │   │   ├── CameraPreview.kt
│   │   │   │   └── ImageCapture.kt
│   │   │   │
│   │   │   ├── detection/        # ML Kit tile detection
│   │   │   │   ├── TileDetector.kt
│   │   │   │   ├── TileClassifier.kt
│   │   │   │   └── ImagePreprocessor.kt
│   │   │   │
│   │   │   ├── scoring/          # Beijing mahjong scoring
│   │   │   │   ├── ScoringEngine.kt
│   │   │   │   ├── HandValidator.kt
│   │   │   │   ├── PatternMatcher.kt
│   │   │   │   └── ScoreCalculator.kt
│   │   │   │
│   │   │   ├── model/            # Data classes
│   │   │   │   ├── Tile.kt
│   │   │   │   ├── Hand.kt
│   │   │   │   ├── Pattern.kt
│   │   │   │   └── Score.kt
│   │   │   │
│   │   │   ├── ui/               # Compose screens
│   │   │   │   ├── MainScreen.kt
│   │   │   │   ├── CameraScreen.kt
│   │   │   │   ├── ResultScreen.kt
│   │   │   │   └── components/
│   │   │   │
│   │   │   └── viewmodel/
│   │   │       └── MainViewModel.kt
│   │   │
│   │   └── res/
│   │       ├── drawable/         # Tile images for reference
│   │       └── values/
│   │
│   └── build.gradle.kts
│
├── ml/                           # ML model files
│   ├── tile_detector.tflite
│   └── labels.txt
│
├── training/                     # Model training scripts
│   ├── dataset/
│   ├── train.py
│   └── export.py
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### Core Components

#### 1. Camera Module

Uses CameraX for reliable camera access:

```kotlin
// Planned implementation
class CameraPreview {
    - Camera preview with viewfinder overlay
    - Tap to capture or auto-detect when hand is stable
    - Gallery picker for existing photos
}
```

#### 2. Tile Detection (ML Kit)

Custom object detection model for mahjong tiles:

```
Pipeline:
1. Capture/load image
2. Preprocess (resize, normalize)
3. Run ML Kit object detection
4. Classify each detected object into tile type
5. Return list of 14 detected tiles
```

**Model Training Plan:**
- **Dataset**: Need ~1000+ images per tile type
  - Synthetic generation from tile graphics
  - Real photos with various lighting/angles
  - Data augmentation (rotation, brightness, perspective)
- **Model**: TensorFlow Lite object detection
- **Classes**: 34 tile types
- **Output**: Bounding boxes + tile classification

#### 3. Scoring Engine

Implements Beijing Mahjong scoring rules:

```kotlin
// Planned implementation
class ScoringEngine {
    fun calculateScore(hand: Hand, context: WinContext): Score {
        // 1. Validate hand (must be 14 tiles)
        // 2. Find all possible interpretations
        // 3. Identify patterns (pongs, kongs, chows, pair)
        // 4. Check special hands
        // 5. Calculate total score
        // 6. Return breakdown
    }
}

data class WinContext(
    val isSelfDraw: Boolean,      // 自摸
    val isConcealed: Boolean,     // 门清
    val seatWind: Wind,
    val roundWind: Wind
)
```

#### 4. UI Screens

**Main Flow:**
```
[Camera Screen] → [Detection Overlay] → [Result Screen]
      ↓                   ↓                    ↓
  Viewfinder      Detected tiles        Score breakdown
  Capture btn     Confirm/edit          Pattern details
  Gallery btn     tile positions        Share score
```

### Development Phases

#### Phase 1: Core Scoring Engine
- [ ] Implement tile data models
- [ ] Build hand validation logic
- [ ] Implement pattern matching
- [ ] Create scoring calculator
- [ ] Unit tests for all scoring scenarios

#### Phase 2: Basic UI
- [ ] Set up Compose navigation
- [ ] Create camera preview screen
- [ ] Build result display screen
- [ ] Add manual tile input (for testing)

#### Phase 3: ML Model
- [ ] Collect/generate training dataset
- [ ] Train object detection model
- [ ] Export to TFLite format
- [ ] Integrate with ML Kit

#### Phase 4: Integration
- [ ] Connect camera → detection → scoring pipeline
- [ ] Add tile correction UI
- [ ] Implement confidence thresholds
- [ ] Handle edge cases

#### Phase 5: Polish
- [ ] Performance optimization
- [ ] Error handling
- [ ] Offline support
- [ ] UI/UX refinements

### Dependencies

```kotlin
// app/build.gradle.kts (planned)
dependencies {
    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")

    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // ML Kit
    implementation("com.google.mlkit:object-detection-custom:17.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android device with camera (API 24+)
- Trained ML model (see training/ directory)

### Build

```bash
# Clone the repo
git clone https://github.com/jmjlacosta/mahjong-scorer.git
cd mahjong-scorer

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Development

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

---

## Contributing

This project is in early development. Contributions welcome!

1. Fork the repo
2. Create a feature branch
3. Make your changes
4. Submit a PR

## License

MIT License - see LICENSE file for details
