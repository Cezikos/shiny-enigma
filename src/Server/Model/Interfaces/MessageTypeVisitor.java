package Server.Model.Interfaces;

/**
 * Created by Piotr Kucharski on 2016-05-09.
 */

import Server.Model.Classes.Messages.*;


/**Visitor Pattern**/
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
