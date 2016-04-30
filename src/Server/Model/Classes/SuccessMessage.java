package Server.Model.Classes;

import Server.Model.Interfaces.Message;
import Server.Model.Enums.SuccessCodes;

import java.io.Serializable;

/**
 * Created by Piotr on 2016-04-30.
 */
public class SuccessMessage implements Message, Serializable {
    private final long ID;
    private final SuccessCodes successCodes;
    private final String room;

    public SuccessMessage(final long ID, final SuccessCodes successCodes, final String room) {
        this.ID = ID;
        this.successCodes = successCodes;
        this.room = room;
    }

    public final long getId() {
        return this.ID;
    }

    public final SuccessCodes getMessage() {
        return this.successCodes;
    }

    public final String getRoom() {
        return this.room;
    }
}
