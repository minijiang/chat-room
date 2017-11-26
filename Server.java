package test;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

/**
 * Created by feiliu on 2017/11/25.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("启动服务器8080端口服务!");
         int  count = 0 ;
        while (true) {
            Socket socket = serverSocket.accept();
            String who = String.valueOf(count);
            System.out.println("接受到新的客户端连接:" + who);
            SocketHandlerImpl socketHandle = new SocketHandlerImpl(socket,who);
            SessionUtils.save(who, socketHandle);
            socketHandle.write("2#" + who);
            count ++;
            Collection<SocketHandlerImpl> socketHandlers = SessionUtils.getUsers();
            for (SocketHandlerImpl n : socketHandlers) {
                if (n != socketHandle) {
                    n.write(who + ",上线了!");
                }
            }

            new Thread(socketHandle).start();
        }
    }
}
