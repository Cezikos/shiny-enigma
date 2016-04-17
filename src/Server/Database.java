package Server;

import Both.LoginForm;

import java.util.ArrayList;

/**
 * Created by Piotr on 2016-04-17.
 */
public class Database {
    private Object lockRegisteredUsers;
    private ArrayList<LoginForm> registeredUsers; //Temporary database.

    public Database(){
        lockRegisteredUsers = new Object();
        registeredUsers = new ArrayList<>();
    }

    public boolean registerUser(String login, String password){
        synchronized (lockRegisteredUsers){
            for(int i=0; i<registeredUsers.size(); i++){
                if(registeredUsers.get(i).getLogin().equals(login)){
                    return false;
                }
            }

            registeredUsers.add(new LoginForm(login, password));
            return true;
        }
    }

    public boolean isValidLoginAndPassword(String login, String password){
        synchronized (lockRegisteredUsers){
            for(int i=0; i<registeredUsers.size(); i++){
                if(registeredUsers.get(i).getLogin().equals(login) && registeredUsers.get(i).getPassword().equals(password)){
                    return true;
                }
            }
            return false;
        }
    }
}
