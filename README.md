# ⚛️ Quantum Watermarking System

A Java-based digital watermarking system for **images and videos**, combining **LSB-based watermark embedding**, **simulated quantum computing concepts**, **JavaFX**, and **FFmpeg**.

The system allows users to embed invisible text watermarks into images and selected video frames and later extract the hidden watermark through a graphical user interface.

This project is developed as an **academic and research-oriented implementation** exploring the application of quantum-inspired concepts to digital watermarking.

---

## 📌 Overview

Digital watermarking is a technique used to embed hidden information into digital media for purposes such as:

- Copyright protection
- Ownership verification
- Content authentication
- Digital media identification

This project implements an experimental watermarking system supporting both **image and video watermarking**.

The image watermarking pipeline uses pixel-level LSB modification, while the video pipeline processes individual video frames using FFmpeg.

The project also contains simulated quantum computing components such as **qubits, complex numbers, quantum gates, and pixel registers** to explore how quantum-inspired representations can be incorporated into watermarking.

---

## ✨ Key Features

### 🖼️ Image Watermarking

- Open an image using the JavaFX interface
- Enter a text watermark
- Embed the watermark into the image
- Preview the original image
- Preview the watermarked image
- Save the watermarked image as PNG
- Extract the hidden watermark
- Detect invalid or missing watermarks

### 🎥 Video Watermarking

- Open MP4, MKV, AVI, and MOV videos
- Extract video frames using FFmpeg
- Embed the watermark into selected frames
- Configure the watermarking interval
- Reconstruct the watermarked video
- Extract the watermark from the processed video
- Use lossless video encoding to preserve pixel-level watermark data

### ⚛️ Quantum-Inspired Components

The project contains simulated implementations of:

- Qubits
- Complex numbers
- Quantum gates
- Pixel registers
- Quantum-inspired watermark processing

The quantum components are simulated using Java and do not require a physical quantum computer.

### 🖥️ Graphical User Interface

The project provides a JavaFX GUI with:

- Image watermarking controls
- Video watermarking controls
- Watermark input
- Frame interval selection
- Image preview
- Video processing status
- Progress indication
- Watermark extraction

---

# 🏗️ System Architecture

```text
                         ┌───────────────────────┐
                         │      JavaFX UI        │
                         │    WatermarkApp       │
                         └───────────┬───────────┘
                                     │
                    ┌────────────────┴────────────────┐
                    │                                 │
                    ▼                                 ▼
          ┌──────────────────┐              ┌──────────────────┐
          │ Image Processing │              │ Video Processing │
          │ ImageWatermarker │              │VideoWatermarker │
          └─────────┬────────┘              └─────────┬────────┘
                    │                                 │
                    ▼                                 ▼
          ┌──────────────────┐              ┌──────────────────┐
          │ Quantum-Inspired │              │      FFmpeg       │
          │ Components       │              │ Frame Processing │
          └──────────────────┘              └─────────┬────────┘
                                                       │
                                                       ▼
                                             ┌──────────────────┐
                                             │ Watermarked Video│
                                             └──────────────────┘
```

---

# 📂 Project Structure

```text
quantum-watermark/
│
├── src/
│   └── quantum/
│       ├── Complex.java
│       ├── Gates.java
│       ├── ImageWatermarker.java
│       ├── Main.java
│       ├── PixelRegister.java
│       ├── QuantumWatermarker.java
│       ├── Qubit.java
│       ├── Step2Test.java
│       ├── Step3Test.java
│       ├── Step4Test.java
│       ├── Step6Test.java
│       ├── VideoWatermarker.java
│       └── WatermarkApp.java
│
├── .gitignore
├── README.md
├── sample.mp4
└── watermarked_video.mkv
```

> Generated media files such as `sample.mp4` and `watermarked_video.mkv` may be excluded from the Git repository depending on the `.gitignore` configuration.

---

# 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| **Java** | Core application development |
| **JavaFX** | Graphical User Interface |
| **FFmpeg** | Video frame extraction and reconstruction |
| **LSB** | Pixel-level image watermark embedding |
| **Simulated Qubits** | Quantum-inspired processing |
| **Quantum Gates** | Quantum computation simulation |
| **Complex Numbers** | Quantum state representation |
| **IntelliJ IDEA** | Development environment |
| **PNG** | Lossless image storage |
| **FFV1** | Lossless video encoding |

