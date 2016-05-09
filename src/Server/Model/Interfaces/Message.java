package Server.Model.Interfaces;

/**
 * Created by Piotr Kucharski on 2016-04-30.
 */
public interface Message {
    long getID();

    Object getMessage();

    String getRoom();


}
