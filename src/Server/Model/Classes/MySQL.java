package Server.Model.Classes;


import Server.Model.Interfaces.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import Server.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Piotr on 2016-04-30.
 */
public class MySQL implements Database {
    private HikariDataSource hikariDataSource;

    public MySQL() {
        this.setHikariCP();
    }

    private void setHikariCP() {
        final HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(20);

        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("port", "3306");
        config.addDataSourceProperty("databaseName", "chat");
        config.addDataSourceProperty("user", "root");
        config.addDataSourceProperty("password", "1234");

        this.hikariDataSource = new HikariDataSource(config);
        this.hikariDataSource.setConnectionTimeout(800);
    }


    public final boolean createUser(final String username, final String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        // Hash a password for the first time
        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        try {

            connection = this.hikariDataSource.getConnection();

            /**SQL Injection bye bye**/
            statement = connection.prepareStatement("INSERT INTO USERS (login, password) VALUES (?, ?)");
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public final boolean isUser(final String username) { //TODO Need to implement
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = this.hikariDataSource.getConnection();

            statement = connection.prepareStatement("SELECT IF (EXISTS (SELECT login FROM USERS WHERE login=?), 1, 0) AS 'isUser';"); //TODO Optimize, change to "if exist" boolean.
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getBoolean(1)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean validateUserAndPassword(final String username, final String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        // Hash a password for the first time
        try {

            connection = hikariDataSource.getConnection();

            /**SQL Injection bye bye**/
            statement = connection.prepareStatement("SELECT password FROM USERS WHERE login=?");
            statement.setString(1, username);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                if (BCrypt.checkpw(password, resultSet.getString("password"))) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }
}
