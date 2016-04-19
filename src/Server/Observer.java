package Server;


import Both.Message;

public interface Observer {
    void sendMessage(Message message);
}
