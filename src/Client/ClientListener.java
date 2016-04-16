package Client;

import Both.Codes;
import Both.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientListener implements Runnable {

    private Controller controller;

    private Socket clientSocket;
    private boolean running;

    private ObjectInputStream objectInputStream;

    ClientListener(Socket clientSocket, Controller controller) {

        this.clientSocket = clientSocket;
        this.controller = controller;

        running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
            } catch (IOException e) {
                terminate();
                e.printStackTrace();
            }

            try {
                if (running) {
                    Message message = (Message) objectInputStream.readObject();


                    if (message.getHeader() == Codes.FAILURE_LOGIN) {
                        controller.setReceivedMessages((String) message.getObject());
                        terminate();
                    } else if (message.getHeader() == Codes.USER_JOIN) {
                        controller.setReceivedMessages(message.getObject() + " - joined the server");
                        controller.addUserOnline(message.getObject().toString());
                    } else if (message.getHeader() == Codes.USER_LEFT) {
                        controller.setReceivedMessages(message.getObject() + " - left the server");
                        controller.removeUserOnline((String) message.getObject());
                    } else if (message.getHeader() == Codes.USERS_LIST) {
                        controller.addUserListOnline((ArrayList<String>) message.getObject());
                    } else {
                        controller.setReceivedMessages((String) message.getObject());
                    }


                }
            } catch (IOException e) {
                terminate();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    public void terminate() {
        running = false;
    }
}
