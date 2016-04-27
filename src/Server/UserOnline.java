package Server;


import Both.Message;
import Both.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserOnline implements Observer {
    private ChatRoom chatRoom;
    private Socket socket;
    private User user;

//    private Listener listener;

    public UserOnline(Socket socket, User user, ChatRoom chatRoom) {
        this.socket = socket;
        this.user = user;
        this.chatRoom = chatRoom;
        /**Create Listener to receive messages from user**/
        (new Thread(new Listener(this))).start();
    }

    public User getUser() {
        return user;
    }

    public Socket getSocket() {
        return socket;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

//    public void setListener(Listener listener) {
//        this.listener = listener;
//    }
//
//    public Listener getListener() {
//        return listener;
//    }

    @Override
    public void sendMessage(Message message) {
        try {
            (new ObjectOutputStream(socket.getOutputStream())).writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
