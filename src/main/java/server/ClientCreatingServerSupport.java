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
    AuthService authService;
    private String nick;
    private String login;
    private String password;

    public ClientCreatingServerSupport(Server server, Socket clntSocket) {
        this.server = server;
        this.socket = clntSocket;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

//Thread
            new Thread(() -> {
                try {
               //cycle of authentication
                    System.out.println("____cycle of authentication____");
                    while (true) {
                        String msgFromClient = in.readUTF();
                        //   registration
                        if (msgFromClient.startsWith("/reg ")){
                            String token[] = msgFromClient.split(" ");
                            if (token.length < 4) {
                                sendMsg("please fill in the fields");
                                continue;
                            }
                            login = token[1];
                            password = token[2];

                            boolean succeed = server.
                                    getAuthService().
                                    registration(token[1], token[2], token[3]);
                            if (succeed){
                                sendMsg("registration finished");

                            } else {
                                sendMsg("registration fail.'/n'" +
                                        " this login myght be used");
                            }
                        }
                        //  authentication
                        if (msgFromClient.startsWith("/auth ")) {
                            String[] token = msgFromClient.split(" ");
                            if (token.length < 3) {
                                sendMsg("please fill in the fields");
                                continue;
                            }

                            String nickname = server
                                    .getAuthService()
                                    .getNicknameByLoginANDPassword(
                                            token[1],
                                            token[2]
                                    );

                            login = token[1];
                            password = token[2];

                            if (nickname != null && server.isLoginFree(login)) {
                                this.nick = nickname;
                                sendMsg("/authOK " + nickname);
                                server.subscribe(this);

                                break;
                            } else {
                                sendMsg("_false attempt_");
                            }
                        }
                    }
           //cycle of working
                    System.out.println("__________cycle of working__________");
                    while (true) {
                        String msgFromClient = in.readUTF();

                        if (msgFromClient.equals("/end")){
                            System.out.println("client disconnected(");
                            sendMsg("/end");
                            server.unSubscribe(this);
                            socket.close();
                            break;
                        } else if (msgFromClient.startsWith("/w ")) {
                            String[] specificMsg = msgFromClient.split(" ",3);
                            server.msgToSpecificClient(specificMsg[1], specificMsg[2], nick);
                            sendMsg("msg to " + specificMsg[1] + ": " + specificMsg[2]);
                            continue;
                        } else {
                            server.mesgToAllClients(nick + ": " + msgFromClient);
                        }

                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                catch (SQLException e){
//                    e.printStackTrace();
//
//                }
                finally {
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
            out.writeUTF(msg);   //  msg to client
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNick() {
        return nick;
    }

    public String getLogin() {
        return login;
    }
}
