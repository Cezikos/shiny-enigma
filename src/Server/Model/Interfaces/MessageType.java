package Server.Model.Interfaces;


/**
 * Created by Piotr Kucharski on 2016-05-09.
 */

/**
 * Visitor Pattern
 * Each Message class must implement this pattern to get simplicity of developing
 **/
public interface MessageType {
    void accept(MessageTypeVisitor messageTypeVisitor);
}
