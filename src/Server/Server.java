package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
    private UsersOnlineList usersOnlineList;

    /**
     * @param port specific port to listen on it
     */
    public Server(int port) {
        this.running = true;
        this.PORT = port;

        database = new Database();
        usersOnlineList = new UsersOnlineList();

        connectWithDatabase();
    }

    private void connectWithDatabase() {
        if (database.connectToMySQLServer()) {

            System.out.println("Connected with MySQL");

            if (database.connectToDatabase("chat")) { //TODO Not work properly
                System.out.println("Connected with chat");
            }

        } else {
            System.out.println("Connection with MySQL failed!");
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

                thread = new Thread(new MessagesListener(clientSocket, database, usersOnlineList));
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
