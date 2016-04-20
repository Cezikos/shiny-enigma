package Server;


import Both.Message;
import Both.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserOnline implements Observer {
    private Socket socket;
    private User user;

    public UserOnline(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            (new ObjectOutputStream(socket.getOutputStream())).writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
