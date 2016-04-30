package Client;

import Server.Model.Classes.TextMessage;
import Server.Model.Interfaces.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by Piotr on 2016-04-30.
 */
public class Listener implements Runnable {

    private Socket socket;
    private Controller controller;

    public Listener(final Socket socket, Controller controller) {
        this.socket = socket;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = (Message) new ObjectInputStream(socket.getInputStream()).readObject();

                controller.addMessage((String) message.getMessage(), message.getRoom());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeListener() {
        Thread.currentThread().interrupt();
    }
}
