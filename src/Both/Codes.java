package Both;

public enum Codes { //TODO Split into to categories? ERROR and Something??
    /**
     * Plain text to the room
     **/
    SIMPLE_MESSAGE,
    /**
     * Confirmation about proof delivery
     **/
    CONFIRMATION,
    /**
     * Client send it when is disconnecting
     **/
    DISCONNECT,
    /**
     * Client send it when would like to register
     **/
    REGISTER,
    /**
     * Server send it when registration went successfully
     **/
    SUCCESSFUL_REGISTER,
    /**
     * Server send it when registration failure
     **/
    FAILURE_REGISTER,
    /**
     * Client send it when would like to login
     **/
    LOGIN,
    /**
     * Server send it when login went successfully
     **/
    SUCCESSFUL_LOGIN,
    /**
     * Server send it when login failure
     **/
    FAILURE_LOGIN,
    /**
     * Server send it with users list
     **/
    USERS_LIST,
    /**
     * Server send it when someone join the room/server
     **/
    USER_JOIN,
    /**
     * Server send it when someone left the room/server
     **/
    USER_LEFT
}
