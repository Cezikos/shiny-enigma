package Server.Model.Classes;


import Server.Model.Interfaces.Database;

import java.util.Hashtable;

/**
 * Created by Piotr on 2016-04-30.
 */
public class MySQL implements Database {
    private Hashtable<String, String> user;


    public MySQL() {
        this.user = new Hashtable<>(10);
    }


    public final boolean createUser(final String username, final String password) {
//        if (!isUser(username)) {
            this.user.put(username, password);
            return true;
//        }
//        return false;
    }

    public final boolean isUser(final String username) {
        return this.user.containsKey(username);
    }

    public boolean validateUserAndPassword(final String username, final String password) {
        return isUser(username) && this.user.get(username).equals(password);
    }
}
