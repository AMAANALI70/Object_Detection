<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/ML-TensorFlow%20Lite-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white" />
  <img src="https://img.shields.io/badge/Camera-CameraX-4285F4?style=for-the-badge&logo=google&logoColor=white" />
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-brightgreen?style=for-the-badge" />
</p>

# 📸 Real-Time Object Detection — Android App

A performant Android application that performs **real-time object detection** using the device camera. Built with **CameraX** for camera management and **TensorFlow Lite Task Vision API** for on-device ML inference using the **SSD MobileNet V1** model trained on the COCO dataset.

> **Detect 80+ object categories** — people, vehicles, animals, electronics, furniture, food, and more — entirely on-device with zero cloud dependency.

---

## 📋 Table of Contents

- [Features](#-features)
- [Demo](#-demo)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Usage](#-usage)
- [Configuration](#-configuration)
- [Model Details](#-model-details)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [Roadmap](#-roadmap)
- [License](#-license)
- [Author](#-author)

---

## ✨ Features

- 🎥 **Real-time camera preview** — Smooth live camera feed using CameraX with back camera support
- 🤖 **On-device ML inference** — TensorFlow Lite runs entirely on the device, no internet needed
- 🎯 **Multi-object detection** — Detects up to **10 objects simultaneously** in every frame
- 📦 **80+ object categories** — Trained on the COCO dataset (people, cars, animals, furniture, food, etc.)
- 🖼️ **Visual bounding boxes** — Color-coded rectangles with labels and confidence percentages drawn in real-time
- 🔄 **Automatic rotation handling** — Correctly processes camera frames regardless of device orientation
- ⚡ **Optimized pipeline** — Frame dropping strategy prevents lag; only the latest frame is processed
- 🔒 **Runtime permissions** — Camera permission is requested at runtime following Android best practices

---

## 📱 Demo

<p align="center">
  <em>Real-time object detection running on Xiaomi M2101K6P — detecting laptop (79%), books, bottle, and mouse.</em>
</p>

> **To see the app in action:** Build and install on any Android device with API 24+ (Android 7.0 Nougat or higher).

---

## 🛠️ Tech Stack

| Layer            | Technology                                                                                                       |
| ---------------- | ---------------------------------------------------------------------------------------------------------------- |
| **Language**     | Java 11                                                                                                          |
| **UI Framework** | Android XML Layouts                                                                                              |
| **Camera**       | [CameraX](https://developer.android.com/training/camerax) 1.4.1                                                  |
| **ML Runtime**   | [TensorFlow Lite](https://www.tensorflow.org/lite) 2.13.0                                                        |
| **ML API**       | [TFLite Task Vision](https://www.tensorflow.org/lite/inference_with_metadata/task_library/object_detector) 0.4.4 |
| **Model**        | SSD MobileNet V1 (COCO)                                                                                          |
| **Build System** | Gradle 8.11.1 + AGP 8.7.3                                                                                        |
| **Min SDK**      | API 24 (Android 7.0)                                                                                             |
| **Target SDK**   | API 35 (Android 15)                                                                                              |

---

## 📁 Project Structure

```
detection_model/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/detection_model/
│   │       │   ├── MainActivity.java         # Camera setup, model loading, detection pipeline
│   │       │   └── OverlayView.java          # Custom View for drawing bounding boxes
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml     # PreviewView + OverlayView layout
│   │       │   └── values/
│   │       │       ├── themes.xml            # AppCompat theme
│   │       │       ├── colors.xml            # Color definitions
│   │       │       └── strings.xml           # App name
│   │       ├── assets/
│   │       │   ├── model.tflite              # SSD MobileNet V1 model (4.3 MB)
│   │       │   └── labels.txt               # COCO class labels (80 categories)
│   │       └── AndroidManifest.xml           # Permissions & activity declaration
│   └── build.gradle.kts                      # App-level dependencies
├── gradle/
│   ├── libs.versions.toml                    # Version catalog
│   └── wrapper/
│       └── gradle-wrapper.properties         # Gradle wrapper config
├── build.gradle.kts                          # Root-level build config
├── settings.gradle.kts                       # Project settings
├── gradle.properties                         # Build properties
└── README.md
```

---

## 📦 Prerequisites

| Requirement                 | Version                              |
| --------------------------- | ------------------------------------ |
| **Android Studio**          | Ladybug (2024.2.1) or later          |
| **JDK**                     | 11 or higher                         |
| **Android SDK**             | API 35                               |
| **Android Device/Emulator** | API 24+ with camera                  |
| **Gradle**                  | 8.11.1 (auto-downloaded via wrapper) |

> ⚠️ **Note:** Object detection requires a **physical camera**. An emulator with a webcam passthrough will work, but a real device is recommended for best performance.

---

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/detection_model.git
cd detection_model
```

### 2. Open in Android Studio

- Open Android Studio
- Select **File → Open** and navigate to the cloned directory
- Wait for Gradle sync to complete

### 3. Verify Assets

Ensure these files exist in `app/src/main/assets/`:

```
assets/
├── model.tflite    # TensorFlow Lite model file
└── labels.txt      # COCO label names (80 classes)
```

### 4. Build the Project

```bash
# Using Gradle wrapper
./gradlew assembleDebug

# Windows
.\gradlew.bat assembleDebug
```

### 5. Run on Device

- Connect an Android device via USB (enable **USB Debugging**)
- Or configure an emulator with camera support
- Click **Run ▶️** in Android Studio or:

```bash
./gradlew installDebug
adb shell am start -n com.example.detection_model/.MainActivity
```

---

## 📖 Usage

### Running the App

1. **Launch** the app on your Android device
2. **Grant camera permission** when prompted
3. **Point your camera** at objects around you
4. **Bounding boxes** with labels and confidence scores appear in real-time

### Reading Detection Output

| Element               | Description                                     |
| --------------------- | ----------------------------------------------- |
| **Colored rectangle** | Bounding box around the detected object         |
| **Label**             | Object category name (e.g., "laptop", "person") |
| **Percentage**        | Model's confidence score (e.g., "79%")          |

### Logcat Debugging

Filter Logcat by tag `DETECTION_DEBUG` to see detailed pipeline output:

```
MODEL LOADED OK - threshold=0.3 maxResults=10
CAMERA: Bound to lifecycle OK
FRAME: 1920x1080 format=35 rotation=90 planes=3
BITMAP: 1920x1080 config=ARGB_8888
ROTATED: 1080x1920
DETECTIONS: 4
  >> laptop = 79.2% box=RectF(25.0, 310.0, 540.0, 850.0)
  >> book = 39.1% box=RectF(350.0, 280.0, 620.0, 540.0)
```

---

## ⚙️ Configuration

You can tune detection parameters in `MainActivity.java`:

```java
// Minimum confidence to display a detection (0.0 to 1.0)
private static final float CONFIDENCE_THRESHOLD = 0.3f;

// Maximum objects detected per frame
private static final int MAX_RESULTS = 10;
```

| Parameter              | Default | Description                                                            |
| ---------------------- | ------- | ---------------------------------------------------------------------- |
| `CONFIDENCE_THRESHOLD` | `0.3`   | Lower = more detections (more noise). Higher = fewer but more accurate |
| `MAX_RESULTS`          | `10`    | Max bounding boxes per frame. Increase for crowded scenes              |

**Recommended values:**

| Use Case            | Threshold | Max Results |
| ------------------- | --------- | ----------- |
| High precision      | `0.7`     | `3`         |
| Balanced            | `0.5`     | `5`         |
| Maximum detection   | `0.3`     | `10`        |
| Debug / exploration | `0.2`     | `15`        |

---

## 🧠 Model Details

| Property             | Value                                             |
| -------------------- | ------------------------------------------------- |
| **Architecture**     | SSD MobileNet V1                                  |
| **Training Dataset** | COCO (Common Objects in Context)                  |
| **Input Size**       | 300×300 pixels                                    |
| **Output**           | Bounding boxes + class labels + confidence scores |
| **Model Size**       | ~4.3 MB                                           |
| **Quantization**     | Float16                                           |
| **Metadata**         | TFLite Metadata V2 (labels embedded)              |
| **Classes**          | 80 categories                                     |

<details>
<summary><strong>📋 Full list of detectable objects (80 classes)</strong></summary>

```
person, bicycle, car, motorcycle, airplane, bus, train, truck, boat,
traffic light, fire hydrant, stop sign, parking meter, bench, bird,
cat, dog, horse, sheep, cow, elephant, bear, zebra, giraffe, backpack,
umbrella, handbag, tie, suitcase, frisbee, skis, snowboard, sports ball,
kite, baseball bat, baseball glove, skateboard, surfboard, tennis racket,
bottle, wine glass, cup, fork, knife, spoon, bowl, banana, apple,
sandwich, orange, broccoli, carrot, hot dog, pizza, donut, cake, chair,
couch, potted plant, bed, dining table, toilet, tv, laptop, mouse,
remote, keyboard, cell phone, microwave, oven, toaster, sink,
refrigerator, book, clock, vase, scissors, teddy bear, hair drier,
toothbrush
```

</details>

---

## 🧪 Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

### Manual Testing Checklist

- [ ] App installs without errors
- [ ] Camera permission prompt appears on first launch
- [ ] Camera preview displays correctly
- [ ] Bounding boxes appear when pointing at objects
- [ ] Labels and confidence percentages are readable
- [ ] App works in portrait and landscape
- [ ] No crash when switching apps and returning

---

## 🚢 Deployment

### Generate Signed APK

1. In Android Studio: **Build → Generate Signed Bundle/APK**
2. Select **APK**
3. Create or use an existing keystore
4. Choose **release** build variant
5. APK will be generated at `app/release/app-release.apk`

### Generate AAB (for Google Play)

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Guidelines

- Follow existing code style and naming conventions
- Test on a physical device before submitting
- Update documentation for any new features
- Keep PRs focused — one feature/fix per PR

---

## 🗺️ Roadmap

- [ ] **GPU acceleration** — Add TFLite GPU delegate with automatic CPU fallback
- [ ] **On-screen count** — Display total detected objects count
- [ ] **Screenshot capture** — Save detection results as images
- [ ] **Model switching** — Toggle between SSD MobileNet and EfficientDet-Lite at runtime
- [ ] **Custom model support** — Load user-provided `.tflite` models
- [ ] **Video recording** — Record camera feed with bounding box overlay
- [ ] **Detection history** — Log detected objects with timestamps
- [ ] **Night mode** — Enhanced detection in low-light conditions
- [ ] **Multi-camera support** — Switch between front and back cameras

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2026 Amaan Ali

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👤 Author

**Amaan Ali**

- GitHub: [@your-username](https://github.com/your-username)

---

<p align="center">
  <strong>⭐ Star this repo if you found it useful!</strong>
</p>
