package server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthorisationService implements AuthService {

/*  interior Class*/
    private class UserdData{
        private String login;
        private String password;
        private String nickname;

        public UserdData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }
/*  end of interior Class*/

    private List<UserdData> users;

    public SimpleAuthorisationService() {
        users=new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            users.add(new UserdData(i+"", i + "", "nick"+i));
        }

    }

    @Override
    public String getNicknameByLoginANDPassword(String login, String passwird) {

        for (UserdData o: users) {
            if (o.login.equals(login) && o.password.equals(passwird)){
                return o.nickname;
            }
        }
        return null;
    }

    @Override
    public boolean registration(String login, String passwird, String nickname) {
        for (UserdData o: users) {
            if (o.login.equals(login)){
                return false;
            }
        }
        users.add(new UserdData(login, passwird, nickname)); 
        return true;
    }
}
