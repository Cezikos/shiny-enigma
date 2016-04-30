package Server.Model.Interfaces;

/**
 * Created by Piotr on 2016-04-30.
 */
public interface Database {
    boolean createUser(String username, String password);

    boolean isUser(String username);

    boolean validateUserAndPassword(String username, String password);
}
