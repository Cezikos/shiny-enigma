package Client;

import Both.Codes;
import Both.LoginForm;
import Both.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientListener implements Runnable {

    private Controller controller;

    private Socket clientSocket;
    private boolean running;

    private ObjectInputStream objectInputStream;

    private final String username;
    private final String password; //TODO Is it safe? Password can be stolen :(

    ClientListener(Socket clientSocket, Controller controller, String username, String password) {
        this.clientSocket = clientSocket;
        this.controller = controller;

        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            /**Send request of login**/
            (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message(new LoginForm(username, password), Codes.LOGIN));

            /**Receive correction of login**/
            Message message = (Message) (new ObjectInputStream(clientSocket.getInputStream())).readObject();

            if (message.getHeader() == Codes.SUCCESSFUL_LOGIN) {
                running = true;
            } else {
                running = false;
                clientSocket.close();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
