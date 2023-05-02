package server;

public interface AuthService {

    String getNicknameByLoginANDPassword(String login, String passwird);
    boolean registration (String login, String passwird, String nickname);
}
