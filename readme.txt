Used:
    mysql-connector-java-5.1.38
    BCrypt 2.0

Database:
    CREATE TABLE IF NOT EXISTS USERS (
    	id INT NOT NULL AUTO_INCREMENT,
        login VARCHAR(20) NOT NULL UNIQUE,
        password VARCHAR(72) NOT NULL,
        PRIMARY KEY (id)
        );

¯\_(ツ)_/¯