---

# ⚙️ Requirements

Before running the project, install the following:

## 1. Java

JDK 26 or a compatible Java version.

Check the installation:

```bash
java -version
```

---

## 2. JavaFX

JavaFX SDK is required for the graphical interface.

Add the JavaFX SDK library to the IntelliJ IDEA project.

Example VM options:

```text
--module-path "PATH_TO_JAVAFX/lib" --add-modules javafx.controls
```

Replace:

```text
PATH_TO_JAVAFX
```

with the actual location of your JavaFX SDK.

For example:

```text
--module-path "D:\Program Files\javafx-sdk-26.0.2\lib" --add-modules javafx.controls
```

---

## 3. FFmpeg

FFmpeg is required for video watermarking.

Check the installation:

```bash
ffmpeg -version
```

If FFmpeg is correctly installed and added to the system PATH, the command should display the installed FFmpeg version.

---

# 🚀 How to Run

## 🖼️ Image Watermarking

1. Open the project in IntelliJ IDEA.
2. Configure the JDK and JavaFX SDK.
3. Set the JavaFX VM options.
4. Run:

```text
WatermarkApp.java
```

5. Click **Open Image**.
6. Select an image.
7. Enter the watermark text.
8. Click **Embed Watermark**.
9. Preview the original and watermarked images.
10. Click **Save as PNG**.
11. Use **Extract from Image** to verify the watermark.

Example:

```text
Original Image
      │
      ▼
Enter Watermark
      │
      ▼
Embed Watermark
      │
      ▼
Watermarked Image
      │
      ▼
Save as PNG
      │
      ▼
Extract Watermark
```

---

# 🎥 Video Watermarking

1. Start `WatermarkApp.java`.
2. Click **Open Video**.
3. Select a video file.
4. Enter the watermark text.
5. Select the watermarking interval.

For example:

```text
Every 5th frame
```

6. Click **Embed in Video**.
7. Wait for the processing to complete.
8. Save the generated video as `.mkv`.
9. Click **Extract from Video**.
10. Select the generated watermarked video.
11. Verify the extracted watermark.

---

# 🎬 Video Watermarking Pipeline

The video watermarking process follows these steps:

```text
                    Input Video
                         │
                         ▼
                ┌─────────────────┐
                │     FFmpeg      │
                │ Extract Frames  │
                └────────┬────────┘
                         │
                         ▼
              ┌─────────────────────┐
              │   Process Frames    │
              └─────────┬───────────┘
                        │
          ┌─────────────┼─────────────┐
          │             │             │
          ▼             ▼             ▼
       Frame 1       Frame 2       Frame 5
      Watermark        Skip        Watermark
          │             │             │
          └─────────────┼─────────────┘
                        │
                        ▼
               Watermarked Frames
                        │
                        ▼
                ┌───────────────┐
                │    FFmpeg     │
                │ Reconstruct   │
                │    Video      │
                └───────┬───────┘
                        │
                        ▼
             Lossless Watermarked
                    Video
```

---

# 🔢 Frame Interval

The application allows the user to select how frequently a watermark is embedded.

For example, with an interval of `5`:

```text
Frame 1   ✓ Watermark
Frame 2   ✗ Skip
Frame 3   ✗ Skip
Frame 4   ✗ Skip
Frame 5   ✓ Watermark
Frame 6   ✗ Skip
Frame 7   ✗ Skip
Frame 8   ✗ Skip
Frame 9   ✗ Skip
Frame 10  ✓ Watermark
```

Watermarking every Nth frame reduces processing compared with watermarking every frame while still providing repeated watermark information throughout the video.

---

# 🔐 Why Lossless Video?

The watermark is stored using **pixel-level information**.

LSB-based watermarking depends on the exact values of individual pixel channels.

Lossy codecs such as H.264 and H.265 can modify pixel values during compression. Even small changes may destroy the hidden watermark.

