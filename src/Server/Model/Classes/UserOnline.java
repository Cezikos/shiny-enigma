package Server.Model.Classes;


import java.net.Socket;

/**
 * Created by Piotr Kucharski on 2016-04-29.
 */
public class UserOnline {
    private final String username;
    private final Socket socket;

    public UserOnline(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
    }

    public final String getUsername() {
        return this.username;
    }

    public final Socket getSocket() {
        return this.socket;
    }
}
