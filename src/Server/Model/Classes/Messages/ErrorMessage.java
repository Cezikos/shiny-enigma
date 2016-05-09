package Server.Model.Classes.Messages;


import Server.Model.Enums.ErrorCodes;
import Server.Model.Interfaces.Message;

import java.io.Serializable;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public class ErrorMessage implements Message, Serializable {
    private final long ID;
    private final ErrorCodes errorCode;
    private final String room;

    public ErrorMessage(final long ID, final ErrorCodes errorCode, final String room) {
        this.ID = ID;
        this.errorCode = errorCode;
        this.room = room;
    }

    public final long getId() {
        return this.ID;
    }

    public final ErrorCodes getMessage() {
        return this.errorCode;
    }

    public final String getRoom() {
        return this.room;
    }
}
