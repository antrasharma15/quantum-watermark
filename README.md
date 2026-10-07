# Quantum Watermarking System

A Java-based image and video watermarking system that explores a quantum-inspired approach to digital watermarking using simulated qubit and quantum-gate concepts, combined with Least Significant Bit (LSB) based watermark embedding.

The system provides a JavaFX graphical interface for embedding and extracting invisible text watermarks from images and videos.

---

## 📌 Overview

Digital watermarking is a technique used to embed hidden information into digital media for purposes such as copyright protection, ownership verification, and content authentication.

This project implements an experimental watermarking system with:

- Image watermark embedding and extraction
- Video watermark embedding and extraction
- Simulated quantum concepts using qubits and quantum gates
- LSB-based image watermarking
- JavaFX graphical user interface
- FFmpeg-based video frame processing
- Lossless video reconstruction
- Watermark extraction and validation

The project demonstrates how concepts from **digital watermarking, image processing, quantum computing, and multimedia processing** can be combined into a single application.

---

## ✨ Features

### 🖼️ Image Watermarking

- Open an image through the JavaFX interface
- Enter a text watermark
- Embed the watermark into the image
- Preview the original and watermarked images
- Save the watermarked image as PNG
- Extract the hidden watermark
- Detect invalid or missing watermarks

### 🎥 Video Watermarking

- Open MP4/MKV/AVI/MOV videos
- Extract video frames using FFmpeg
- Embed the watermark into selected frames
- Configure the watermarking interval
- Reconstruct the watermarked video
- Extract the watermark from the processed video
- Preserve the watermark using a lossless video codec

### ⚛️ Quantum-Inspired Components

The project includes simulated quantum components such as:

- Qubit representation
- Complex numbers
- Quantum gates
- Pixel/register representation
- Quantum-inspired watermark processing

These components are implemented in Java for experimentation and educational purposes rather than relying on a physical quantum computer.

---

## 🏗️ Project Architecture

```text
                    ┌─────────────────────┐
                    │    JavaFX UI        │
                    │   WatermarkApp      │
                    └──────────┬──────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ▼                             ▼
       ┌─────────────────┐          ┌──────────────────┐
       │ Image Processing│          │ Video Processing │
       │ ImageWatermarker│          │VideoWatermarker │
       └────────┬────────┘          └─────────┬────────┘
                │                             │
                ▼                             ▼
       ┌─────────────────┐          ┌──────────────────┐
       │ Quantum-Inspired│          │      FFmpeg       │
       │ Components      │          │ Frame Processing │
       └─────────────────┘          └─────────┬────────┘
                                              │
                                              ▼
                                      Watermarked Video