package ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.ApplicationState;
import network.EndpointService;
import network.payload.PasswordData;
import network.payload.ServerPasswordData;
import ui.list.PasswordItemCell;
import ui.list.PasswordItemData;

import java.net.http.HttpRequest;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainView {
  private final ApplicationState m_appState;

  public ListView<PasswordItemData> list;
  public Button refreshButton;
  public Button addButton;

  public AtomicBoolean m_updating;

  public MainView(ApplicationState appState) {
    m_appState = appState;
    m_updating = new AtomicBoolean(false);
  }

  public void RefreshPasswords() {
    if (!m_updating.compareAndSet(false, true)) {
      return;
    }

    // Send request, reset afterwards
    HttpRequest httpRequest = HttpRequest.newBuilder()
      .uri(EndpointService.GetPasswordMapping())
      .GET()
      .header("Authorization", "Bearer " + m_appState.network.Serialize(m_appState.authService.GetAccessToken()))
      .header("Content-Type", "application/json")
      .timeout(java.time.Duration.ofSeconds(5))
      .build();
    m_appState.network.SendRequest(httpRequest).thenAcceptAsync(response -> {
      if (response.statusCode() / 100 != 2) {
        System.out.println("[MainView]: Failed to get passwords");
        m_updating.set(false);
        return;
      }

      try {
        ServerPasswordData[] passwords = m_appState.network.Deserialize(response.body(), ServerPasswordData[].class);

        list.getItems().clear();
        if (passwords == null || passwords.length == 0) {
          System.out.println("[MainView]: No passwords found");
          m_updating.set(false);
          return;
        }

        PasswordItemData[] items = new PasswordItemData[passwords.length];
        for (int i = 0; i < passwords.length; ++i) {
          items[i] = new PasswordItemData(m_appState, passwords[i], this);
        }

        list.getItems().addAll(items);
        System.out.println("[MainView]: Passwords loaded");
        m_updating.set(false);
      } catch (Exception e) {
        System.out.println("[MainView]: Failed to get passwords");
        System.out.println("[MainView]: " + e.getMessage());
        m_updating.set(false);
        return;
      }
    }, Platform::runLater)
      .exceptionally(throwable -> {
        System.out.println("[MainView]: Failed to get passwords");
        System.out.println("[MainView]: " + throwable.getMessage());
        m_updating.set(false);
        return null;
      });
  }

  public void DeletePassword(PasswordItemData element) {
    if (!m_updating.compareAndSet(false, true)) {
      return;
    }

    // Send request, delete afterwards
    HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(EndpointService.GetPasswordDeleteMapping(element.data.id.toString()))
        .DELETE()
        .header("Authorization", "Bearer " + m_appState.network.Serialize(m_appState.authService.GetAccessToken()))
        .header("Content-Type", "application/json")
        .timeout(java.time.Duration.ofSeconds(5))
        .build();
      m_appState.network.SendRequest(httpRequest).thenAcceptAsync(response -> {
        if (response.statusCode() / 100 != 2) {
          System.out.println("[MainView]: Failed to delete password");
          System.out.println("[MainView]: " + response.statusCode());
          m_updating.set(false);
          return;
        }

        list.getItems().remove(element);
        m_updating.set(false);
      }, Platform::runLater)
      .exceptionally(throwable -> {
        System.out.println("[MainView]: Failed to delete");
        System.out.println("[MainView]: " + throwable.getMessage());
        m_updating.set(false);
        return null;
      });
  }

  public void AddPassword(PasswordData data) {
    if (!m_updating.compareAndSet(false, true)) {
      return;
    }

    // Send request, add afterwards
    HttpRequest httpRequest = HttpRequest.newBuilder()
      .uri(EndpointService.GetPasswordMapping())
      .POST(HttpRequest.BodyPublishers.ofString(m_appState.network.Serialize(data)))
      .header("Authorization", "Bearer " + m_appState.network.Serialize(m_appState.authService.GetAccessToken()))
      .header("Content-Type", "application/json")
      .timeout(java.time.Duration.ofSeconds(5))
      .build();
    m_appState.network.SendRequest(httpRequest).thenAcceptAsync(response -> {
      if (response.statusCode() / 100 != 2) {
        System.out.println("[MainView]: Failed to add password");
        System.out.println("[MainView]: " + response.statusCode());
        m_updating.set(false);
        return;
      }

      try {
        ServerPasswordData serverData = m_appState.network.Deserialize(response.body(), ServerPasswordData.class);
        PasswordItemData item = new PasswordItemData(m_appState, serverData, this);
        list.getItems().add(item);
        System.out.println("[MainView]: Password added");
        m_updating.set(false);
      } catch (Exception e) {
        System.out.println("[MainView]: Failed to add password");
        System.out.println("[MainView]: " + e.getMessage());
        m_updating.set(false);
      }
    }, Platform::runLater)
      .exceptionallyAsync(throwable -> {
        System.out.println("[MainView]: Failed to add password");
        System.out.println("[MainView]: " + throwable.getMessage());
        m_updating.set(false);
        return null;
      }, Platform::runLater);
  }

  @FXML
  public void initialize() {
    list.setCellFactory(listView -> new PasswordItemCell());
    RefreshPasswords();

//    for (int i = 0; i < 20; ++i) {
//      ServerPasswordData data = new ServerPasswordData();
//      data.name = "Password " + i;
//      list.getItems().add(new PasswordItemData(m_appState, data, this));
//    }
  }

  public void onActionRefresh(ActionEvent actionEvent) {
    RefreshPasswords();
  }

  public void onActionAdd(ActionEvent actionEvent) {
    try {
      Stage stage = new Stage();
      stage.setTitle("Add password");
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(m_appState.primaryStage);

      FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("AddPasswordView.fxml")));
      loader.setControllerFactory(param -> new AddPasswordView(m_appState, this));
      Scene scene = new Scene(loader.load());
      stage.setScene(scene);
      stage.showAndWait();
    } catch (Exception e) {
      System.out.println("Failed to load AddPasswordView");
      System.out.println(e.getMessage());
      return;
    }
  }

  public void onActionLogout(ActionEvent actionEvent) {
    try {
      // Load scene
      FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("LoginView.fxml")));
      loader.setControllerFactory(param -> new LoginView(m_appState));
      Parent element = loader.load();

      // Calculate transition
      double width = m_appState.primaryStage.getWidth();
      double height = m_appState.primaryStage.getHeight();
      double x = m_appState.primaryStage.getX();
      double y = m_appState.primaryStage.getY();

      double newWidth = element.prefWidth(-1);
      double newHeight = element.prefHeight(-1);
      double newX = x + (width - newWidth) / 2;
      double newY = y + (height - newHeight) / 2;

      // Create transition
      FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), m_appState.rootLayout.getChildren().getFirst());
      fadeIn.setFromValue(1);
      fadeIn.setToValue(0);
      fadeIn.setOnFinished(event -> {
        // Log out
        m_appState.authService.LogOut();

        m_appState.primaryStage.setTitle("Login");
        m_appState.rootLayout.getChildren().clear();
        m_appState.rootLayout.getChildren().add(element);

        m_appState.primaryStage.setWidth(newWidth);
        m_appState.primaryStage.setHeight(newHeight);
        m_appState.primaryStage.setX(newX);
        m_appState.primaryStage.setY(newY);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), element);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.play();
      });
      fadeIn.play();

    } catch (Exception e) {
      System.out.println("Failed to load LoginView");
      System.out.println(e.getMessage());
      e.printStackTrace();
      return;
    }
  }

  public void onActionAbout(ActionEvent actionEvent) {
    try {
      Stage aboutStage = new Stage();
      aboutStage.setTitle("About");
      aboutStage.initModality(Modality.WINDOW_MODAL);
      aboutStage.initOwner(m_appState.primaryStage);

      FXMLLoader aboutLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("AboutView.fxml")));
      Scene aboutScene = new Scene(aboutLoader.load());
      aboutStage.setScene(aboutScene);
      aboutStage.showAndWait();
    } catch (Exception e) {
      System.out.println("Failed to load AboutView");
      System.out.println(e.getMessage());
      return;
    }
  }
}
