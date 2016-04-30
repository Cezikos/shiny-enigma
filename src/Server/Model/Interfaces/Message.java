package Server.Model.Interfaces;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public interface Message {
    long getId();

    Object getMessage();

    String getRoom();
}
