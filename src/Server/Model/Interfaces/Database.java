package Server.Model.Interfaces;

/**
 * Simple interface which describe contract to storage classes
 */
public interface Database {
    /**
     * Create new user in Database with specific username and hashed password
     *
     * @param username Username of user to login and show in public
     * @param password Password field, must be hashed
     * @return boolean True if creation went successfully
     */
    boolean createUser(String username, String password);

    /**
     * Check if there exist user with specific username
     *
     * @param username Specific name of
     * @return boolean True if user exist
     */
    boolean isUser(String username);

    /**
     * Validates username and password
     *
     * @param username Username of user to login
     * @param password Password which will be checked, must be hashed
     * @return boolean True if username and password are correct
     */
    boolean validateUserAndPassword(String username, String password);
}
