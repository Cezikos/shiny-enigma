package Client.Controller;

import Server.Constants;
import Server.Model.Classes.*;
import Server.Model.Classes.Messages.SuccessMessage;
import Server.Model.Classes.Messages.TextMessage;
import Server.Model.Interfaces.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField address;

    @FXML
    private TextField port;

    @FXML
    private Button connect;

    @FXML
    private Button disconnect;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button login;

    @FXML
    private Button register;

    @FXML
    private TextField outputMessage;

    @FXML
    private Button sendButton;

    @FXML
    private TableView<?> usersTable;

    @FXML
    private TableColumn<?, ?> usersList;

    @FXML
    private TabPane tabPane;

    @FXML
    private TextField outputChannel;

    @FXML
    private Button joinChannel;

    private Hashtable<String, TextArea> channelsList;

    private MessagesManager messagesManager;

    private Logger logger;

    public Controller() {
        this.logger = LoggerFactory.getLogger(Controller.class);
        this.channelsList = new Hashtable<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        /** Open default channel to catch system messages**/
        openNewChannel(Constants.DEFAULT_ROOM);
    }

    /**
     * Open new channel and add to Hashtable
     **/
    private void openNewChannel(String channel) {
        Tab tab = new Tab(channel);

        AnchorPane anchorPane = new AnchorPane();
        tab.setContent(anchorPane);

        TextArea textArea = new TextArea();
        anchorPane.getChildren().add(textArea);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setScrollTop(Double.MAX_VALUE);

        this.tabPane.getTabs().add(tab);
        this.channelsList.put(channel, textArea);

    }

    @FXML
    void connectToServer() {
        this.messagesManager = new MessagesManager("127.0.0.1", 7171, this);
        new Thread(this.messagesManager).start();
    }


    @FXML
    void disconnectFromServer(ActionEvent event) {

    }

    @FXML
    void joinChannel() {
        final String channel = this.outputChannel.getText();
        openNewChannel(channel);
        if (!channel.isEmpty()) {
            try {
                this.messagesManager.joinRoom(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.outputChannel.clear();
        }
    }

    @FXML
    void leaveChannel() {
        final String channel = this.outputChannel.getText();
        if(!channel.isEmpty()){
            try {
                this.messagesManager.leftRoom(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.outputChannel.clear();
        }
    }

    @FXML
    void loginToServer() {
        final String username = this.username.getText();
        final String password = this.password.getText();
        if (!username.isEmpty() && 20 > username.length() && !password.isEmpty()) {
            try {
                this.messagesManager.login(username, password);
                this.password.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    void registerAccountOnServer() {
        final String username = this.username.getText();
        final String password = this.password.getText();
        if (!username.isEmpty() && 20 > username.length() && !password.isEmpty()) {
            try {
                this.messagesManager.login(username, password);
                this.password.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    void sendMessageToServer() {
        TextMessage textMessage = new TextMessage(this.outputMessage.getText(), this.tabPane.getSelectionModel().getSelectedItem().getText());

        try {
            this.messagesManager.sendMessage(textMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.outputMessage.clear();
    }

    public void addMessage(String message, String room) {
        this.channelsList.get(room).appendText(message + "\n");
    }
}
