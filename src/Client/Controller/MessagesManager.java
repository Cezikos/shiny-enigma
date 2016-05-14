package Client.Controller;

import Server.Constants;
import Server.Model.Classes.Messages.*;
import Server.Model.Classes.UserForm;
import Server.Model.Interfaces.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Piotr Kucharski on 2016-05-10.
 */
public class MessagesManager implements Runnable {
    private final String hostname;
    private final int port;

    private Controller controller;
    private Socket socket;

    public MessagesManager(String hostname, int port, Controller controller) {
        this.hostname = hostname;
        this.port = port;
        this.controller = controller;
        this.socket = null;
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket(this.hostname, this.port);
        } catch (IOException e) {
            closeListener();//TODO Message to the user
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = (Message) new ObjectInputStream(socket.getInputStream()).readObject();

                if (message instanceof SignedTextMessage) { //TODO Need to delete If's
                    SignedTextMessage signedTextMessage = (SignedTextMessage) message;

                    controller.addMessage("[" + signedTextMessage.getMessage().getAuthor() + "] - " + signedTextMessage.getMessage().getMessage(), message.getRoom());//TODO each message should be handled separately
                } else {
                    controller.addMessage((String) message.getMessage(), message.getRoom());//TODO each message should be handled separately
                }
            } catch (IOException e) {
                closeListener();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(final String username, final String password) throws IOException {
        sendMessage(new RegisterMessage(new UserForm(username, password)));
    }

    public void login(final String username, final String password) throws IOException {
        sendMessage(new LoginMessage(new UserForm(username, password)));
    }

    public void joinRoom(String room) throws IOException {
        sendMessage(new JoinRoom(room));
    }

    public void leftRoom(String room) throws IOException {
        sendMessage(new LeftRoom(room));
    }

    public void sendMessage(Message message) throws IOException {
        new ObjectOutputStream(this.socket.getOutputStream()).writeObject(message);
    }

    public void disconnect() {
        try {
            new ObjectOutputStream(this.socket.getOutputStream()).writeObject(new DisconnectMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeListener() {
        Thread.currentThread().interrupt();
    }
}
