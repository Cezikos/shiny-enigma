package Server.Model.Interfaces;

/**
 * Simple interface which describe contract between messages classes
 */
public interface Message {
    /**
     * Contain unique identifier of message from user.
     * Based on current UNIX time in milliseconds
     *
     * @return long UNIX time in milliseconds
     */
    long getID();

    /**
     * @return Object Return specific message based on message specification
     */
    Object getMessage();

    /**
     * @return String Room name where message belong
     */
    String getRoom();
}
