package Server.Controller;

import Server.Model.Classes.ChatRoom;
import Server.Model.Classes.UserOnline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Piotr on 2016-05-09.
 */
public class RoomsManager {
    private final List<ChatRoom> chatRooms;

    public RoomsManager() {
        this.chatRooms = Collections.synchronizedList(new ArrayList<>(10));
    }

    public boolean isRoom(final String room) {
        synchronized (this.chatRooms) {
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(room)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void addRoom(final String name) {
        this.chatRooms.add(new ChatRoom(name));
    }

    public boolean addUser(final UserOnline userOnline, final String roomName) {
        synchronized (this.chatRooms) {
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(roomName)) {
                    this.chatRooms.get(i).addUserOnline(userOnline);

                    return true;
                }
            }
            addRoom(roomName);
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(roomName)) {
                    this.chatRooms.get(i).addUserOnline(userOnline);

                    return true;
                }
            }
            return false;
        }
    }

    public final ChatRoom getChatRoom(final String room) {
        synchronized (this.chatRooms) {
            for (int i = 0; i < this.chatRooms.size(); i++) {
                if (this.chatRooms.get(i).getName().equals(room)) {
                    return this.chatRooms.get(i);
                }
            }
            return null;
        }
    }
}
