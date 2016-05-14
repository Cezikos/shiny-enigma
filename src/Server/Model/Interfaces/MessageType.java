package Server.Model.Interfaces;

/**
 * Visitor Pattern
 * Each Message class must implement this pattern to get simplicity of developing
 **/
public interface MessageType {

    void accept(MessageTypeVisitor messageTypeVisitor);
}
