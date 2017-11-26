package test;

import java.net.Socket;

/**
 * Created by feiliu on 2017/11/25.
 */
public class Client {
    public static void main(String[] args) throws Exception {
//192.168.6.163
        Socket socket = new Socket("localhost", 8080);
        SocketHandlerImpl socketHandler = new SocketHandlerImpl(socket,"");
        socketHandler.run();




    }
}
