package network;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URI;

public final class ApplicationState {
  public NetworkingService network;
  public AuthService authService;
  public Stage primaryStage;
  public Scene primaryScene;
  public StackPane rootLayout;

  public ApplicationState(URI baseAddress) {
    EndpointService.SetBaseAddress(baseAddress);
    network = new NetworkingService();
    authService = new AuthService(network);
  }
}
