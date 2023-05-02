package DBcomunication;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;



public class CRUD {
    private Connection connection;
    private Statement statement;


    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/gigaChat?user=root&password=HoxaHoxa";

        connection = DriverManager.getConnection(url);
        statement = connection.createStatement();
        System.out.println("package_DBcomunication/class_CRAD:   DB connected");
    }

    public void disConnect(){
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void insert(String nick, String login, String password) throws SQLException {
        statement.executeUpdate("INSERT INTO listOfUsers (nick, login, password) values ("
                + nick
                + ","
                + login
                + ","
                + password);
    }

//    public static void main(String[] args) {
//        try {
//            connect();
//            disConnect();
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
