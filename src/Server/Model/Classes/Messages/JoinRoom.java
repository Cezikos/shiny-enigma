package Server.Model.Classes.Messages;

import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

/**
 * Created by Piotr Kucharski on 2016-05-09.
 */
public class JoinRoom implements Message, MessageType {
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
