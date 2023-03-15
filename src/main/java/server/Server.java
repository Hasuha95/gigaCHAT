package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private Vector<ClientCreatingServerSupport> clients;

    public Server() {
        clients = new Vector<>();
        final int PORT=1999;
        ServerSocket server;
        Socket socket;

        try {
            server = new ServerSocket(PORT);
            System.out.println("server started...");

            while (true){
                socket = server.accept();
                System.out.println("client connected...");
                subscribe(new ClientCreatingServerSupport(this, socket));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mesgToAllClients(String msg){
        for (ClientCreatingServerSupport c: clients) {
            c.sendMsg(msg);
        }
    }

    public void subscribe(ClientCreatingServerSupport client){
        clients.add(client);
    }

    public void unSubscribe(ClientCreatingServerSupport client){
        clients.remove(client);
    }
}
