package Server.Controller;

import Server.Model.Classes.*;
import Server.Model.Interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public class Core implements Runnable {
    private final RoomsManager roomsManager;
    private final Database database;

    public Core() {
        this.roomsManager = new RoomsManager();
        this.database = new MySQL();
    }

    public void run() {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        ServerSocket serverSocket = null;

        try {
            /**Listen on specific port**/
            serverSocket = new ServerSocket(7171);

            /**Loop to wait for new users**/
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    logger.info("Waiting for the new user");
                    final Socket socket = serverSocket.accept();

                    /**Create new thread for new MessagesManager**/
                    new Thread(new MessagesManager(this, socket)).start();//TODO Close??
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

    public final RoomsManager getRoomsManager() {
        return this.roomsManager;
    }

    public final Database getDatabase() {
        return this.database;
    }

    public final void closeServer() {
        //TODO Close rooms, disconnect with users
        Thread.currentThread().interrupt();
    }
}