Therefore, the project uses a **lossless video workflow** for the watermarked output.

```text
Original Video
      │
      ▼
Extract Frames
      │
      ▼
Modify Pixel LSBs
      │
      ▼
Watermarked Frames
      │
      ▼
Reconstruct using
Lossless Codec
      │
      ▼
Watermarked Video
```

The resulting `.mkv` file can be significantly larger than a normally compressed video because the pixel information is preserved.

---

# ⚛️ Quantum-Inspired Approach

The project explores the use of quantum computing concepts in digital watermarking.

The implementation contains simulated representations of:

### Qubits

Qubits are represented using Java classes to model quantum states.

### Complex Numbers

Complex numbers are used as part of the mathematical representation of quantum states.

### Quantum Gates

Quantum gates are simulated to represent transformations of quantum states.

### Pixel Registers

Pixel information is represented through register-like structures for experimentation with quantum-inspired image processing.

The overall concept can be represented as:

```text
Image Pixel
     │
     ▼
Pixel Representation
     │
     ▼
Quantum-Inspired Register
     │
     ▼
Simulated Quantum Operation
     │
     ▼
Watermark Processing
     │
     ▼
Watermarked Pixel
```

The project does **not require a physical quantum computer**.

The quantum components are simulated using Java for academic and experimental purposes.

---

# 🧪 Testing

The project includes multiple test classes for validating different stages of development.

## 🖼️ Image Tests

The image watermarking pipeline verifies:

- Watermark embedding
- Watermark extraction
- Invalid watermark detection
- Output image generation

---

## 🎥 Video Tests

`Step6Test.java` performs an end-to-end video watermarking test.

The test performs:

```text
1. Create/sample input video
2. Extract video frames
3. Watermark selected frames
4. Reconstruct lossless video
5. Extract watermark
6. Compress/test using a lossy video format
7. Attempt watermark extraction again
```

Example successful output:

```text
Found 150 frames.
Watermarking every 5-th frame...

Watermarked 10 frames...
Watermarked 20 frames...
Watermarked 30 frames...

Total frames : 150
Marked frames: 30

Watermark found in frame 0.
Extracted: "hy i am antra sharma"
```

The lossy compression test demonstrates an important limitation:

```text
Extraction FAILED: No valid watermark found
```

This demonstrates that the current LSB-based watermark is sensitive to lossy video compression.

---

# 📊 Example Test Results

The video testing pipeline successfully demonstrated:

| Test | Result |
|---|---|
| Video frame extraction | ✅ Passed |
| Watermark selected frames | ✅ Passed |
| Lossless video reconstruction | ✅ Passed |
| Watermark extraction | ✅ Passed |
| Extraction from original video | ✅ Correctly rejected |
| Lossy compression extraction | ⚠️ Watermark not preserved |

Example:

```text
Total frames : 150
Marked frames: 30
Time taken   : 1.543 s

Watermark found in frame 0.
Extracted: "hy i am antra sharma"
```

---

# ⚠️ Limitations

The current implementation has several limitations:

- LSB watermarking is sensitive to lossy compression.
- Watermarked videos using lossless codecs can be large.
- Processing time increases with video resolution and duration.
- The current watermark is primarily intended for academic and experimental purposes.
- The system does not currently provide cryptographic-grade copyright authentication.
- Heavy editing, resizing, filtering, or transcoding may destroy the watermark.
- The current implementation does not guarantee robustness against intentional watermark-removal attacks.

For example:

```text
Watermarked MKV
      │
      ├── Lossless copy
      │       │
      │       └──► Watermark likely preserved
      │
      └── H.264 / Online Compression
              │
              └──► Watermark may be destroyed
```

---

# 🔮 Future Improvements

Possible future extensions include:

- Robust watermarking using DCT/DWT instead of only LSB
- Cryptographic encryption of watermark information
- Error-correcting codes
- Improved watermark synchronization
- Watermark resistance against resizing and filtering
- Audio watermarking
- Real-time video watermarking
- GPU acceleration
- Integration with actual quantum computing frameworks
- Evaluation using PSNR
- Evaluation using SSIM
- Bit Error Rate (BER) analysis
- Robustness testing against common attacks
- Improved security against watermark removal attacks

