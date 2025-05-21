package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import md2.MD2;
import network.ApplicationState;
import network.payload.PasswordData;
import utils.CipherId;
import utils.EncryptionUtils;

import java.nio.charset.StandardCharsets;

public class AddPasswordView {
  private final ApplicationState m_appState;
  private final MainView m_parentView;
  
  public TextField nameField;
  public PasswordField passwordField;
  public PasswordField keyField;
  public ComboBox<CipherId> cipherBox;
  public Button cancelButton;
  public Button okButton;


  public AddPasswordView(ApplicationState appState, MainView parentView) {
    m_appState = appState;
    m_parentView = parentView;
  }

  @FXML
  public void initialize() {
    cipherBox.getItems().addAll(CipherId.values());
    cipherBox.getSelectionModel().select(0);
  }

  public void onCancel(ActionEvent actionEvent) {
    ((Stage)cancelButton.getScene().getWindow()).close();
  }

  public void onOk(ActionEvent actionEvent) {
    byte[] passwordBytes = passwordField.getText().getBytes(StandardCharsets.UTF_8);
    byte[] keyBytes = keyField.getText().getBytes(StandardCharsets.UTF_8);

    PasswordData data = new PasswordData();
    data.name = nameField.getText();
    data.keyHash = MD2.Hash(keyBytes);

    CipherId cipherId = cipherBox.getValue();
    if (cipherId == null) {
      return;
    }

    data.cipherId = (int)cipherId.GetId();
    data.encrypted_password = EncryptionUtils.Encrypt(cipherId, passwordBytes, keyBytes);

    m_parentView.AddPassword(data);
    ((Stage)cancelButton.getScene().getWindow()).close();
  }
}
