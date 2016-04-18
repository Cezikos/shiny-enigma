package Server;

public class ServerMain {

    public static void main(String args[]) {
        Server server = new Server(7171); //TODO Should ask for specific port
        server.start();
    }

}