---

# 📈 Evaluation Metrics for Future Work

To further evaluate the quality and robustness of the watermarking system, the following metrics can be incorporated:

### PSNR

Peak Signal-to-Noise Ratio can measure the visual quality difference between the original and watermarked media.

### SSIM

Structural Similarity Index can measure structural similarity between the original and watermarked image/video frames.

### BER

Bit Error Rate can measure how accurately the embedded watermark can be recovered after processing or attacks.

These metrics can help evaluate the trade-off between:

```text
Imperceptibility
       ↕
Robustness
       ↕
Embedding Capacity
```

---

# 📊 Current Project Status

| Component | Status |
|---|---|
| Java project setup | ✅ Complete |
| Quantum-inspired classes | ✅ Implemented |
| Qubit representation | ✅ Implemented |
| Complex number operations | ✅ Implemented |
| Quantum gate simulation | ✅ Implemented |
| Pixel register | ✅ Implemented |
| Image watermark embedding | ✅ Working |
| Image watermark extraction | ✅ Working |
| JavaFX image UI | ✅ Working |
| FFmpeg setup | ✅ Complete |
| Video frame extraction | ✅ Working |
| Video watermark embedding | ✅ Working |
| Lossless video reconstruction | ✅ Working |
| Video watermark extraction | ✅ Working |
| Lossy compression test | ✅ Tested |
| JavaFX video UI | ✅ Implemented |
| Robust watermarking | 🔄 Future work |
| Quantitative robustness evaluation | 🔄 Future work |

---

# 🖥️ Application Workflow

The complete application workflow is:

```text
                 START
                   │
                   ▼
            Open JavaFX App
                   │
          ┌────────┴────────┐
          │                 │
          ▼                 ▼
      IMAGE MODE        VIDEO MODE
          │                 │
          ▼                 ▼
     Open Image         Open Video
          │                 │
          ▼                 ▼
 Enter Watermark      Enter Watermark
          │                 │
          ▼                 ▼
 Embed Watermark     Select Frame Interval
          │                 │
          ▼                 ▼
 Preview Image       Embed in Video
          │                 │
          ▼                 ▼
 Save PNG            Save Lossless Video
          │                 │
          ▼                 ▼
 Extract Watermark   Extract Watermark
          │                 │
          └────────┬────────┘
                   ▼
               VERIFY
                   │
                   ▼
                  END
```

---

# 💡 Research Motivation

Traditional LSB watermarking provides a simple and efficient way to hide information inside digital media, but it has limited robustness against compression and other transformations.

This project explores whether **quantum-inspired representations and processing concepts** can provide a foundation for developing more advanced watermarking techniques.

The current implementation establishes the basic watermarking pipeline and provides a platform for future research into:

- Quantum-inspired watermarking
- Robust multimedia watermarking
- Secure watermark representation
- Error correction
- Hybrid classical-quantum approaches

---

# 🎓 Academic Purpose

This project is developed as an academic project to study the intersection of:

```text
Digital Watermarking
        +
Image Processing
        +
Video Processing
        +
Quantum Computing Concepts
        +
Java Application Development
```

It demonstrates the practical implementation of watermark embedding and extraction while providing a foundation for further research and experimentation.

---

# 👩‍💻 Author

**Antra Sharma**

MCA Student  
**Vellore Institute of Technology (VIT)**

---

# 📄 Disclaimer

This project is developed for **academic and research purposes**.

The current implementation demonstrates a quantum-inspired watermarking concept and should not be considered a production-grade copyright protection system.

The watermarking method is currently sensitive to lossy compression and other media transformations. Further research and robustness evaluation are required before considering the system for real-world copyright protection.

---

## ⭐ Project Highlights

```text
✓ Image Watermarking
✓ Video Watermarking
✓ LSB-Based Embedding
✓ Quantum-Inspired Components
✓ Simulated Qubits
✓ Quantum Gates
✓ JavaFX GUI
✓ FFmpeg Video Processing
✓ Lossless Video Reconstruction
✓ Watermark Extraction
✓ Compression Robustness Testing
```