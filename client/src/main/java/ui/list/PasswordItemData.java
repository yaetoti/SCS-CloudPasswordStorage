package ui.list;

import network.ApplicationState;
import network.payload.ServerPasswordData;
import ui.MainView;

public class PasswordItemData {
  public ApplicationState state;
  public ServerPasswordData data;
  public MainView parent;
  public String password;

  public PasswordItemData(ApplicationState state, ServerPasswordData data, MainView parent) {
    this.state = state;
    this.data = data;
    this.parent = parent;
    this.password = null;
  }
}
