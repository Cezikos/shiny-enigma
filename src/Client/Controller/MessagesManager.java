package Client.Controller;

import Server.Constants;
import Server.Model.Classes.Messages.JoinRoom;
import Server.Model.Classes.Messages.LoginMessage;
import Server.Model.Classes.Messages.RegisterMessage;
import Server.Model.Classes.Messages.TextMessage;
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
                if(message instanceof TextMessage){
                    controller.addMessage((String)message.getMessage(), message.getRoom());
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
        sendMessage(new LoginMessage(new UserForm(username, password)));
    }

    public void login(final String username, final String password) throws IOException {
        sendMessage(new RegisterMessage(new UserForm(username, password)));
    }

    public void joinRoom(String room) throws IOException {
        sendMessage(new JoinRoom(room));
    }

    public void sendMessage(Message message) throws IOException {
        new ObjectOutputStream(this.socket.getOutputStream()).writeObject(message);
    }

    public void closeListener() {
        Thread.currentThread().interrupt();
    }
}
