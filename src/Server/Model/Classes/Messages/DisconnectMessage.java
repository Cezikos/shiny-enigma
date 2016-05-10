package Server.Model.Classes.Messages;

import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

import java.io.Serializable;

/**
 * Created by Piotr on 2016-05-09.
 */
public class DisconnectMessage implements Serializable, Message, MessageType {
    @Override
    public long getID() {
        return 0;
    }

    @Override
    public Object getMessage() {
        return null;
    }

    @Override
    public String getRoom() {
        return null;
    }

    @Override
    public void accept(MessageTypeVisitor messageTypeVisitor) {
        messageTypeVisitor.visit(this);
    }
}
