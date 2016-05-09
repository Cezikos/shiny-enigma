package Server.Model.Classes;


import Server.Controller.UserOnline;
import Server.Model.Interfaces.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Piotr Kucharski on 2016-04-29.
 */
public class ChatRoom {
    private final String name;
    private final List<UserOnline> usersOnline;

    private Logger logger;

    public ChatRoom(final String name) {
        this.name = name;
        this.usersOnline = Collections.synchronizedList(new ArrayList<UserOnline>(10));

        logger = LoggerFactory.getLogger(ChatRoom.class);
    }

    public void sendMessageToAll(final Message message) {
        final List<UserOnline> usersToDelete = new ArrayList<>(5);

        synchronized (this.usersOnline) {
            for (int i = 0; i < this.usersOnline.size(); i++) {
                try {
                    logger.info("Sending new message from Queue");
                    new ObjectOutputStream(this.usersOnline.get(i).getSocket().getOutputStream()).writeObject(message);
                } catch (IOException e) {
                    usersToDelete.add(this.usersOnline.get(i));
                    e.printStackTrace();
                }
            }

            this.usersOnline.removeAll(usersToDelete);
        }

    }

    public final String getName() {
        return this.name;
    }

    public final void addUserOnline(final UserOnline userOnline) {
        this.usersOnline.add(userOnline);
    }

    public final void removeUserOnline(final String username) {
        synchronized (this.usersOnline) {
            for (int i = 0; i < this.usersOnline.size(); i++) {
                if (this.usersOnline.get(i).getUsername().equals(username)) {
                    this.usersOnline.remove(i);
                }
            }
        }
    }

    public final void setMessageQueue(Message message) {
        logger.info("Put new message in Queue");
        sendMessageToAll(message);

    }

    public final void closeRoom() {
        Thread.currentThread().interrupt();
    }

    public static void closeRooms() {
        Thread.currentThread().interrupt();
    }
}
