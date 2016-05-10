package Server.Model.Classes.Messages;


import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

import java.io.Serializable;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public class TextMessage implements Serializable, Message, MessageType {
    private final long ID = System.currentTimeMillis();
    private final String message;
    private final String room;

    public TextMessage(final String message, final String room) {
        this.message = message;
        this.room = room;
    }

    @Override
    public final long getID() {
        return this.ID;
    }

    @Override
    public final String getMessage() {
        return this.message;
    }

    @Override
    public final String getRoom() {
        return this.room;
    }

    @Override
    public void accept(MessageTypeVisitor messageTypeVisitor) {
        messageTypeVisitor.visit(this);
    }
}
