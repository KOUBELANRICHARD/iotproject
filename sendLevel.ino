/*
  Lora Send And Receive
  This sketch demonstrates how to send and receive data with the MKR WAN 1300/1310 LoRa module.
  This example code is in the public domain.
*/

// Pour upload notre code de notre microcontroleur on doit le faire depuis Arduino IDE

#include <MKRWAN.h>

LoRaModem modem;

// Uncomment if using the Murata chip as a module
// LoRaModem modem(Serial1);

#include "arduino_secrets.h"

// Please enter your sensitive data correspondant au donne de mon Aplication sur ttn
String appEui = SECRET_APP_EUI ;
String appKey = SECRET_APP_KEY;

#define MAX_RANG (520) // la valeur maximale de mesure du module est de 520cm
#define ADC_SOLUTION (1023.0) // Précision ADC de l'Arduino est de 10 bits

int sensityPin = A0; // sélectionnez le pin d'entrée pour le capteur


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
  while (!Serial);
  // change this to your regional band (eg. US915, AS923, ...)
  if (!modem.begin(US915)) {
    Serial.println("Failed to start module");
    while (1) {}
  };
  Serial.print("Your module version is: ");
  Serial.println(modem.version());
  Serial.print("Your device EUI is: ");
  Serial.println(modem.deviceEUI());

  int connected = modem.joinOTAA(appEui, appKey);
  if (!connected) {
    Serial.println("Something went wrong; are you indoor? Move near a window and retry");
    while (1) {}
  }

  // Set poll interval to 60 secs.
  modem.minPollInterval(60);
  // NOTE: independent of this setting, the modem will
  // not allow sending more than one message every 2 minutes,
  // this is enforced by firmware and can not be changed.
}

void loop() {
   // Lecture de la valeur du capteur :
  float sensity_t = analogRead(sensityPin);
  // Conversion de la valeur lue en distance :
  float dist_t = sensity_t * MAX_RANG / ADC_SOLUTION;
  
  Serial.print(dist_t, 0);
  Serial.println(" cm");

  // caculons le niveau de notre eau ensachant que ous avonsun recipient de 7,5 cm

  float level = (7.5-dist_t);

  // Envoi de la distance sur le réseau LoRa :
  modem.beginPacket();
  modem.print(level);
  int err = modem.endPacket(true);

  if (err > 0) {
    Serial.println("Distance sent correctly!");
  } else {
    Serial.println("Error sending distance");
  }

  delay(10000); // Attendre avant la prochaine mesure
}
