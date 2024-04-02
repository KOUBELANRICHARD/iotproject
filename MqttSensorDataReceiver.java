

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
import java.net.URL;


public class MqttSensorDataReceiver {
            
   

    private static void insertDistanceIntoDatabase(Connection connection, double distance)  throws SQLException {

        int #_id = 0;
        int controller_id = 0;
        int captor_id= 0;

      
        
        String sql = "INSERT INTO CONTROLLER_CAPTORS (#_id, controller_id, captor_id, value) VALUES (?, ?, ?, ?)"; 


        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            //preparedStatement.setInt(1, #_id);
            
            preparedStatement.setInt(2, controller_id);

            preparedStatement.setInt(3, captor_id); 

            preparedStatement.setDouble(4, distance);

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

    /*
    private static int findSensorIdByName(String name) throws SQLException {

        Connection connection = new Connexion().renvoi();
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

    */

    public static void main(String[] args) {

        String serverURI = "tcp://nam1.cloud.thethings.network:1883"; 
        String clientId = MqttClient.generateClientId();
        String topic = "v3/my-lora-testapp@ttn/devices/eui-a8610a32301a7818/#"; 

        Connection connection = new Connexion().renvoi(); //class connection au projet


        try {

            MqttClient client = new MqttClient(serverURI, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("my-lora-testapp@ttn"); //Remplacer par votre nom d'utilisateur MQTT
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
                        insertDistanceIntoDatabase(connection, distancef); 
                    } else {
                        System.out.println("Le champ 'text' est manquant dans le payload décodé.");
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