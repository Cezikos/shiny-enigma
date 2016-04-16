package Server;

import Both.Codes;
import Both.Message;
import Both.LoginForm;

import java.io.*;
import java.net.Socket;

public class MessagesListener implements Runnable {

    private boolean running;

    private Socket clientSocket;
    private ObjectInputStream objectInputStream;

    public MessagesListener(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.running = true;
    }

    @Override
    public void run() { //TODO Need Refactor! Code looks, a bit messy.
        try {
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            String usernameT = "root"; //TODO Backdoor :D it will be until I'm doing the fucking registration system
            String passwordT = "pass";

            Message message = (Message) objectInputStream.readObject(); //TODO Yeah login form but where is registration?
            String username = ((LoginForm)message.getObject()).getLogin();
            String password = ((LoginForm)message.getObject()).getPassword();

            if (username.equals(usernameT) && password.equals(passwordT)) { //TODO Need to implement database/JSON/XML

                (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message("You have been successfully logged in", Codes.SUCCESSFUL_LOGIN));
                (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message("Your ID: " + clientSocket.getPort(), Codes.SIMPLE_MESSAGE));

                Server.addUserOnlineAndSendToAll(new UserOnline(this, new User(username)));
                Server.sendAllUsersOnlineToUser(clientSocket);

                new Thread(new Runnable() { //TODO Don't use lambdas cuz my VPS has JRE 1.7
                    @Override
                    public void run() {
                        Server.sendObjectToAllUsers(new Message("[User " + clientSocket.getPort() + "] joined the server", Codes.SIMPLE_MESSAGE));
                    }
                }).start();

            } else {

                (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message(Codes.FAILURE_LOGIN.toString() + "[Error] Incorrect login or password", Codes.FAILURE_LOGIN));
                terminate();

            }


        } catch (IOException e) {
            terminate();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (running) {
            try {
                Message message = (Message) objectInputStream.readObject();

                if (message.getHeader() == Codes.SIMPLE_MESSAGE) { //TODO Need to implement more Codes and switch statement?
                    final String msg = "[User: " + clientSocket.getPort() + "]: " + message.getObject();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Server.sendObjectToAllUsers(new Message(msg, Codes.SIMPLE_MESSAGE));
                        }
                    }).start();
                } else if (message.getHeader() == Codes.DISCONNECT) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Server.removeUserOnlineAndSendToAll(clientSocket);
                        }
                    }).start();
                }

            } catch (IOException e) {
                terminate();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void terminate() {
        this.running = false;
        try {
            clientSocket.close();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
