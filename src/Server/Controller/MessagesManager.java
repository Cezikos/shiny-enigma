package Server.Controller;

import Server.Constants;
import Server.Model.Classes.Messages.*;
import Server.Model.Classes.UserForm;
import Server.Model.Classes.UserOnline;
import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Piotr on 2016-05-09.
 */
public class MessagesManager implements Runnable, MessageTypeVisitor {
    private final Core core;
    private final Socket socket;

    private UserOnline userOnline;
    private final List<String> userRooms;

    private final Logger logger = LoggerFactory.getLogger(UserOnline.class);

    public MessagesManager(Core core, Socket socket) {
        this.core = core;
        this.socket = socket;
        this.userOnline = null;
        this.userRooms = Collections.synchronizedList(new ArrayList<>(2));
    }

    @Override
    public void run() {
        sendMessage(new SuccessMessage(0, "Successfully connected to the server", Constants.DEFAULT_ROOM), this.socket);

        ObjectInputStream objectInputStream;
        while (!Thread.currentThread().isInterrupted()) {
            try {

                this.logger.info("Waiting for new message from user");
                objectInputStream = new ObjectInputStream(socket.getInputStream());

                this.logger.info("New message from user");
                final MessageType message = (MessageType) objectInputStream.readObject();

                if (userOnline != null && message instanceof LoginMessage) { //TODO Delete this fking if's
                    sendMessage(new FailureMessage(((Message) message).getID(), "You are already logged in " + userOnline.getUsername(), Constants.DEFAULT_ROOM), this.socket);
                }  else if (userOnline != null || (userOnline == null && message instanceof LoginMessage) || message instanceof RegisterMessage) {

                    /**Visitor Pattern**/
                    final MessageTypeVisitor messageTypeVisitor = this;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            message.accept(messageTypeVisitor);//TODO new thread?
                        }
                    });
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    sendMessage(new FailureMessage(((Message) message).getID(), "Please log in ", Constants.DEFAULT_ROOM), this.socket);
                }


            } catch (IOException e) {
                this.logger.error("Unable to read input stream", e);
                for (int i = 0; i < this.userRooms.size(); i++) {
                    this.core.getRoomsManager().getChatRoom(this.userRooms.get(i)).removeUserOnline(this.userOnline.getUsername());//TODO performance?
                }
                Thread.currentThread().interrupt();
            } catch (ClassNotFoundException e) {
                this.logger.error("Class not found", e);
            }
        }
    }

    private void sendMessage(final Message message, final Socket socket) {
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        Thread.currentThread().interrupt();
    }

    @Override
    public void visit(DisconnectMessage disconnectMessage) {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(FailureMessage failureMessage) {//TODO FailureMessage for server?
    }

    @Override
    public void visit(JoinRoom joinRoom) {
        this.userRooms.add(joinRoom.getRoom());
        this.core.getRoomsManager().addUser(this.userOnline, joinRoom.getRoom());
    }

    @Override
    public void visit(LeftRoom leftRoom) {
        this.userRooms.remove(leftRoom.getRoom());
        this.core.getRoomsManager().getChatRoom(leftRoom.getRoom()).removeUserOnline(this.userOnline.getUsername());
    }

    @Override
    public void visit(LoginMessage loginMessage) {

        final UserForm userForm = (UserForm) loginMessage.getMessage();
        if (this.core.getDatabase().validateUserAndPassword(userForm.getUsername(), userForm.getPassword())) {
            this.userOnline = new UserOnline(userForm.getUsername(), socket);
            core.getRoomsManager().addUser(this.userOnline, Constants.DEFAULT_ROOM);

            sendMessage(new SuccessMessage(loginMessage.getID(), "Successfully logged to the server", Constants.DEFAULT_ROOM), socket);

        } else {
            sendMessage(new FailureMessage(loginMessage.getID(), "Failure of login process", Constants.DEFAULT_ROOM), socket);

        }
    }

    @Override
    public void visit(RegisterMessage registerMessage) {
        final UserForm userForm = (UserForm) registerMessage.getMessage();
        if (!this.core.getDatabase().isUser(userForm.getUsername())) {//TODO Unnecessary query, need optimization
            if (this.core.getDatabase().createUser(userForm.getUsername(), userForm.getPassword())) {
                sendMessage(new SuccessMessage(registerMessage.getID(), "Successfully registered to the server", Constants.DEFAULT_ROOM), socket);
            }
        } else {
            sendMessage(new FailureMessage(registerMessage.getID(), "Failure of register process", Constants.DEFAULT_ROOM), socket);
        }
    }

    @Override
    public void visit(SuccessMessage successMessage) { //TODO Receive status of delivered message, need message table
    }

    @Override
    public void visit(TextMessage textMessage) {
        SignedMessage signedMessage = new SignedMessage(this.userOnline.getUsername(), textMessage.getMessage());
        SignedTextMessage signedTextMessage = new SignedTextMessage(textMessage.getID(), signedMessage, textMessage.getRoom());

        this.core.getRoomsManager().getChatRoom(textMessage.getRoom()).sendMessageToAll(signedTextMessage);
    }

    @Override
    public void visit(SignedTextMessage signedTextMessage) {
        //TODO unnecessary?
    }
}
