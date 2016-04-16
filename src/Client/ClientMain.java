package Client;

import Both.Codes;
import Both.Message;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
        primaryStage.setTitle("JavaFX - Simple Chat");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {

        try {
            (new ObjectOutputStream(Controller.controller.getClientSocket().getOutputStream())).writeObject(new Message("", Codes.DISCONNECT));
            Controller.controller.terminate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
