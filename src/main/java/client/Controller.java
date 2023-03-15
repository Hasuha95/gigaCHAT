package client;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
public class Controller implements Initializable{
    @FXML
    public TextArea textArea;
    @FXML
    public TextField textField;

    final String IP_ADRESS = "localhost";
    final int PORT=1999;

    Socket clntSocket;
    DataInputStream in;
    DataOutputStream out;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clntSocket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(clntSocket.getInputStream());
            out = new DataOutputStream(clntSocket.getOutputStream());
//Thread
            new Thread(() -> {
                try {
                    while (true) {
                        String msgFromSrv = in.readUTF();
                        if (msgFromSrv.equals("/end")){
                            textArea.appendText("you are disconected");
                            break;
                        }
                        textArea.appendText(msgFromSrv + '\n');
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        clntSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }).start();
//Thread_closed
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMsg(){
        try {
            out.writeUTF(textField.getText());  //msg to server from cloient
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}