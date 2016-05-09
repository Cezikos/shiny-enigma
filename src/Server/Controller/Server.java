package Server.Controller;

import Server.*;
import Server.Model.Classes.Messages.ActionMessage;
import Server.Model.Classes.Messages.ErrorMessage;
import Server.Model.Classes.Messages.SuccessMessage;
import Server.Model.Enums.ActionCodes;
import Server.Model.Classes.*;
import Server.Model.Enums.ErrorCodes;
import Server.Model.Interfaces.*;
import Server.Model.Enums.SuccessCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public class Server implements Runnable {
    private final List<ChatRoom> chatRooms;
    private final Database database;

    public Server() {
        this.chatRooms = Collections.synchronizedList(new ArrayList<>(10));
        this.database = new MySQL();
    }

    public void run() {
        final Logger logger = LoggerFactory.getLogger(this.getClass());

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(7171);
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    logger.info("Waiting for the new user");
                    final Socket socket = serverSocket.accept();
                    new Thread(new Runnable() {
                        public void run() {
                            logger.info("New thread created");
                            userBuilder(socket);
                        }
                    }).start();

                } catch (final IOException e) {
                    logger.error("New connection accept failed", e);
                }

            }
        } catch (final IOException e) {
            logger.error("Cannot listen on port", e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (final IOException e) {
                logger.error("Cannot close server socket", e);
            }
        }
    }

    private void userBuilder(final Socket socket) {
        try {
            sendMessage(new SuccessMessage(System.currentTimeMillis(), SuccessCodes.CONNECTION, Constants.DEFAULT_ROOM), socket);

            final ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            final Message message = (Message) objectInputStream.readObject();

            if (message instanceof ActionMessage) {
                final ActionCodes actionMessage = ((ActionMessage) message).getCode();
                switch (actionMessage) {
                    case LOGIN:
                        /**If True, create new thread for UserOnline to listen messages**/
                        if (loginCode(message, socket)) {
                            sendMessage(new SuccessMessage(message.getId(), SuccessCodes.LOGIN, Constants.DEFAULT_ROOM), socket);
                        } else {
                            sendMessage(new ErrorMessage(message.getId(), ErrorCodes.LOGIN, Constants.DEFAULT_ROOM), socket);
                        }
                        break;

                    case REGISTER:
                        if (registerCode(message)) {
                            sendMessage(new SuccessMessage(message.getId(), SuccessCodes.REGISTER, Constants.DEFAULT_ROOM), socket);

                        } else {
                            sendMessage(new ErrorMessage(message.getId(), ErrorCodes.REGISTER, Constants.DEFAULT_ROOM), socket);
                        }
                        break;

                    case DISCONNECT:
                        socket.close();
                        break;
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(final Message message, final Socket socket) throws IOException {
        new ObjectOutputStream(socket.getOutputStream()).writeObject(message);
    }

    private boolean loginCode(final Message message, final Socket socket) {
        final UserForm userForm = (UserForm) message.getMessage();
        if (this.database.validateUserAndPassword(userForm.getUsername(), userForm.getPassword())) {
            if (!isRoom(message.getRoom())) {
                addRoom(message.getRoom());
            }
            final UserOnline userOnline = new UserOnline(this, userForm.getUsername(), socket);
            new Thread(userOnline).start(); /**Creating new thread if user successfully logged in**/
            addUser(userOnline, message.getRoom());
            return true;
        }
        return false;
    }

    private boolean registerCode(final Message message) {
        final UserForm userForm = (UserForm) message.getMessage();
        if (!this.database.isUser(userForm.getUsername())) {
            if (this.database.createUser(userForm.getUsername(), userForm.getPassword())) {
                return true;
            }
        }
        return false;
    }

    private boolean isRoom(final String room) {
        synchronized (this.chatRooms) {
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(room)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void addRoom(final String name) {
        this.chatRooms.add(new ChatRoom(name));
    }

    public boolean addUser(final UserOnline userOnline, final String roomName) {
        synchronized (this.chatRooms) {
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(roomName)) {
                    this.chatRooms.get(i).addUserOnline(userOnline);

                    return true;
                }
            }
            addRoom(roomName);
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(roomName)) {
                    this.chatRooms.get(i).addUserOnline(userOnline);

                    return true;
                }
            }
            return false;
        }
    }

    public final ChatRoom getChatRoom(final String room) {
        synchronized (this.chatRooms) {
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(room)) {
                    return this.chatRooms.get(i);
                }
            }
            return null;
        }
    }

    public final void closeServer() {
        synchronized (this.chatRooms) {
            for (int i = 0; i < this.chatRooms.size(); i++) {
                this.chatRooms.get(i).closeRoom();
            }
        }
        Thread.currentThread().interrupt();
    }
}
