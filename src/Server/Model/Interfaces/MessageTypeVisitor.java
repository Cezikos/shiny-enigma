package Server.Model.Interfaces;

/**
 * Created by Piotr Kucharski on 2016-05-09.
 */

import Server.Model.Classes.Messages.*;


/**
 * Visitor Pattern, to recognize suitable method to take care of the guest
 * In effect we have overloaded method visit, very simple to implement and develop
 **/
public interface MessageTypeVisitor {
    void visit(DisconnectMessage disconnectMessage);

    void visit(FailureMessage failureMessage);

    void visit(JoinRoom joinRoom);

    void visit(LeftRoom leftRoom);

    void visit(LoginMessage loginMessage);

    void visit(RegisterMessage registerMessage);

    void visit(SuccessMessage successMessage);

    void visit(TextMessage textMessage);
}
