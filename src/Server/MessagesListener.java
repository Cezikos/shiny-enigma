package Server;

import Both.Codes;
import Both.LoginForm;
import Both.Message;
import Both.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessagesListener implements Runnable { //TODO Refactor! Code looks messy

    private UserOnline userOnline;

    private boolean running;

    private Socket clientSocket;
    private ObjectInputStream objectInputStream;

    private Database database;
    private UsersOnlineList usersOnlineList;

    public MessagesListener(Socket clientSocket, Database database, UsersOnlineList usersOnlineList) {
        this.userOnline = null;
        this.clientSocket = clientSocket;
        this.database = database;
        this.usersOnlineList = usersOnlineList;

        this.running = true;
    }

    @Override
    public void run() { //TODO Need Refactor! Code looks, a bit messy.
        try {
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            /**Getting first message from new client,it can be LOGIN, REGISTER or DISCONNECT**/
            Message message = (Message) objectInputStream.readObject();

            /**If statement to attend these three Codes**/
            String username;
            if (message.getHeader() == Codes.LOGIN) {

                username = ((LoginForm) message.getObject()).getLogin();

                /**If login and password is correct then client is successfully logged in otherwise client is disconnected**/
                if (database.isValidLoginAndPassword(username, ((LoginForm) message.getObject()).getPassword())) { //TODO Need to implement database/JSON/XML

                    (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message("You have been successfully logged in", Codes.SUCCESSFUL_LOGIN));


                    final MessagesListener listener = this;
                    /**Creating new thread because sending message to all users can take a lot of time (synchronized + a lot of users)**/
                    new Thread(new Runnable() { //TODO Don't use lambdas cuz my VPS has JRE 1.7
                        @Override
                        public void run() {
                            userOnline = new UserOnline(listener, new User(username));
                            usersOnlineList.addObserver(userOnline);
                            usersOnlineList.sendUsersListToUser(clientSocket);
                            //database.sendAllUsersOnlineToUser(clientSocket); //TODO Online list if new
                        }
                    }).start();

                } else {
                    (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message(Codes.FAILURE_LOGIN.toString() + "[Error] Incorrect login or password", Codes.FAILURE_LOGIN));
                    terminate();
                }

            } else if (message.getHeader() == Codes.REGISTER) {
                username = ((LoginForm) message.getObject()).getLogin();


                /**Creating new account if not exist**/
                if (database.createUser(username, ((LoginForm) message.getObject()).getPassword())) {
                    (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message("", Codes.SUCCESSFUL_REGISTER));
                } else {
                    (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(new Message("", Codes.FAILURE_REGISTER));
                }

                terminate();
            } else if (message.getHeader() == Codes.DISCONNECT) {
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
                objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
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
                terminate();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void headerDisconnect() {
        new Thread(new Runnable() {//TODO Not working properly.
            @Override
            public void run() {
                usersOnlineList.removeObserver(userOnline);
            }
        }).start();
    }

    private void headerSimpleMessage(Message message) {
        final String msg = "[" + userOnline.getUser().getUsername() + "]:" + message.getObject();
        new Thread(new Runnable() {
            @Override
            public void run() {
                usersOnlineList.sendMessageToAllUsers(new Message(msg, Codes.SIMPLE_MESSAGE));
            }
        }).start();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void terminate() {
        this.running = false;
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
