package Server.Model.Classes.Messages;

import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

import java.io.Serializable;

/**
 * Created by Piotr Kucharski on 2016-05-12.
 */
public class SignedTextMessage implements Serializable, Message, MessageType {
    private final long ID;
    private final SignedMessage signedMessage;
    private final String room;

    public SignedTextMessage(long ID, SignedMessage signedMessage, String room) {
        this.ID = ID;
        this.signedMessage = signedMessage;
        this.room = room;
    }

    @Override
    public long getID() {
        return this.ID;
    }

    @Override
    public SignedMessage getMessage() {
        return this.signedMessage;
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
