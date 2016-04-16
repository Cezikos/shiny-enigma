package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = FXMLLoader.load(getClass().getResource("Controller.fxml"));
        primaryStage.setTitle("JavaFX - Simple Chat");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){

        try {
            (new PrintStream(Controller.controller.getClientSocket().getOutputStream())).println("Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
