package Both;

import java.io.Serializable;

/**
 * Created by Piotr on 2016-04-15.
 */
public class LoginForm implements Serializable{ //TODO Serializable need standardize
    private final String login;
    private final String password;  //TODO Need security, hash.

    public LoginForm(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
