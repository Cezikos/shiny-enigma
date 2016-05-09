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
    boolean visit(DisconnectMessage disconnectMessage);

    boolean visit(FailureMessage failureMessage);

    boolean visit(JoinRoom joinRoom);

    boolean visit(LeftRoom leftRoom);

    boolean visit(LoginMessage loginMessage);

    boolean visit(RegisterMessage registerMessage);

    boolean visit(SuccessMessage successMessage);

    boolean visit(TextMessage textMessage);
}
