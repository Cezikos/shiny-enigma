package Server;

import Both.Codes;
import Both.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;

public class Listener implements Runnable {
    private UserOnline userOnline;
    private Hashtable<String, ChatRoom> chatRoomArrayList;


    public Listener(UserOnline userOnline, Hashtable<String, ChatRoom> chatRoomArrayList) {
        this.userOnline = userOnline;
        this.chatRoomArrayList = chatRoomArrayList;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream;
        while (!Thread.currentThread().isInterrupted()) {

            try {
                objectInputStream = new ObjectInputStream(userOnline.getSocket().getInputStream());
                System.out.println("dasdas");
                Message message = (Message) objectInputStream.readObject();
                Codes codes = message.getHeader();

                switch (codes) { //TODO Need to implement more switch statement

                    case SIMPLE_MESSAGE:
                        headerSimpleMessage(message);
                        break;

                    case JOIN_ROOM:
                        headerJoinRoom(message);
                        break;

                    case DISCONNECT:
                        headerDisconnect();
                        break;

                }

            } catch (IOException e) {
                e.printStackTrace();
                closeListener();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void headerJoinRoom(Message message) {
        if(!chatRoomArrayList.containsKey(message.getChannel())){
            chatRoomArrayList.put(message.getChannel(), new ChatRoom(message.getChannel()));
        }
        chatRoomArrayList.get(message.getChannel()).addUser(userOnline);
    }

    private void headerDisconnect() {
        new Thread(new Runnable() {//TODO Not working properly.
            @Override
            public void run() {
                userOnline.getChatRoom().getUsersOnlineList().removeObserver(userOnline);
            }
        }).start();
    }

    private void headerSimpleMessage(Message message) {
        final String msg = "[" + userOnline.getUser().getUsername() + "]:" + message.getObject();
        new Thread(new Runnable() {
            @Override
            public void run() {
                userOnline.getChatRoom().getUsersOnlineList().sendMessageToAllUsers(new Message(msg, Codes.SIMPLE_MESSAGE, message.getChannel()));
            }
        }).start();
    }

    public void closeListener() {
        Thread.currentThread().interrupt();
    }
}
