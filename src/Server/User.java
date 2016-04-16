package Server;

import java.io.Serializable;

public class User implements Serializable { //TODO Serializable, need standard! Maybe change name to ServerUser??
    private final String username;

    public User(String username) {
        this.username = new String(username);
    }

    public String getUsername() {
        return username;
    }

}
