package ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import md2.MD2;
import ui.list.PasswordItemData;
import utils.CipherId;
import utils.EncryptionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RevealPasswordView {
  private final PasswordItemData m_data;
  public Button cancelButton;
  public Button okButton;
  public PasswordField keyField;

  public RevealPasswordView(PasswordItemData data) {
    m_data = data;
  }

  public void onCancel(ActionEvent actionEvent) {
    ((Stage)cancelButton.getScene().getWindow()).close();
  }

  public void onOk(ActionEvent actionEvent) {
    byte[] key = keyField.getText().getBytes(StandardCharsets.UTF_8);
    byte[] keyHash = MD2.Hash(key);
    if (!Arrays.equals(m_data.data.keyHash, keyHash)) {
      System.out.println("[RevealPasswordView]: Invalid key");
      return;
    }

    CipherId cipherId = CipherId.FromId(m_data.data.cipherId);
    if (cipherId == null) {
      System.out.println("[RevealPasswordView]: Invalid cipher id: " + cipherId);
      return;
    }

    m_data.password = new String(EncryptionUtils.Decrypt(cipherId, m_data.data.encrypted_password, key), StandardCharsets.UTF_8);
    ((Stage)cancelButton.getScene().getWindow()).close();
  }
}
