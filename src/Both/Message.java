package Both;

import java.io.Serializable;

public class Message implements Serializable { //TODO Serializable need standardize
    /**Unique id of each sent message, used time in milliseconds**/
    private final long ID = System.currentTimeMillis();

    /**Universal object to send**/
    private Object object;
    /**Specific code which contain short information about sent/received <code>Object</code>**/
    private Codes code;


    public Message(Object object, Codes code) {
        this.object = object;
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public Codes getHeader() {
        return code;
    }

    public long getID() {
        return ID;
    }
}
