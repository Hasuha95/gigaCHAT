package client;

import java.io.IOException;
import java.net.Socket;

public class TestClient {

    public static void main(String[] args) {
        final String IP_ADRESS = "localhost";

        try (Socket socket = new Socket(IP_ADRESS, 1999) ) {
            while (true){

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
