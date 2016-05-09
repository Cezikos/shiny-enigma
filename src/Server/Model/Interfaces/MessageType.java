package Server.Model.Interfaces;


/**
 * Created by Piotr Kucharski on 2016-05-09.
 */
/**Visitor Pattern**/
public interface MessageType {
    void accept(MessageTypeVisitor messageTypeVisitor);
}
