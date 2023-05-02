package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegController {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nickField;
    @FXML
    Controller controller;


    public void clickOk(ActionEvent actionEvent) {
        controller.tryToRegistration(loginField.getText().trim(),
                passwordField.getText().trim(),
                nickField.getText().trim());
    }

}
