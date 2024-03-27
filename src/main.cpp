#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
//#include <TimeLib.h>

const char* ssid = "UNIFI_IDO2";
const char* password = "99Bidules!";
const char* serverAddress = "http://192.168.1.251:8080/data";
#define ButtonPin 5 //Define the button pin to 5

// Variable statique pour stocker l'ID
static int currentID = 8214;

void connectToWiFi() {
    Serial.println("Connecting to WiFi...");
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.println("Connecting to WiFi...");
    }
    Serial.println("Connected to WiFi");
}

void setup() {
    Serial.begin(9600);
    connectToWiFi();
    pinMode(ButtonPin, INPUT_PULLUP); // Set the button pin as input with pullup resistor
}

//Fonction pour les actuateurs
void sendDataActuatorToServer(int ID, const char* type, int value, const char* objectName, const char* sensorName, const char* sensorDescription) {
    Serial.println("Sending actuator data to server...");
    WiFiClient client;
    HTTPClient http;

    // Incrémente l'ID
    currentID++;

    // Construire l'objet JSON pour les données de l'actionneur
    DynamicJsonDocument jsonDocument(1024); // Taille du document JSON, ajustez si nécessaire
    jsonDocument["ID"] = ID;
    jsonDocument["Type de Mesure"] = type;
    jsonDocument["Valeur"] = value;
    jsonDocument["Nom Objet"] = objectName;
    jsonDocument["Nom capteur"] = sensorName;
    jsonDocument["Description Capteur"] = sensorDescription;

    // Sérialiser l'objet JSON en chaîne
    String jsonData;
    serializeJson(jsonDocument, jsonData);

    http.begin(client, serverAddress);
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(jsonData);

    if (httpResponseCode > 0) {
        Serial.print("Server response: ");
        Serial.println(http.getString());
    } else {
        Serial.print("HTTP error: ");
        Serial.println(httpResponseCode);
    }

    http.end();
}

//Fonction pour les capteurs
void sendDataSensorsToServer(int ID, const char* type, int value, const char* objectName, const char* sensorName, const char* sensorDescription, const char* status) {
    Serial.println("Sending data to server...");
    WiFiClient client;
    HTTPClient http;

    // Incrémente l'ID
    currentID++;

    // Construire l'objet JSON (adaptable pour chaque capteur)
    DynamicJsonDocument jsonDocument(1024); // Taille du document JSON, ajustez si nécessaire
    jsonDocument["ID"] = ID;
    jsonDocument["Type de Mesure"] = type;
    jsonDocument["Valeur"] = value;
    jsonDocument["Nom Objet"] = objectName;
    jsonDocument["Nom capteur"] = sensorName;
    jsonDocument["Description Capteur"] = sensorDescription;
    jsonDocument["status"] = status;

    // Sérialiser l'objet JSON en chaîne
    String jsonData;
    serializeJson(jsonDocument, jsonData);

    http.begin(client, serverAddress);
    http.addHeader("Content-Type", "application/json");

    int httpResponseCode = http.POST(jsonData);

    if (httpResponseCode > 0) {
        Serial.print("Server response: ");
        Serial.println(http.getString());
    } else {
        Serial.print("HTTP error: ");
        Serial.println(httpResponseCode);
    }

    http.end();
}

void loop() {
    int buttonState = digitalRead(ButtonPin); // Read the button state
    
    // Envoi des données au serveur
    sendDataSensorsToServer(currentID, "actuateur", buttonState, "esp32 upesy_wroom", "Boutton", "capteur de moisissure", "etat boutton");

    // Exemple d'utilisation de la fonction pour envoyer des données d'actionneur
    sendDataActuatorToServer(currentID, "actuateur", buttonState, "esp32(3)", "capteur Moisissure", "capteur de moisissure");


    delay(5000); // Attendre 5 secondes avant d'envoyer la prochaine série de données
}
