package Server.Model.Classes.Messages;


import Server.Model.Enums.ActionCodes;
import Server.Model.Interfaces.Message;

import java.io.Serializable;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public class ActionMessage implements Message, Serializable {
    private final long id;
    private final Object object;
    private final String room;

    private final ActionCodes code;

    public ActionMessage(final Object object, final String room, final ActionCodes code) {
        this.id = System.currentTimeMillis();
        this.object = object;
        this.room = room;
        this.code = code;
    }

    public final long getId() {
        return this.id;
    }

    public final Object getMessage() {
        return this.object;
    }

    public final String getRoom() {
        return this.room;
    }

    public final ActionCodes getCode() {
        return this.code;
    }
}
