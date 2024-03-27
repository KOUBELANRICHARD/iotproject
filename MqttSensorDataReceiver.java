

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//import java.net.HttpURLConnection;
import java.net.URL;
//import java.nio.charset.StandardCharsets;

public class MqttSensorDataReceiver {
            
   

    private static void insertDistanceIntoDatabase(Connection connection, double distance, String Name)  throws SQLException {

        int sensorId = findSensorIdByName(Name);
        String sensorname = "levelWater";
        //System.out.println(sensorId);
        
        String sql = "INSERT INTO Mesures (capteur_id,valeur, nom, actif) VALUES (?, ?, ?, TRUE)"; // Assurez-vous que cela correspond à la structure de votre table

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, sensorId);
            
            preparedStatement.setDouble(2, distance);

            preparedStatement.setString(3, sensorname);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("La distance a été insérée avec succès : " + distance);
            } else {
                System.out.println("Aucune ligne affectée, insertion échouée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion de la distance dans la base de données: " + e.getMessage());
        }
    }

    private static int findSensorIdByName(String name) throws SQLException {

        Connection connection = Connectdb.getConnection();
        String sql = "SELECT capteur_id FROM Capteurs WHERE nom = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capteur_id");
                   
                }
            }
        }
        return -1;  // -1 signifie que le capteur n'a pas été trouvé
    }

    
        

    public static void main(String[] args) {

        String serverURI = "tcp://nam1.cloud.thethings.network:1883"; 
        String clientId = MqttClient.generateClientId();
        String topic = "v3/my-lora-testapp@ttn/devices/eui-a8610a32301a7818/#"; 
        Connection connection = Connectdb.getConnection(); // class connectio n propre a mon projet


        try {
            MqttClient client = new MqttClient(serverURI, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("my-lora-testapp@ttn"); // Remplacer par votre nom d'utilisateur MQTT
            options.setPassword("NNSXS.M3LVHP4XCLPOGNCYTHTLRQWPIM5TGEKMFRIOFVY.5EKC7DNQ4XOAGJLKTULD5INAVAU6JLRZ5PM6GJ45SCKJJWZ4NXYA".toCharArray()); // Remplacer par votre mot de passe MQTT
            client.connect(options);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Conversion du message MQTT reçu en String
                    String messageContent = new String(message.getPayload());
                    //System.out.println("Message arrived: " + messageContent);

                    // Utiliser Jackson pour analyser le contenu

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(messageContent);

                    

                    JsonNode decodedPayload = rootNode.path("uplink_message").path("decoded_payload").path("text");
                    if (!decodedPayload.isMissingNode()) { // si 'text' existe
                        String distance = decodedPayload.asText();
                        double distancef = Double.parseDouble(distance);
                        System.out.println("Distance: " + distance);
                        insertDistanceIntoDatabase(connection, distancef, "levelWater"); 
                    } else {
                        System.out.println("Le champ 'text' est manquant dans le payload décodé.");
                    }
                }

                private static void sendDistanceToServer(double distancef) {
                    
    String query = "distance=" + distancef; // Vous pouvez ajuster cela selon la structure attendue par votre serveur
    byte[] postData = query.getBytes(StandardCharsets.UTF_8);

    try {
        URL url = new URL("http://192.168.1.49:8080/data");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postData.length));

        conn.getOutputStream().write(postData);

        int responseCode = conn.getResponseCode(); // Vous pouvez utiliser cette réponse pour gérer les cas d'erreur
        System.out.println("POST Response Code :: " + responseCode);

        conn.disconnect();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Pas besoin de gérer cela pour un client abonné
                }
            });

            client.subscribe(topic);
            System.out.println("Subscribed to topic: " + topic);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}