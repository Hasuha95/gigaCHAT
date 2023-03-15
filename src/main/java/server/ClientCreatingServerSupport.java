package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientCreatingServerSupport {

    Server server;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public ClientCreatingServerSupport(Server server, Socket clntSocket) {
        this.server = server;
        this.socket = clntSocket;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
//Thread
            new Thread(() -> {
                try {
                    while (true) {
                        String msgFromClient = in.readUTF();
                        System.out.println("msg from client: " + msgFromClient);

                        if (msgFromClient.equals("/end")){
                            sendMsg(msgFromClient);
                            System.out.println("client disconnected(");
                            break;
                        }

                        server.mesgToAllClients(msgFromClient);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    server.unSubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
//Thread_close.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);   // msg to All clients from server
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
