package Server;

import Both.Message;

import java.net.Socket;

public interface Observed {
    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObserversJoinUser(Observer observer);

    void notifyObserversLeftUser(Observer observer);

    void sendUsersListToUser(Socket socket);

    void sendMessageToAllUsers(Message message);
}
