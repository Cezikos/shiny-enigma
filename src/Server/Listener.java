package Server;

import Both.Codes;
import Both.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Listener implements Runnable {
    private UserOnline userOnline;

    public Listener(UserOnline userOnline) {
        this.userOnline = userOnline;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream;
        while (!Thread.currentThread().isInterrupted()) {

            try {
                objectInputStream = new ObjectInputStream(userOnline.getSocket().getInputStream());
                Message message = (Message) objectInputStream.readObject();
                Codes codes = message.getHeader();

                switch (codes) { //TODO Need to implement more switch statement

                    case SIMPLE_MESSAGE:
                        headerSimpleMessage(message);
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
                userOnline.getChatRoom().getUsersOnlineList().sendMessageToAllUsers(new Message(msg, Codes.SIMPLE_MESSAGE, userOnline.getChatRoom().getROOM_ID()));
            }
        }).start();
    }

    public void closeListener() {
        Thread.currentThread().interrupt();
    }
}
