package Server;

/**
 * Class which represents each room in the server
 * "system" is default room where each client must be connected after Log In to Join/Left specific room
 */
public class ChatRoom {
    private final String ROOM_ID;
    private UsersOnlineList usersOnlineList;

    public ChatRoom(String ROOM_ID) {
        this.ROOM_ID = ROOM_ID;
        usersOnlineList = new UsersOnlineList();
    }

    public String getROOM_ID() {
        return ROOM_ID;
    }

    public void addUser(UserOnline userOnline) {
        usersOnlineList.addObserver(userOnline);
        usersOnlineList.sendUsersListToUser(userOnline.getSocket());
    }

    public UsersOnlineList getUsersOnlineList() {
        return usersOnlineList;
    }

    public void closeRoom() {

    }

}
