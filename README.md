# BLUETOMETA – Bluetooth Integrated Home Automation

## Description
BLUETOMETA is an IoT-based home automation system that integrates an Android app with an Arduino over Bluetooth.  
It allows users to **control home appliances, monitor sensors in real-time, and receive alerts**, providing enhanced **security, safety, and energy efficiency**.

## Features
- **PIR Motion Sensor** → Automatically turns lights on when motion is detected.  
- **LDR Sensor** → Adjusts brightness of lights based on ambient light.  
- **MQ-2 Gas Sensor** → Detects gas leaks and triggers real-time alerts.  
- **MQ-135 Air Quality Sensor** → Monitors oxygen levels for safer environments.  
- **Android App Control** → Login/signup, room & kitchen appliance control, timers, and real-time notifications.  

## Tech Stack
- **Hardware:** Arduino Uno, PIR, LDR, MQ-2, MQ-135 sensors, Relay, Buzzer  
- **Software:** Android Studio (Java), SQLite, Bluetooth SPP (Serial Port Profile)  

## How It Works (Code Flow)
1. User opens the app → Login/Signup using SQLite database.  
2. Connects the app to Arduino via Bluetooth.  
3. **ConnectedThread.java** manages communication in the background:  
   - Sends commands (e.g., `"LIGHT_ON"`) to Arduino.  
   - Receives sensor alerts (e.g., `"GAS_ALERT"`) in real-time.  
4. User controls appliances via Room/Kitchen screens.  
5. Arduino reads sensor data → triggers hardware actions (lights/buzzer) and sends updates back to the app.  
6. Timer feature automatically turns off devices when required.  

## Usage
1. Sign up or log in on the Android app.  
2. Connect via Bluetooth to the Arduino device.  
3. Control appliances (lights, buzzer) or monitor sensor data in real-time.  
4. Receive instant alerts for motion detection, gas leaks, or air quality issues.

## Screenshots

### Components Used
![Components](https://github.com/user-attachments/assets/408adbfb-8cfa-477d-981f-90ee333e0722)

### Fig-1: App Launch / Splash Screen
![Fig-1: Splash Screen](https://github.com/user-attachments/assets/31d3c47f-762c-43c3-8f5d-91d13854aa10)

### Fig-2: Login Screen
![Fig-2: Login Screen](https://github.com/user-attachments/assets/dbc62e49-e46c-4d4a-8967-56719e7f0d01)

### Fig-3: Signup Screen
![Fig-3: Signup Screen](https://github.com/user-attachments/assets/8843d42b-f2f0-4936-b76c-4d25616e7ef5)

### Fig-4: Main Dashboard / Bluetooth Status
![Fig-4: Main Dashboard](https://github.com/user-attachments/assets/e8b7c231-7ecc-444b-aa9a-0eef9d5ffae6)

### Fig-5: Room Control Screen
![Fig-5: Room Control](https://github.com/user-attachments/assets/645bb416-fdd7-4efd-b2e7-15dd60f32933)

### Fig-6: Kitchen Control Screen
![Fig-6: Kitchen Control](https://github.com/user-attachments/assets/833e4799-b65b-4dad-990c-b1d0c2a5f17a)

### Fig-7: Real-time Sensor Alert (e.g., Gas Alert)
![Fig-7: Sensor Alert](https://github.com/user-attachments/assets/e19f0648-2c17-4062-8b81-59b4ebb0c505)

## Future Enhancements
- Cloud/Wi-Fi integration for remote control.  
- AI-based automation to predict user behavior and optimize appliance usage.  
- Voice assistant support (Alexa / Google Assistant).  
- Support for multiple Arduino endpoints for a fully automated smart home.

## Learning Outcomes
- Hands-on experience with **IoT systems** combining hardware and mobile apps.  
- Practical knowledge of **Bluetooth SPP communication** and real-time data handling.  
- Understanding of **Arduino sensor integration** and controlling actuators.  
- Android app development with **SQLite database integration**.  
