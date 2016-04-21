Used:
    JDK 8u40+ Client
    JDK 7+ Server
    mysql-connector-java-5.1.38
    BCrypt 2.0
    HikariCP-2.4.5

Database:
    CREATE TABLE IF NOT EXISTS USERS (
    	id INT NOT NULL AUTO_INCREMENT,
        login VARCHAR(20) NOT NULL UNIQUE,
        password VARCHAR(72) NOT NULL,
        PRIMARY KEY (id)
        );

TODO: Random order
    -Channels/Rooms
    -Private Channels/Rooms
    -Private Messages
    -RichTextFX
    -RSA cryptosystem
    -Better GUI
    -Refactor Code
    -Decrease bandwidth consumption

¯\_(ツ)_/¯
