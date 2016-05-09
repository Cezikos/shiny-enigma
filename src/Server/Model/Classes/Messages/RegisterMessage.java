package Server.Model.Classes.Messages;

import Server.Model.Classes.UserForm;
import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

/**
 * Created by Piotr on 2016-05-09.
 */
public class RegisterMessage implements Message, MessageType {
    private final long ID = System.currentTimeMillis();
    private final UserForm userForm;
    private final String room;

    public RegisterMessage(final UserForm userForm, final String room) {
        this.userForm = userForm;
        this.room = room;
    }

    @Override
    public long getID() {
        return this.ID;
    }

    @Override
    public Object getMessage() {
        return this.userForm;
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
