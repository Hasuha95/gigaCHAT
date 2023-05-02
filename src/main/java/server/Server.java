package server;

import DBcomunication.CRUD;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private CRUD crudOperation;
    private AuthService authService;
    private Vector<ClientCreatingServerSupport> clients;

    public Server() {
        authService = new SimpleAuthorisationService();
        clients = new Vector<>();
        final int PORT=1999;
        ServerSocket server;
        Socket socket;
        crudOperation = new CRUD();

        try {
            server = new ServerSocket(PORT);
            System.out.println("server started...");
            crudOperation.connect();
            while (true){
                socket = server.accept();
                System.out.println("client connected...");

                new ClientCreatingServerSupport(this, socket);
            }

        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void mesgToAllClients(String msg){

        for (ClientCreatingServerSupport c: clients) {
            c.sendMsg(msg);          // msg to All clients from server
        }
    }

    public void msgToSpecificClient(String nick, String msg, String nickOfSender){
        for (ClientCreatingServerSupport c: clients) {
            if (c.getNick().equals(nick)){
                c.sendMsg("msg from " + nickOfSender + " to you" + ": " + msg);
            }
        }

    }


    public void subscribe(ClientCreatingServerSupport client) {
        clients.add(client);
//        crudOperation.insert(nick, login, password);
        setListOfClient();

    }

    public void unSubscribe(ClientCreatingServerSupport client){
        clients.remove(client);
        setListOfClient();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isLoginFree(String login){
        for (ClientCreatingServerSupport c: clients) {
            System.out.println(c.getLogin());
            if (c.getLogin().equals(login)){
                return false;
            }
        }
        return true;
    }


    public void setListOfClient(){
        StringBuilder sb = new StringBuilder("/clientlist ");

        for (ClientCreatingServerSupport c: clients) {
            sb.append(c.getNick()).append(" ");
        }

        String msg = sb.toString();
        System.out.println("server/broadcastClientList: " + msg);

        for (ClientCreatingServerSupport c: clients) {
            c.sendMsg(msg);
        }
    }


}
