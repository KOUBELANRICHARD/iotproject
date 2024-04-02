#include <MKRWAN.h>

LoRaModem modem;

#include "arduino_secrets.h"

// Vos données sensibles de l'application TTN
String appEui = SECRET_APP_EUI;
String appKey = SECRET_APP_KEY;

int waterSensorPin = A0; // Pin du capteur de niveau d'eau

void setup() {
 // Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
  //while (!Serial);

  // Initialise le modem LoRa
  if (!modem.begin(US915)) {
    Serial.println("Failed to start module");
    while (1) {}
  };
  Serial.print("Your module version is: ");
  Serial.println(modem.version());
  Serial.print("Your device EUI is: ");
  Serial.println(modem.deviceEUI());

  // Rejoint le réseau TTN
  int connected = modem.joinOTAA(appEui, appKey);
  if (!connected) {
    Serial.println("Something went wrong; are you indoor? Move near a window and retry");
    while (1) {}
  }

  modem.minPollInterval(60); // Intervalle de sondage minimal
}

void loop() {
  int sensorValue = analogRead(waterSensorPin); // Lit la valeur du capteur
  int mappedValue = map(sensorValue, 0, 1023, 0, 100); // Mappe de 0-1023 à 0-100
  
  Serial.print("Niveau d'eau (0-100): ");
  Serial.println(mappedValue);

  // Envoi de la valeur mappée sur le réseau LoRa
  modem.beginPacket();
  modem.print(mappedValue);
  int err = modem.endPacket(true);

  if (err > 0) {
    Serial.println("Message sent correctly!");
  } else {
    Serial.println("Error sending message");
  }

  delay(10000); // Attente avant la prochaine transmission
}
