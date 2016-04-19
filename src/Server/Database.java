package Server;

import Both.Codes;
import Both.Message;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    /**
     * Object locker to synchronize actions on MySQL DB
     **/ //TODO Necessary?
    private Object locker;

    private Connection connection;
    private Statement statement;

    /**
     * Object locker to synchronize ArrayList with online users
     **/
    private static Object lockOnlineUsers;
    /**
     * ArrayList which contain all online users
     **/
    private ArrayList<UserOnline> onlineUsersArrayList;

    public Database() {
        locker = new Object();
        connection = null;
        statement = null;
        lockOnlineUsers = new Object();
        onlineUsersArrayList = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean connectToMySQLServer() {
        try {

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "1234");

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (connection != null) {
            return true;
        }

        return false;
    }

    public boolean connectToDatabase(String database) {
        if (connection != null) {
            try {
                ResultSet catalogs = connection.getMetaData().getCatalogs();
                while (catalogs.next()) {
                    if (catalogs.getString("TABLE_CAT").equals(database)) {
                        connection.setCatalog(database);
                        statement = connection.createStatement(); //statement must be created after DB change
                        return true;
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Synchronized functions to work on the MySQL Database of users
     **/
    public boolean createUser(String username, String password) {
        synchronized (locker) {
            try {
                // Hash a password for the first time
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                statement.execute("INSERT INTO USERS (login, password) VALUES ('" + username + "', '" + hashedPassword + "')");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    public boolean isValidLoginAndPassword(String login, String password) {
        synchronized (locker) {
            try {
                ResultSet resultSet = statement.executeQuery("SELECT password FROM USERS WHERE login='" + login + "'");

                if (resultSet.next()) {
                    if (BCrypt.checkpw(password, resultSet.getString("password"))) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    /**
     * Synchronized functions to work on the ArrayList of Users Online
     **/
    public void sendAllUsersOnlineToAll() { //TODO Is it necessary? Maybe, /users command
        synchronized (lockOnlineUsers) {
            ArrayList<String> usersToSend = new ArrayList<>();

            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                usersToSend.add(onlineUsersArrayList.get(i).getUser().getUsername());
            }

            sendObjectToAllUsers(new Message(usersToSend, Codes.USERS_LIST));
        }
    }

    public void sendAllUsersOnlineToUser(Socket socket) {
        synchronized (lockOnlineUsers) {
            ArrayList<String> usersToSend = new ArrayList<>();

            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                usersToSend.add(onlineUsersArrayList.get(i).getUser().getUsername());
            }

            sendObjectToUser(new Message(usersToSend, Codes.USERS_LIST), socket);
        }
    }

    public void sendObjectToUser(Message message, Socket socket) {
        synchronized (lockOnlineUsers) {
            try {
                (new ObjectOutputStream(socket.getOutputStream())).writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addUserOnlineAndSendToAll(UserOnline userOnline) {
        synchronized (lockOnlineUsers) {
            onlineUsersArrayList.add(userOnline);
            sendObjectToAllUsers(new Message(userOnline.getUser().getUsername(), Codes.USER_JOIN));
        }
    }

    public void removeUsersOnlineAndSendToAll(ArrayList<UserOnline> arrayList) {
        synchronized (lockOnlineUsers) {

            UserOnline userOnline;
            for (int i = 0; i < arrayList.size(); i++) {
                userOnline = arrayList.get(i);
                userOnline.getMessagesListener().terminate();
                onlineUsersArrayList.remove(userOnline);
                sendObjectToAllUsers(new Message(userOnline.getUser().getUsername(), Codes.USER_LEFT));
            }
        }
    }

    public void removeUserOnlineAndSendToAll(Socket socket) {
        synchronized (lockOnlineUsers) {
            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                if (onlineUsersArrayList.get(i).getMessagesListener().getClientSocket() == socket) {
                    onlineUsersArrayList.get(i).getMessagesListener().terminate();
                    String s = onlineUsersArrayList.get(i).getUser().getUsername();
                    onlineUsersArrayList.remove(i);
                    i = onlineUsersArrayList.size();
                    sendObjectToAllUsers(new Message(s, Codes.USER_LEFT));

                }
            }
        }
    }

    public UserOnline getUserOnline(int index) { //TODO Is it necessary?
        synchronized (lockOnlineUsers) {
            return onlineUsersArrayList.get(index);
        }
    }

    public void sendObjectToAllUsers(Message message) {
        synchronized (lockOnlineUsers) {
            ArrayList<UserOnline> usersToDelete = new ArrayList<>();
            UserOnline userOnline;

            for (int i = 0; i < onlineUsersArrayList.size(); i++) {
                userOnline = onlineUsersArrayList.get(i);
                try {
                    (new ObjectOutputStream(userOnline.getMessagesListener().getClientSocket().getOutputStream())).writeObject(message);
                } catch (IOException e) {
                    usersToDelete.add(onlineUsersArrayList.get(i));
                    e.printStackTrace();
                }
            }

            removeUsersOnlineAndSendToAll(usersToDelete);
        }
    }
}
