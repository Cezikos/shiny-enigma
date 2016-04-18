package Server;

import Both.Codes;
import Both.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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

    /**
     * Object locker to synchronize ArrayList with online users
     **/
    private static Object lockOnlineUsers;
    /**
     * Static ArrayList which contain all online users
     **/
    static ArrayList<UserOnline> onlineUsersArrayList = new ArrayList<>();

    Database database;

    /**
     * @param port specific port to listen on it
     */
    public Server(int port) {
        this.running = true;
        this.PORT = port;

        lockOnlineUsers = new Object();
        database = new Database();

        if (database.connectToMySQLServer()) {

            System.out.println("Connected with MySQL");

            if (database.connectToDatabase("chat")) { //TODO Not work properly
                System.out.println("Connected with chat");
            }

        } else {
            System.out.println("Connection with MySQL failed!");
        }

    }


    static void sendAllUsersOnlineToAll() { //TODO Is it necessary? Maybe, /users command
        synchronized (lockOnlineUsers) {
            ArrayList<String> usersToSend = new ArrayList<>();

            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                usersToSend.add(onlineUsersArrayList.get(i).getUser().getUsername());
            }

            sendObjectToAllUsers(new Message(usersToSend, Codes.USERS_LIST));
        }
    }

    static void sendAllUsersOnlineToUser(Socket socket) {
        synchronized (lockOnlineUsers) {
            ArrayList<String> usersToSend = new ArrayList<>();

            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                usersToSend.add(onlineUsersArrayList.get(i).getUser().getUsername());
            }

            sendObjectToUser(new Message(usersToSend, Codes.USERS_LIST), socket);
        }
    }

    static void sendObjectToUser(Message message, Socket socket) {
        synchronized (lockOnlineUsers) {
            try {
                (new ObjectOutputStream(socket.getOutputStream())).writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void addUserOnlineAndSendToAll(UserOnline userOnline) {
        synchronized (lockOnlineUsers) {
            onlineUsersArrayList.add(userOnline);
            sendObjectToAllUsers(new Message(userOnline.getUser().getUsername(), Codes.USER_JOIN));
        }
    }

    static void removeUsersOnlineAndSendToAll(ArrayList<UserOnline> arrayList) {
        synchronized (lockOnlineUsers) {

            UserOnline userOnline;
            for (int i = 0; i < arrayList.size(); i++) {
                userOnline = arrayList.get(i);
                userOnline.getMessagesListener().terminate();
                onlineUsersArrayList.remove(userOnline);
                sendObjectToAllUsers(new Message(userOnline.getUser().getUsername(), Codes.USER_LEFT));
            }
        }
    }

    static void removeUserOnlineAndSendToAll(Socket socket) {
        synchronized (lockOnlineUsers) {
            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                if (onlineUsersArrayList.get(i).getMessagesListener().getClientSocket() == socket) {
                    onlineUsersArrayList.get(i).getMessagesListener().terminate();
                    String s = onlineUsersArrayList.get(i).getUser().getUsername();
                    onlineUsersArrayList.remove(i);
                    i = onlineUsersArrayList.size();
                    sendObjectToAllUsers(new Message(s, Codes.USER_LEFT));

                }
            }
        }
    }

    static UserOnline getUserOnline(int index) { //TODO Is it necessary?
        synchronized (lockOnlineUsers) {
            return onlineUsersArrayList.get(index);
        }
    }

    static void sendObjectToAllUsers(Message message) {
        synchronized (lockOnlineUsers) {
            ArrayList<UserOnline> usersToDelete = new ArrayList<>();
            UserOnline userOnline;

            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                userOnline = onlineUsersArrayList.get(i);
                try {
                    (new ObjectOutputStream(userOnline.getMessagesListener().getClientSocket().getOutputStream())).writeObject(message);
                } catch (IOException e) {
                    usersToDelete.add(onlineUsersArrayList.get(i));
                    e.printStackTrace();
                }
            }

            removeUsersOnlineAndSendToAll(usersToDelete);
        }
    }


    public void start() {
        Socket clientSocket;
        Thread thread;
        try {
            serverSocket = new ServerSocket(PORT);

            while (running) {
                System.out.print("Listening in socket " + PORT + "\n");

                clientSocket = serverSocket.accept();

                thread = new Thread(new MessagesListener(clientSocket, database));
                thread.setDaemon(true);


                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate() {  //TODO I should implement this method to correctly close the thread
        running = false;
        try {
            new Socket("127.0.0.1", PORT);
        } catch (Exception e) {

        }
    }
}
