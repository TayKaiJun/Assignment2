package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {

    @FXML
    MenuItem changeUsername;
    @FXML
    MenuItem reconnect;
    @FXML
    MenuItem disconnect;
    @FXML
    MenuItem showConnections;
    @FXML
    TextArea messages;
    @FXML
    TextField inputField;
    @FXML
    Button sendMessage;

    //private Main main;
    private String username;
    private P2PConnection sourceConnection;
    private static TextArea referenceMessages;

    public void initialize(Main main, String  username) throws SocketException{

        //this.main = main;
        this.username = username;
        messages.setEditable(false);
        referenceMessages = messages;

        sourceConnection = P2PConnection.getConnection();
        sourceConnection.setHostName(username);
        // Discovery
        sourceConnection.broadcastToAllHostsOnNetwork(MessageUtil.getMessage(MessageUtil.MessageType.DISCOVER, username));

        changeUsername.setOnAction(event ->{
            TextInputDialog dialog = new TextInputDialog("new user");
            dialog.setTitle("P2P Messenger");
            dialog.setHeaderText("Welcome to the P2P chatroom!");
            dialog.setContentText("Please enter a username:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                this.username = result.get();
            }
            else{
                return;
            }

            sourceConnection.setHostName(this.username);
            sourceConnection.removeAllHost();
            sourceConnection.broadcastToAllHostsOnNetwork(MessageUtil.getMessage(MessageUtil.MessageType.DISCOVER, this.username));

            messages.appendText("You have changed your username to " + this.username + " !\n");
        });

        reconnect.setOnAction(event -> {
            inputField.setDisable(false);
            sourceConnection = P2PConnection.getConnection();
            sourceConnection.setHostName(this.username);
            sourceConnection.broadcastToAllHostsOnNetwork(MessageUtil.getMessage(MessageUtil.MessageType.DISCOVER, this.username));
        });

        disconnect.setOnAction(event -> {
            sourceConnection.stop();
            main.stop();
        });

        showConnections.setOnAction(event -> {
            ArrayList<String> connectedIPs = sourceConnection.getHosts();
            String listOfUsers = "";

            if(connectedIPs.isEmpty())
                listOfUsers = "There is no other connected users in the network.";
            else {
                listOfUsers = "Here are the connected users:\n";
                for (String ipaddress : connectedIPs) {
                    listOfUsers += MessageUtil.getHostName(ipaddress) + "(" + ipaddress + ")" + "\n";
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connected Users");
            alert.setHeaderText(null);
            alert.setContentText(listOfUsers);
            alert.showAndWait();
        });

        inputField.setOnAction(event -> {
            String message = inputField.getText();
            inputField.clear();
            messages.appendText(sourceConnection.getHostName() + ": " + message + "\n");
            try {
                sourceConnection.broadcastToDiscoveredHosts(MessageUtil.getMessage(MessageUtil.MessageType.MESSAGE, message));
            } catch (Exception e) {
                messages.appendText("Failed to send\n");
            }
        });

        sendMessage.setOnAction(event -> {
            String message = inputField.getText();
            inputField.clear();
            messages.appendText(sourceConnection.getHostName() + ": " + message + "\n");
            try {
                sourceConnection.broadcastToDiscoveredHosts(MessageUtil.getMessage(MessageUtil.MessageType.MESSAGE, message));
            } catch (Exception e) {
                messages.appendText("Failed to send\n");
            }
        });
    }

    public static void writeMessage(String message){
        referenceMessages.appendText(message);
    }
}
