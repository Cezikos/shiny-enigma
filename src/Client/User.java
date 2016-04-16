package Client;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class User implements Serializable{ //TODO Serializable, need standard! Maybe change name to ClientUser??
    private final SimpleStringProperty USERNAME;

    public User(String username) {
        this.USERNAME = new SimpleStringProperty(username);
    }

    public String getUsername() {
        return USERNAME.get();
    }


}
