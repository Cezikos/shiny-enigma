package Server;

import Both.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Server {
    /**
     * Server will listening on this socket
     **/
    private ServerSocket serverSocket;
    /**
     * Port on which server will be listening
     **/
    private final int PORT;
    /**
     * Condition for infinite loop
     **/
    private boolean running;

    private Database database;
    private Hashtable<String, ChatRoom> chatRoomArrayList;

    /**
     * @param port specific port to listen on it
     */
    public Server(int port) {
        this.running = true;
        this.PORT = port;

        database = new Database();
        chatRoomArrayList = new Hashtable<>();

    }

    public void start() {

        try {
            serverSocket = new ServerSocket(PORT);

            Logger logger = LoggerFactory.getLogger(Server.class);
            while (running) {
                Socket clientSocket = serverSocket.accept();

                logger.info("Someone connected");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("Created thread for the new user");
                        userConnected(clientSocket);
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * If someone connected, check what he would like to do Login or Register
     **/
    private void userConnected(Socket clientSocket) {
        ObjectInputStream objectInputStream = null;
        Message message = null;

        try {

            /**Listening first message from new client,it can be LOGIN, REGISTER or DISCONNECT**/
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

            /**Getting first message from new client,it can be LOGIN, REGISTER or DISCONNECT**/
            message = (Message) objectInputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (message != null) {
            final Codes codes = message.getHeader();
            switch (codes) {

                case LOGIN:
                    loginHeader(message, clientSocket);
                    break;

                case REGISTER:
                    registerHeader(message, clientSocket);
                    break;

                case DISCONNECT:
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

    }

    private void loginHeader(Message message, Socket clientSocket) {
        final String username = ((LoginForm) message.getObject()).getLogin();
        final String password = ((LoginForm) message.getObject()).getPassword();

        /**If login and password is correct then client is successfully logged in otherwise client is disconnected**/
        if (database.isValidLoginAndPassword(username, password)) {

            /**Sending message about successful login**/
            Message messageToSend = new Message("You have been successfully logged in", Codes.SUCCESSFUL_LOGIN);
            sendMessage(clientSocket, messageToSend);

            /**If default room does not exist, create it**/
            ChatRoom chatRoom = new ChatRoom(Constants.DEFAULT_CHANNEL);
            if (!chatRoomArrayList.containsKey(Constants.DEFAULT_CHANNEL)) {
                chatRoomArrayList.put(Constants.DEFAULT_CHANNEL, chatRoom);
            }

            /**Create new user**/
            UserOnline userOnline = new UserOnline(clientSocket, new User(username), chatRoom);
            /**Add user to default room**/
            addUserToRoom(Constants.DEFAULT_CHANNEL, userOnline);

            /**Create Listener to receive messages from user**/
            (new Thread(new Listener(userOnline, chatRoomArrayList))).start();
        } else {

            /**If username or password is incorrect, send FAILURE_LOGIN**/
            Message messageToSend = new Message(Codes.FAILURE_LOGIN + "[Error] Incorrect login or password", Codes.FAILURE_LOGIN);
            sendMessage(clientSocket, messageToSend);

            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void registerHeader(Message message, Socket clientSocket) {
        final String username = ((LoginForm) message.getObject()).getLogin();
        final String password = ((LoginForm) message.getObject()).getPassword();

        /**Creating new account if not exist**/
        if (database.createUser(username, password)) {

            /**If register succeeded**/
            Message messageToSend = new Message("", Codes.SUCCESSFUL_REGISTER);
            sendMessage(clientSocket, messageToSend);

        } else {

            /**If register failed**/
            Message messageToSend = new Message("", Codes.FAILURE_REGISTER);
            sendMessage(clientSocket, messageToSend);

        }

    }

    public void addUserToRoom(String room, UserOnline userOnline) {
        if (!chatRoomArrayList.containsKey(room)) {
            addRoom(room);
        }
        chatRoomArrayList.get(room).addUser(userOnline);
    }

    private boolean addRoom(String room) {
        if (!chatRoomArrayList.containsKey(room)) {
            chatRoomArrayList.put(room, new ChatRoom(room));

            return true;
        }

        return false;
    }

    private void sendMessage(Socket clientSocket, Message messageToSend) {
        try {
            (new ObjectOutputStream(clientSocket.getOutputStream())).writeObject(messageToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminateServer() {  //TODO I should implement this method to correctly close the thread
        running = false;
        try {
            new Socket("127.0.0.1", PORT);
        } catch (Exception e) {

        }
    }
}
