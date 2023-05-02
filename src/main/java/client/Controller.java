package client;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

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
    @FXML
    final String IP_ADRESS = "localhost";
    @FXML
    final int PORT=1999;
    @FXML
    public HBox authPanel;
    @FXML
    public HBox msgPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public ListView<String> clientList;

    Stage regStage;
    Socket clntSocket;
    DataInputStream in;
    DataOutputStream out;
    private boolean authenticated;
    private String nick;

/*   METHOD_setAuthenticated*/

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        msgPanel.setManaged(authenticated);
        msgPanel.setVisible(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        if (!authenticated) {
            nick = "";
        }
        textArea.clear();
        setTitle(nick);

    }
/*   END OF METHOD_setAuthenticated*/



/*   METHOD_INITIALIZE  */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAuthenticated(false);

        regStage = createRegWindow();

        Platform.runLater(() -> {
            Stage stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.out.println("bue");
                    if (clntSocket != null && !clntSocket.isClosed()) {
                        try {
                            out.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });

    }
/*  END OF METHOD_INITIALIZE  */


/*   METHOD_connect   */
    public void connect(){
        try {
            clntSocket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(clntSocket.getInputStream());
            out = new DataOutputStream(clntSocket.getOutputStream());
//Thread
            new Thread(() -> {
                try {
         //cycle of authentication
                    while (true) {
                        String msgFromSrv = in.readUTF();
                        textArea.clear();
                        if (msgFromSrv.startsWith("/authOK")){
                            nick = msgFromSrv.split(" ")[1];
                            setAuthenticated(true);
                            break;
                         } else {
                            textArea.appendText(msgFromSrv);
                        }
                    }
         //cycle of working
                    while (true) {
                        String msgFromSrv = in.readUTF();
                        if (msgFromSrv.startsWith("/")){
                            if (msgFromSrv.equals("/end")){
                                setAuthenticated(false);
                                textArea.clear();
                                textArea.appendText("you are disconected");
                                clntSocket.close();
                                break;
                            }
                            if (msgFromSrv.startsWith("/clientlist ")){
                                String[] token = msgFromSrv.split(" ");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }
                                });
                            }
                        } else {
                            textArea.appendText(msgFromSrv + '\n');
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException();
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
/*  END OF METHOD_connect*/


/*  METHOD_sendMsg*/
    @FXML
    public void sendMsg(){
        if (textField.getText().equals("") || textField.getText().equals(" ")) {
            textField.appendText("write something");
        } else {
            try {
                out.writeUTF(textField.getText());  //msg to server from client
                textField.clear();
                textField.requestFocus();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
/*  END OF METHOD_sendMsg*/

    @FXML
    public void tryToAuthenticated(ActionEvent actionEvent) {
        if (clntSocket == null || clntSocket.isClosed()){
            connect();
        }
        if (loginField.getText().equals("") && passwordField.getText().equals("")){
            textArea.clear();
            textArea.appendText("please fill in the fields");
        }
        try {
            out.writeUTF("/auth "+ loginField.getText().trim() + " " + passwordField.getText().trim());
            passwordField.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void setTitle(String nickTitle){
        Platform.runLater(()->{
            ((Stage) textField.getScene().getWindow()).setTitle("<<GigaChaT>> " + nickTitle);
                });
    }

    @FXML
    public void clickClientList(MouseEvent mouseEvent) {
        System.out.println(clientList.getSelectionModel().getSelectedItem());
        String receiver = clientList.getSelectionModel().getSelectedItem();
        textField.setText("/w " + receiver + " ");
    }

    private Stage createRegWindow() {
        Stage stage = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reg.fxml"));
            Parent root = fxmlLoader.load();

            stage = new Stage();
            stage.setTitle("Registration ");
            stage.setScene(new Scene(root, 300, 200));
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);

            RegController regController = fxmlLoader.getController();
            regController.controller = this;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stage;
    }

    public void showRegWindow(ActionEvent actionEvent) {
        regStage.show();
    }

    public void tryToRegistration(String login, String password, String nickname){
//        String msg = String.format("/reg $s $s $s", login, password, nickname);
        String msg ="/reg" + " " + login + " " + password + " " + nickname;

        if (clntSocket == null || clntSocket.isClosed()){
            connect();
        }

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
