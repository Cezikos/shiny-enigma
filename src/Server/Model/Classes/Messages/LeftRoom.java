package Server.Model.Classes.Messages;

import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

import java.io.Serializable;

/**
 * Created by Piotr Kucharski on 2016-05-09.
 */
public class LeftRoom implements Serializable, Message, MessageType {
    private final long ID = System.currentTimeMillis();
    private final String room;

    public LeftRoom(final String room) {
        this.room = room;
    }

    @Override
    public long getID() {
        return this.ID;
    }

    @Override
    public Object getMessage() {
        return null;
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
