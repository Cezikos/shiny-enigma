package Server;


public class UserOnline {
    private MessagesListener messagesListener;
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
}
