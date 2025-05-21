package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import ui.list.PasswordItemData;

import java.util.Objects;

public class PasswordEntryView {
  private PasswordItemData m_data;
  public Button copyButton;

  @FXML
  private HBox hBox;
  @FXML
  private BorderPane borderPane;
  @FXML
  private Label label;
  @FXML
  private Button deleteButton;

  public PasswordEntryView(@Nullable PasswordItemData data) {
    m_data = data;
  }

  public void SetData(@Nullable PasswordItemData data) {
//    if (m_data != null) {
//      m_data.password = null;
//    }

    m_data = data;
    if (m_data == null) {
      return;
    }

    if (m_data.password == null) {
      label.setText(m_data.data.name);
      label.getStyleClass().removeAll("revealed");
    }
    else {
      label.setText(m_data.password);
      label.getStyleClass().add("revealed");
    }
  }

  @FXML
  public void initialize() {
    if (m_data == null) {
      return;
    }

    if (m_data.password == null) {
      label.setText(m_data.data.name);
    }
    else {
      label.setText(m_data.password);
    }
  }

  public void handleDelete(ActionEvent actionEvent) {
    if (m_data == null) {
      return;
    }

    m_data.parent.DeletePassword(m_data);
  }

  public void handleReveal(MouseEvent mouseEvent) {
    if (m_data == null) {
      return;
    }

    if (m_data.password == null) {
      ShowRequestWindow();

      if (m_data.password == null) {
        label.setText(m_data.data.name);
        label.getStyleClass().removeAll("revealed");
      }
      else {
        label.setText(m_data.password);
        label.getStyleClass().add("revealed");
      }

      return;
    }

    m_data.password = null;
    label.getStyleClass().removeAll("revealed");
    label.setText(m_data.data.name);
  }

  private void ShowRequestWindow() {
    try {
      Stage stage = new Stage();
      stage.setTitle("Reveal password");
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(m_data.state.primaryStage);

      FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("RevealPasswordView.fxml")));
      loader.setControllerFactory(param -> new RevealPasswordView(m_data));
      Scene scene = new Scene(loader.load());
      stage.setScene(scene);
      stage.showAndWait();
    } catch (Exception e) {
      System.out.println("[PasswordEntryView]: Failed to load RevealPasswordView");
      System.out.println(e.getMessage());
      return;
    }
  }

  public void onCopy(ActionEvent actionEvent) {
    if (m_data == null) {
      return;
    }

    if (m_data.password == null) {
      ShowRequestWindow();
    }

    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(m_data.password);
    clipboard.setContent(content);
    m_data.password = null;
  }
}
