package Client;

import Both.Codes;
import Both.Constants;
import Both.LoginForm;
import Both.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static Both.Constants.DEFAULT_CHANNEL;

public class ClientListener implements Runnable {


    private Controller controller;

    private Socket clientSocket;
    private boolean running;

    private ObjectInputStream objectInputStream;

    private Message message;

    ClientListener(Socket clientSocket, Controller controller, String username, String password) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.message = null;

        loginToSever(username, password);
    }

    private void loginToSever(String username, String password) {
        /**Send request of login**/
        try {
            (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message(new LoginForm(username, password), Codes.LOGIN, Constants.DEFAULT_CHANNEL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveValidityOfLogin() {
        try {
            /**Receive correction of login**/
            Message message = (Message) (new ObjectInputStream(clientSocket.getInputStream())).readObject();

            /**If login process went successful or not, notify user and listen/not for more messages**/
            if (message.getHeader() == Codes.SUCCESSFUL_LOGIN) {
                controller.setReceivedMessages((String) message.getObject(), message.getChannel());
                running = true;
            } else {
                controller.setReceivedMessages((String) message.getObject(), message.getChannel());
                running = false;
                clientSocket.close();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            running = false;
            try {
                clientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            running = false;
            try {
                clientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void disableButtons() {
        /**Disable buttons Connect, Login, Register **/
        controller.setLoginButtonDisabled();
        controller.setRegisterButtonDisabled();
        controller.setConnectButtonDisabled();
    }

    @Override
    public void run() {
        receiveValidityOfLogin();

        disableButtons();

        while (running) {
            try {
                objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
            } catch (IOException e) {
                terminate();
                //e.printStackTrace();
            }

            try {
                if (running) {
                    message = (Message) objectInputStream.readObject();
                    Codes codes = message.getHeader();

                    switch (codes) {

                        case FAILURE_LOGIN:
                            headerFailureLogin();
                            break;

                        case USER_JOIN:
                            headerUserJoin();
                            break;

                        case USER_LEFT:
                            headerUserLeft();
                            break;

                        case USERS_LIST:
                            headerUsersList();
                            break;

                        case SIMPLE_MESSAGE:
                            headerSimpleMessage();
                            break;

                        default:
                            headerUnknown();

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

    private void headerUsersList() {
        controller.addUserListOnline((ArrayList<String>) message.getObject());
    }

    private void headerUserLeft() {
        controller.setReceivedMessages(message.getObject() + " - left the server", DEFAULT_CHANNEL);
        controller.removeUserOnline((String) message.getObject());
    }

    private void headerFailureLogin() {
        controller.setReceivedMessages((String) message.getObject(), DEFAULT_CHANNEL);
        terminate();
    }

    private void headerUnknown() {
        controller.setReceivedMessages((String) message.getObject(), DEFAULT_CHANNEL);
    }

    private void headerSimpleMessage() {
        controller.setReceivedMessages((String) message.getObject(), message.getChannel());
    }

    private void headerUserJoin() {
        controller.setReceivedMessages(message.getObject() + " - joined the server", DEFAULT_CHANNEL);
        controller.addUserOnline(message.getObject().toString());
    }

    public void terminate() {
        running = false;
    }
}
