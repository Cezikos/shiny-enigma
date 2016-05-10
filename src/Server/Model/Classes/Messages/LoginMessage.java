package Server.Model.Classes.Messages;

import Server.Constants;
import Server.Model.Classes.UserForm;
import Server.Model.Interfaces.Message;
import Server.Model.Interfaces.MessageType;
import Server.Model.Interfaces.MessageTypeVisitor;

import java.io.Serializable;

/**
 * Created by Piotr on 2016-05-09.
 */
public class LoginMessage implements Serializable, Message, MessageType {
    private final long ID = System.currentTimeMillis();
    private final UserForm userForm;

    public LoginMessage(final UserForm userForm) {
        this.userForm = userForm;
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
        return null;
    }

    @Override
    public void accept(MessageTypeVisitor messageTypeVisitor) {
        messageTypeVisitor.visit(this);
    }
}
