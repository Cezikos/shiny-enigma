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

/**
 * Created by Piotr on 2016-05-09.
 */
public class MessagesManager implements Runnable, MessageTypeVisitor {
    private final Core core;
    private final Socket socket;

    private UserOnline userOnline;

    public MessagesManager(Core core, Socket socket) {
        this.core = core;
        this.socket = socket;
        this.userOnline = null;
    }

    @Override
    public void run() {
        Logger logger = LoggerFactory.getLogger(UserOnline.class);
        ObjectInputStream objectInputStream;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                logger.info("Waiting for new message from user");
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                logger.info("New message from user");
                Message message = (Message) objectInputStream.readObject();
                ((MessageType) message).accept(this);


            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void userBuilder() {
        try {
            sendMessage(new SuccessMessage(0, "Successfully connected to the server", Constants.DEFAULT_ROOM), socket);

            final ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            final Message message = (Message) objectInputStream.readObject();


            if (message instanceof LoginMessage)
            /**If True, create new thread for UserOnline to listen messages**/

            else if (message instanceof RegisterMessage) {
                if (registerCode(message)) {
                    sendMessage(new SuccessMessage(message.getID(), "Successfully registered to the server", Constants.DEFAULT_ROOM), socket);
                } else {
                    sendMessage(new FailureMessage(message.getID(), "Failure register.....", Constants.DEFAULT_ROOM), socket);
                }
            } else if (message instanceof DisconnectMessage) {
                socket.close();
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

    public void disconnect() {
        Thread.currentThread().interrupt();
    }

    @Override
    public boolean visit(DisconnectMessage disconnectMessage) {
        return false;
    }

    @Override
    public boolean visit(FailureMessage failureMessage) {
        return false;
    }

    @Override
    public boolean visit(JoinRoom joinRoom) {
        this.core.getRoomsManager().addUser(this.userOnline, joinRoom.getRoom());
        return true;
    }

    @Override
    public boolean visit(LeftRoom leftRoom) {
        return false;
    }

    @Override
    public boolean visit(LoginMessage loginMessage) {

        final UserForm userForm = (UserForm) loginMessage.getMessage();
        if (this.core.getDatabase().validateUserAndPassword(userForm.getUsername(), userForm.getPassword())) {
            if (!this.core.getRoomsManager().isRoom(Constants.DEFAULT_ROOM)) {
                this.core.getRoomsManager().addRoom(Constants.DEFAULT_ROOM);//TODO Create new room? It should be RoomsManager job, logging to default room Security!
            }
            this.userOnline = new UserOnline(userForm.getUsername(), socket);
            core.getRoomsManager().addUser(this.userOnline, Constants.DEFAULT_ROOM);

            try {
                sendMessage(new SuccessMessage(loginMessage.getID(), "Successfully logged to the server", Constants.DEFAULT_ROOM), socket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        try {
            sendMessage(new FailureMessage(loginMessage.getID(), "Failure of login process", Constants.DEFAULT_ROOM), socket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean visit(RegisterMessage registerMessage) {
        final UserForm userForm = (UserForm) registerMessage.getMessage();
        if (!this.core.getDatabase().isUser(userForm.getUsername())) {
            if (this.core.getDatabase().createUser(userForm.getUsername(), userForm.getPassword())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(SuccessMessage successMessage) {
        return false;
    }

    @Override
    public boolean visit(TextMessage textMessage) {
        return false;
    }
}
