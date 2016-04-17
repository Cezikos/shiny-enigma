package Client;

import Both.Codes;
import Both.Constants;
import Both.LoginForm;
import Both.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    static Controller controller;

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField address;

    @FXML
    private TextField port;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button login;

    @FXML
    private TextField outputMessage;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea inputMessages;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn usersList;


    private ObservableList<User> usersObservableList;

    private ClientListener clientListener;

    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;
    private StringBuilder stringBuilder;

    public Controller() {
        stringBuilder = new StringBuilder();
        usersObservableList = FXCollections.observableArrayList();
        clientListener = null;
        clientSocket = null;
        controller = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        inputMessages.setEditable(false);
        inputMessages.setWrapText(true);
        usersList.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );
        usersTable.setItems(usersObservableList);

        setDefaultValues();
    }

    public void addUserOnline(String name) {
        usersObservableList.add(new User(name));
        usersTable.refresh();
    }

    public void addUserListOnline(ArrayList<String> arrayList) {
        usersObservableList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            usersObservableList.add(new User(arrayList.get(i)));
        }
        usersTable.refresh();
    }

    public void removeUserOnline(String name) {
        for (int i = 0; i < usersObservableList.size(); i++) {
            if (usersObservableList.get(i).getUsername().equals(name)) {
                usersObservableList.remove(i);
                i = usersObservableList.size();
            }
        }
        usersTable.refresh();
    }

    private void setDefaultValues() {
        address.setText("127.0.0.1");
        port.setText("7171");

        login.setDisable(true);
    }

    private boolean isUsername() {
        if (username.getText().length() > 0){
            return true;
        } else {
            return false;
        }
    }

    private boolean isPassword() {
        if(password.getText().length() > 0){
            return true;
        } else {
            return false;
        }
    }

    private String getUsername() {
        return username.getText();
    }

    private String getPassword() { //TODO Maybe clear password field after get?
        return password.getText();
    }

    @FXML
    private void connectToServer() {
        try {
            clientSocket = new Socket(Constants.HOST, Constants.PORT);
        } catch (IOException e) {
            clientSocket = null;
            e.printStackTrace();
        }

        if(clientSocket != null){
            login.setDisable(false);
        } else {
            login.setDisable(true);
        }
    }

    @FXML
    private void disconnectFromServer() {
        terminate();
    }

    @FXML
    private void loginToServer() {
        if (clientSocket.isConnected()) {

            if(isUsername() && isPassword()){

                Thread thread = new Thread(clientListener = new ClientListener(clientSocket, this, getUsername(), getPassword()));
                thread.setDaemon(true); //TODO Daemon?? :D
                thread.start();

            } else {
                login.setDisable(true);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @FXML
    private void sendMessageToServer() {
        if (outputMessage.getText().length() > 0) {
            try {
                objectOutputStream.writeObject(new Message(outputMessage.getText(), Codes.SIMPLE_MESSAGE));
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputMessage.clear();
        }

    }

    public void setReceivedMessages(String message) {
        stringBuilder.append(message + "\n");
        inputMessages.setText(stringBuilder.toString());
        inputMessages.setScrollTop(Double.MAX_VALUE);
    }

    public void terminate() {

        try {
            (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message("", Codes.DISCONNECT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientListener.terminate();
        if (clientSocket.isConnected()) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
