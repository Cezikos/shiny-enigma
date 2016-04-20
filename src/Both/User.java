package Both;

import java.io.Serializable;

public class User implements Serializable { //TODO Serializable, need standard!
    private final String USERNAME;

    public User(String username) {
        this.USERNAME = username;
    }

    public String getUsername() {
        return USERNAME;
    }


}
