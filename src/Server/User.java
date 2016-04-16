package Server;

import java.io.Serializable;

public class User implements Serializable { //TODO Serializable, need standard! Maybe change name to ServerUser??
    private final String USERNAME;

    public User(String username) {
        this.USERNAME = new String(username);
    }

    public String getUsername() {
        return USERNAME;
    }

}
