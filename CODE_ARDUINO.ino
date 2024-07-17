#include <Wire.h>
#include <RTClib.h>
#include <SoftwareSerial.h>

SoftwareSerial BTSerial(10, 11); // RX, TX

RTC_DS3231 rtc;

const int pirPin = 2;
const int ldrPin = A0;
const int bulbRelayPin = 3;
const int mq135Pin = A1;
const int fanRelayPin = 4;
const int mq2Pin = 5;
const int buzzerPin = 6;

bool bulbState = false;
bool fanState = false;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  
  pinMode(pirPin, INPUT);
  pinMode(ldrPin, INPUT);
  pinMode(bulbRelayPin, OUTPUT);
  pinMode(fanRelayPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);

  if (!rtc.begin()) {
    Serial.println("Couldn't find RTC");
    while (1);
  }
  
  if (rtc.lostPower()) {
    Serial.println("RTC lost power, let's set the time!");
    rtc.adjust(DateTime(2024, 7, 17, 13, 55, 0)); // Adjust to compile time
  }
}

void loop() {
  DateTime now = rtc.now();
  
  Serial.print(now.year(), DEC);
  Serial.print('/');
  Serial.print(now.month(), DEC);
  Serial.print('/');
  Serial.print(now.day(), DEC);
  Serial.print(" ");
  Serial.print(now.hour(), DEC);
  Serial.print(':');
  Serial.print(now.minute(), DEC);
  Serial.print(':');
  Serial.print(now.second(), DEC);
  Serial.println();

  bool motionDetected = digitalRead(pirPin);
  int ldrValue = analogRead(ldrPin);
  int mq135Value = analogRead(mq135Pin);
  bool gasDetected = digitalRead(mq2Pin);
  bool isDark = ldrValue > 500; // Adjust threshold as needed

  Serial.print("PIR: ");
  Serial.println(motionDetected);
  Serial.print("LDR: ");
  Serial.println(ldrValue);
  Serial.print("MQ135: ");
  Serial.println(mq135Value);
  Serial.print("MQ2: ");
  Serial.println(gasDetected);

  // Control bulb based on conditions
  if (motionDetected && isDark) {
    digitalWrite(bulbRelayPin, HIGH);
    bulbState = true;
    BTSerial.println("Bulb ON");
  } else {
    digitalWrite(bulbRelayPin, LOW);
    bulbState = false;
    BTSerial.println("Bulb OFF");
  }

  // Turn off the bulb at specific time (e.g., 22:00)
  if (now.hour() == 22 && now.minute() == 0 && now.second() == 0) {
    digitalWrite(bulbRelayPin, LOW);
    bulbState = false;
    Serial.println("Bulb turned off at 22:00");
  }

  // Activate fan based on MQ135 sensor value
  if (mq135Value > 300) { 
    digitalWrite(fanRelayPin, HIGH);
    fanState = true;
    delay(20000); // Run fan for 20 seconds (adjust as needed)
    digitalWrite(fanRelayPin, LOW);
    fanState = false;
  }

  // Activate buzzer if gas is detected
  if (gasDetected) {
    digitalWrite(buzzerPin, HIGH);
  } else {
    digitalWrite(buzzerPin, LOW);
  }

  // Handle Bluetooth commands
  if (BTSerial.available()) {
    char command = BTSerial.read();
    if (command == 'B') {
      bulbState = !bulbState;
      digitalWrite(bulbRelayPin, bulbState ? HIGH : LOW);
      BTSerial.println(bulbState ? "Bulb ON" : "Bulb OFF");
    }
    if (command == 'F') {
      fanState = !fanState;
      digitalWrite(fanRelayPin, fanState ? HIGH : LOW);
      BTSerial.println(fanState ? "Fan ON" : "Fan OFF");
    }
    if (command == 'T') {
      int hour;
      int minute;
      // Read the hour and minute from Bluetooth message
      // Example: "T:22:30"
      sscanf(BTSerial.readString().c_str(), "T:%d:%d", &hour, &minute);
      // Set the sleep timer using RTC or delay function
      setSleepTimer(hour, minute);
    }
  }

  delay(1000); // Adjust delay as needed
}

void setSleepTimer(int hour, int minute) {
  // Example implementation using delay (adjust as per your needs)
  int sleepTime = (hour * 60 + minute) * 60; // Convert hours and minutes to seconds
  delay(sleepTime * 1000); // Convert seconds to milliseconds
  // Perform actions after sleep timer expires
  // For example, dimming the light or turning off devices
}