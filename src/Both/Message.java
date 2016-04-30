package Both;

import java.io.Serializable;

public class Message implements Serializable { //TODO Serializable need standardize
    /**
     * Unique id of each sent message, used time in milliseconds
     **/
    private final long ID = System.currentTimeMillis();

    /**
     * Universal object to send
     **/
    private final Object object;
    /**
     * Specific code which contain short information about sent/received <code>Object</code>
     **/
    private final Codes code;

    /**
     * Specific channel where message will be send
     * Channel 0, default channel with system messages
     **/
    private final String channel;

    public Message(Object object, Codes code, String channel) {
        this.object = object;
        this.code = code;
        this.channel = channel;
    }

    public Object getObject() {
        return object;
    }

    public Codes getHeader() {
        return code;
    }

    public String getChannel() {
        return channel;
    }

    public long getID() {
        return ID;
    }
}
