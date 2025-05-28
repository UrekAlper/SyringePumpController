#include <Arduino.h>

// Pin Tanımlamaları
#define EN_PIN    18  // Stepper sürücü enable pini
#define DIR_PIN   16  // Stepper sürücü yön pini
#define STEP_PIN  17  // Stepper sürücü adım pini

// Kontrol Değişkenleri
bool pumpRunning = false;    // Pompa çalışıyor mu?
bool directionForward = true;  // Yön ileri mi?
int pumpSpeed = 50;          // Pompa hızı (0-100%)

// Zamanlama değişkenleri
unsigned long lastStepMillis = 0;
unsigned long stepInterval = 500;  // Başlangıç step aralığı

void setup() {
  // Seri haberleşmeyi başlat
  Serial.begin(115200);
  
  // Pinleri ayarla
  pinMode(STEP_PIN, OUTPUT);
  pinMode(DIR_PIN, OUTPUT);
  pinMode(EN_PIN, OUTPUT);
  
  // Motoru etkinleştir (LOW = etkin)
  digitalWrite(EN_PIN, LOW);
}

void loop() {
  // Seri port üzerinden gelen komutları kontrol et
  if (Serial.available() > 0) {
    String command = Serial.readStringUntil('\n');
    command.trim();
    processCommand(command);
  }
  
  // Eğer pompa çalışıyorsa
  if (pumpRunning) {
    // Step sinyali oluştur - eski koddaki gibi
    digitalWrite(STEP_PIN, !digitalRead(STEP_PIN));
    delayMicroseconds(stepInterval);
  } else {
    digitalWrite(STEP_PIN, LOW);
  }
}

// Gelen komutları işleme fonksiyonu
void processCommand(String command) {
  // START - Pompayı başlat
  if (command == "START") {
    pumpRunning = true;
  }
  
  // STOP - Pompayı durdur
  else if (command == "STOP") {
    pumpRunning = false;
  }
  
  // DIR_FWD - Yönü ileri ayarla
  else if (command == "DIR_FWD") {
    directionForward = true;
    digitalWrite(DIR_PIN, HIGH);
  }
  
  // DIR_REV - Yönü geri ayarla
  else if (command == "DIR_REV") {
    directionForward = false;
    digitalWrite(DIR_PIN, LOW);
  }
  
  // SPEED_XX - Hız ayarla (0-100)
  else if (command.startsWith("SPEED_")) {
    int newSpeed = command.substring(6).toInt();
    if (newSpeed >= 0 && newSpeed <= 100) {
      pumpSpeed = newSpeed;
      // Eski koddaki gibi hız aralığını kullan
      stepInterval = map(pumpSpeed, 0, 100, 800, 300);
    }
  }
}