package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        String initUsername = "";
        TextInputDialog dialog = new TextInputDialog("new user");
        dialog.setTitle("P2P Messenger");
        dialog.setHeaderText("Welcome to the P2P chatroom!");
        dialog.setContentText("Please enter a username:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            initUsername = result.get();
        } else{
            this.stop();
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("messenger.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.initialize(this, initUsername);

        primaryStage.setTitle("P2P Messenger");
        primaryStage.setScene(new Scene(root, 600, 800));
        primaryStage.show();
        this.primaryStage = primaryStage;
    }

    public void stop(){
        P2PConnection.getConnection().stop(() ->{
            Platform.runLater(()->{
                primaryStage.close();
            });
            return null;
        }, null);
    }


    public static void main(String[] args) {launch(args);}
}
