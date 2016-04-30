package Server.Model.Classes;


import Server.Model.Interfaces.Message;

import java.io.Serializable;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public class TextMessage implements Message, Serializable {
    private final long ID = System.currentTimeMillis();
    private final String message;
    private final String room;

    public TextMessage(final String message, final String room) {
        this.message = message;
        this.room = room;
    }

    public final long getId() {
        return this.ID;
    }

    public final String getMessage() {
        return this.message;
    }

    public final String getRoom() {
        return this.room;
    }
}
