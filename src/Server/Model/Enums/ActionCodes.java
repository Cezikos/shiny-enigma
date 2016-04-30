package Server.Model.Enums;

import java.io.Serializable;

/**
 * Created by Piotr on 2016-04-30.
 */
public enum ActionCodes implements Serializable {
    REGISTER,
    LOGIN,
    JOIN_ROOM,
    LEAVE_ROOM,
    DISCONNECT
}
