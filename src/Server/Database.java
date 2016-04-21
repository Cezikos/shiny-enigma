package Server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    HikariDataSource hikariDataSource;

    public Database() {
        setHikariCP();
    }

    private void setHikariCP() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(20);

        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", "localhost");
        config.addDataSourceProperty("port", "3306");
        config.addDataSourceProperty("databaseName", "chat");
        config.addDataSourceProperty("user", "root");
        config.addDataSourceProperty("password", "1234");

        hikariDataSource = new HikariDataSource(config);
        hikariDataSource.setConnectionTimeout(800);
    }

    public boolean createUser(String username, String password) {
            Connection connection = null;
            PreparedStatement statement = null;
            // Hash a password for the first time
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12)); //TODO Maybe another thread to hash faster???
            try {

                connection = hikariDataSource.getConnection();

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

    public boolean isValidLoginAndPassword(String login, String password) {
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            // Hash a password for the first time
            try {

                connection = hikariDataSource.getConnection();

                /**SQL Injection bye bye**/
                statement = connection.prepareStatement("SELECT password FROM USERS WHERE login=?");
                statement.setString(1, login);

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
