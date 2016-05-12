package Server.Model.Classes.Messages;

import java.io.Serializable;

/**
 * Created by Piotr Kucharski on 2016-05-12.
 */
public class SignedMessage implements Serializable {
    private final String author;
    private final String message;

    public SignedMessage(String author, String message) {
        this.author = author;
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }
}
