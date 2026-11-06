# Syringe Pump Controller

**Pulseless, Low-Power, Open-Source Syringe Pump for Biotechnology Applications Powered by Smartphones**

Senior design project, Biomedical Engineering Design II (BME4496), Ankara University, Spring 2025. Supported by TÜBİTAK 2209-B.

A low-cost syringe pump that is controlled and powered from an Android phone. An ESP32 drives a stepper motor that pushes the syringe plunger along a threaded rod; the phone sends commands to the ESP32 over a wired USB connection.

This repository contains the Android app, the ESP32 firmware and the 3D models of the mechanical parts.

<!-- Photo of the assembled pump goes here, e.g.: ![Assembled pump](docs/pump.jpg) -->

## Features

- Start and stop the pump from the phone
- Forward and reverse direction (fill / dispense)
- Speed control from 0 to 100%, plus three preset speed buttons
- Run timer, with each run saved to a local database (date, duration, notes)
- Run history, measurement records and a graph view

## Repository structure

```
app/          Android app (Java)
ArduinoCode/  ESP32 firmware (Arduino)
3DModels/     STL / STEP files for the pump body, slider, syringe holder (5 mL), stepper coupler and motor/ESP32 case
```

## How it works

The app talks to the ESP32 over USB serial (115200 baud) using simple text commands, each ending with a newline:

| Command | Action |
|---|---|
| `START` | Start the pump |
| `STOP` | Stop the pump |
| `DIR_FWD` | Set direction forward |
| `DIR_REV` | Set direction reverse |
| `SPEED_XX` | Set speed, XX = 0–100 |

The firmware turns the speed value into a step interval and drives the stepper driver through its STEP (GPIO 17), DIR (GPIO 16) and EN (GPIO 18) pins.

## Getting started

**Firmware**
1. Open `ArduinoCode/sketch_may27b.ino` in the Arduino IDE with ESP32 board support installed.
2. Wire the stepper driver to the pins above and upload the sketch.

**Android app**
1. Open the project in Android Studio and build it (minimum Android 7.0, API 24).
2. Connect the phone to the ESP32 with a USB OTG cable and allow USB access when asked.
3. The app supports common USB-serial chips (CP210x, CH340, FTDI).

**Hardware**
Print the parts in `3DModels/`. The slider runs on an M5 × 80 mm fully threaded rod coupled to the stepper motor.

## Project status

This is a student prototype. The pump runs and can be controlled from the phone, but testing was limited and flow rate accuracy has not been validated. It is not intended for clinical use.

## Team

Two-person senior design team. Alper Ürek developed the software side: the Android app (written in Java with AI-assisted development) and its integration with the ESP32.
