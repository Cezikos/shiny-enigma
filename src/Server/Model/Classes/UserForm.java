package Server.Model.Classes;

import java.io.Serializable;

/**
 * Created by Piotr on 2016-04-30.
 */
public class UserForm implements Serializable {
    private final String username;
    private final String password;

    public UserForm(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public final String getUsername() {
        return this.username;
    }

    public final String getPassword() {
        return this.password;
    }
}
