package Server;


import Both.Message;
import Both.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class UserOnline implements Observer {
    private MessagesListener messagesListener; //TODO Redundant weird
    private User user;

    public UserOnline(MessagesListener messagesListener, User user) {
        this.messagesListener = messagesListener;
        this.user = user;
    }

    public MessagesListener getMessagesListener() {
        return messagesListener;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            (new ObjectOutputStream(messagesListener.getClientSocket().getOutputStream())).writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
