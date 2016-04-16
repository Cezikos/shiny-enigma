//package Server;
//
//import Both.Codes;
//import Both.Message;
//
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.io.PrintStream;
//import java.util.ArrayList;
//
///**
// * Created by Piotr on 2016-04-13.
// */
//public class MessagesSender implements Runnable {
//
//    private String message;
//
//    public MessagesSender(String message) {
//        this.message = message;
//    }
//
//    @Override
//    public void run() {
//        MessagesListener messagesListener;
//        for (int i = 0; i < Server.messagesListenerArrayList.size(); i++) {
//            messagesListener = Server.messagesListenerArrayList.get(i);
//            try {
//                (new ObjectOutputStream(messagesListener.getClientSocket().getOutputStream())).writeObject();
//            } catch (IOException e) {
//                e.printStackTrace();
//                messagesListener.terminate();
//                Server.messagesListenerArrayList.remove(Server.messagesListenerArrayList.get(i));
//                i--;
//            }
//
//        }
//    }
//
//}
