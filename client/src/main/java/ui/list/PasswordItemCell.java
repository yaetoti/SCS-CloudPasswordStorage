package ui.list;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import ui.PasswordEntryView;

import java.util.Objects;

public class PasswordItemCell extends ListCell<PasswordItemData> {
  private Parent m_listEntry = null;
  private PasswordEntryView m_entryView = null;

  public PasswordItemCell() {
    try {
      FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/ui/PasswordEntryView.fxml")));
      loader.setControllerFactory(param -> new PasswordEntryView(null));
      m_listEntry = loader.load();
      m_entryView = loader.getController();
    } catch (Exception e) {
      System.out.println("[PasswordItemCell]: Failed to load PasswordEntryView");
      System.out.println("[PasswordItemCell]: " + e.getMessage());
      return;
    }
  }

  @Override
  protected void updateItem(PasswordItemData item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setGraphic(null);
      return;
    }

    if (m_entryView != null) {
      m_entryView.SetData(item);
      setGraphic(m_listEntry);
    }
  }
}
