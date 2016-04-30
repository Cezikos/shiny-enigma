package Server.Model.Classes;


import Server.Model.Enums.ActionCodes;
import Server.Model.Interfaces.Message;
import Server.Controller.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by Piotr Kucharski on 2016-04-29.
 */
public class UserOnline implements Runnable {
    private final Server server;
    private final String username;

    private final Socket socket;

    public UserOnline(Server server, String username, Socket socket) {
        this.server = server;
        this.username = username;
        this.socket = socket;
    }

    public final String getUsername() {
        return this.username;
    }

    public final Socket getSocket() {
        return this.socket;
    }

    public void run() {
        Logger logger = LoggerFactory.getLogger(UserOnline.class);
        ObjectInputStream objectInputStream;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                logger.info("Waiting for new message from user");
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                logger.info("New message from user");
                Message message = (Message) objectInputStream.readObject();

                if (message instanceof TextMessage) {
                    this.server.getChatRoom(message.getRoom()).setMessageQueue(message);
                } else if (message instanceof ActionMessage) {
                    if(((ActionMessage) message).getCode() == ActionCodes.JOIN_ROOM){
                        server.addUser(this, message.getRoom());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        Thread.currentThread().interrupt();
    }
}
