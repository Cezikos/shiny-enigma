package Server;

import Both.Codes;
import Both.Constants;
import Both.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class UsersOnlineList implements Observed {
    Object locker;
    ArrayList<UserOnline> usersOnline;

    public UsersOnlineList() {
        locker = new Object();
        usersOnline = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer observer) {
        synchronized (locker) {
            usersOnline.add((UserOnline) observer);
            notifyObserversJoinUser(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        Observer temp = observer;
        synchronized (locker) {
            usersOnline.remove(observer);
            notifyObserversLeftUser(temp);
        }
    }

    @Override
    public void notifyObserversJoinUser(Observer observer) {
        Message message = new Message(((UserOnline) observer).getUser().getUsername(), Codes.USER_JOIN, Constants.DEFAULT_CHANNEL);
        synchronized (locker) {
            for (int i = 0; i < usersOnline.size(); i++) {
                usersOnline.get(i).sendMessage(message);
            }
        }
    }

    @Override
    public void notifyObserversLeftUser(Observer observer) {
        Message message = new Message(((UserOnline) observer).getUser().getUsername(), Codes.USER_LEFT, Constants.DEFAULT_CHANNEL);
        synchronized (locker) {
            for (int i = 0; i < usersOnline.size(); i++) {
                usersOnline.get(i).sendMessage(message);
            }
        }
    }

    @Override
    public void sendUsersListToUser(Socket socket) {
        ArrayList<String> usersToSend = new ArrayList<>();
        Message message = new Message(usersToSend, Codes.USERS_LIST, Constants.DEFAULT_CHANNEL);
        synchronized (locker) {
            for (int i = 0; i < usersOnline.size(); i++) {
                usersToSend.add(usersOnline.get(i).getUser().getUsername());
            }

            try {
                (new ObjectOutputStream(socket.getOutputStream())).writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void sendMessageToAllUsers(Message message) {
        synchronized (locker) {
            for (int i = 0; i < usersOnline.size(); i++) {
                usersOnline.get(i).sendMessage(message);
            }
        }
    }
}
