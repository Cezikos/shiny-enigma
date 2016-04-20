package Server;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class Database {
    /**
     * Object locker to synchronize actions on MySQL DB
     **/ //TODO Necessary?
    private Object locker;

    private Connection connection;
    private Statement statement;

    public Database() {
        locker = new Object();
        connection = null;
        statement = null;

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
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); //12 rounds

                /**SQL Injection bye bye**/
                PreparedStatement statement = connection.prepareStatement("INSERT INTO USERS (login, password) VALUES (?, ?)");
                statement.setString(1, username);
                statement.setString(2, hashedPassword);
                statement.execute();
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
                /**SQL Injection bye bye**/
                PreparedStatement stmt = connection.prepareStatement("SELECT password FROM USERS WHERE login=?");
                stmt.setString(1, login);
                ResultSet resultSet = stmt.executeQuery();

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


}
