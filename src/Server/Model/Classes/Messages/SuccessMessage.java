package Server.Model.Classes.Messages;

import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

import java.io.Serializable;

/**
 * Created by Piotr on 2016-05-09.
 */
public class SuccessMessage implements Serializable, Message, MessageType {
    private final long ID;
    private final String message;
    private final String room;

    public SuccessMessage(final long ID, final String message, final String room) {
        this.ID = ID;
        this.message = message;
        this.room = room;
    }

    @Override
    public long getID() {
        return this.ID;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getRoom() {
        return this.room;
    }

    @Override
    public void accept(MessageTypeVisitor messageTypeVisitor) {
        messageTypeVisitor.visit(this);
    }
}
