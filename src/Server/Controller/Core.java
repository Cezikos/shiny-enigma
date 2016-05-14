package Server.Controller;

import Server.Model.Classes.*;
import Server.Model.Interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Core class which listen on specific port for new users then
 * create separate thread for each user
 */
public class Core implements Runnable {
    /**
     * Main class to manage all rooms
     **/
    private final RoomsManager roomsManager;
    /**
     * Specific type of database to store data
     **/
    private final Database database;

    public Core() {
        this.roomsManager = new RoomsManager();
        this.database = new MySQL();
    }

    /**
     * Awaiting for new user then create separate thread for each user
     */
    public void run() {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        ServerSocket serverSocket = null;

        try {
            // Listen on specific port
            serverSocket = new ServerSocket(7171);

            // Loop to wait for new users
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    logger.info("Waiting for the new user");
                    final Socket socket = serverSocket.accept();

                    // Create new thread for new MessagesManager
                    new Thread(new MessagesManager(this, socket)).start();//TODO Close?? Deamon??

                } catch (final IOException e) {
                    logger.error("New connection accept failed", e);
                }
            }

        } catch (final IOException e) {
            logger.error("Cannot listen on port", e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (final IOException e) {
                logger.error("Cannot close server socket", e);
            }
        }
    }

    /**
     * @return RoomsManager Which contain all rooms
     **/
    public final RoomsManager getRoomsManager() {
        return this.roomsManager;
    }

    /**
     * @return Database Which give access to stored data
     */
    public final Database getDatabase() {
        return this.database;
    }
}
