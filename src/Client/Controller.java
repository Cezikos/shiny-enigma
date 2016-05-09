package Client;

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

    private Socket socket;

    private Listener listener;

    private Logger logger;

    public Controller() {
        this.logger = LoggerFactory.getLogger(Controller.class);
        this.channelsList = new Hashtable<>();
        this.socket = null;
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

        tabPane.getTabs().add(tab);
        channelsList.put(channel, textArea);

    }

    @FXML
    void connectToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                conn();
            }
        }).start();
    }

    private void conn() {
        try {
            this.socket = new Socket("127.0.0.1", 7171);
            Object object = new ObjectInputStream(this.socket.getInputStream()).readObject();
            if (object instanceof SuccessMessage) {
                if (((SuccessMessage) object).getMessage() == SuccessCodes.CONNECTION) {
                    System.out.println("Connected");//TODO Change IT!
                }
            }
        } catch (IOException e) {
            this.socket = null;
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void disconnectFromServer(ActionEvent event) {

    }

    @FXML
    void joinChannel() {
        if(!outputChannel.getText().isEmpty()){
            ActionMessage actionMessage = new ActionMessage(null, this.outputChannel.getText(), ActionCodes.JOIN_ROOM);
            try {
                new ObjectOutputStream(this.socket.getOutputStream()).writeObject(actionMessage);
                openNewChannel(this.outputChannel.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.outputChannel.clear();
        }
    }

    @FXML
    void loginToServer() { //TODO Refactor
        if (!this.username.getText().isEmpty() && this.username.getText().length() < 20 && !this.password.getText().isEmpty()) {

            /**Prepare login form**/
            UserForm userForm = new UserForm(username.getText(), password.getText());
            ActionMessage actionMessage = new ActionMessage(userForm, Constants.DEFAULT_ROOM, ActionCodes.LOGIN);

            /**Send login form**/
            try {
                new ObjectOutputStream(this.socket.getOutputStream()).writeObject(actionMessage);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            /**Wait for success or failure register**/
            try {
                final Object object = new ObjectInputStream(this.socket.getInputStream()).readObject();

                /**If success**/
                if (object instanceof SuccessMessage) {

                    this.logger.info("Creating new thread to listen messages");
                    /**Create new thread to listen new messages from server **/
                    this.listener = new Listener(socket, this);
                    new Thread(this.listener).start();

                    /**If failure**/
                } else if (object instanceof ErrorMessage) {
                    logger.info("Failed ?", ((Message) object).getMessage().toString()); //TODO Change IT!
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void registerAccountOnServer(ActionEvent event) { //TODO Refactor
        new Thread(new Runnable() {
            @Override
            public void run() {
                reg();
            }
        }).start();
    }

    private void reg() {
        if (!this.username.getText().isEmpty() && this.username.getText().length() < 20 && !this.password.getText().isEmpty()) {
            /**Prepare register form**/
            UserForm userForm = new UserForm(username.getText(), password.getText());
            ActionMessage actionMessage = new ActionMessage(userForm, Constants.DEFAULT_ROOM, ActionCodes.REGISTER);

            /**Send register form**/
            try {
                new ObjectOutputStream(this.socket.getOutputStream()).writeObject(actionMessage);
            } catch (final IOException e) {
                e.printStackTrace();
            }


            /**Wait for success or failure register**/
            try {
                final Object object = new ObjectInputStream(this.socket.getInputStream()).readObject();

                /**If success**/
                if (object instanceof SuccessMessage) {
                    this.logger.info("Register process went successfully");
                    /**If failure**/
                } else if (object instanceof ErrorMessage) {
                    logger.info("Failed ?", ((Message) object).getMessage().toString()); //TODO Change IT!
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }
    }


    @FXML
    void sendMessageToServer() {
        TextMessage textMessage = new TextMessage(this.outputMessage.getText(), this.tabPane.getSelectionModel().getSelectedItem().getText());
        try {
            new ObjectOutputStream(this.socket.getOutputStream()).writeObject(textMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.outputMessage.clear();
    }

    public void addMessage(String message, String room) {
        this.channelsList.get(room).appendText(message + "\n");
    }
}
