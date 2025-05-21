package ui;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import listeners.AuthStateListener;
import md2.MD2;
import network.ApplicationState;
import network.AuthService;
import network.EndpointService;
import network.requests.RegisterRequest;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterView implements AuthStateListener {
  private final ApplicationState m_appState;
  @FXML
  public Button mainButton;
  @FXML
  public Button secondaryButton;
  @FXML
  private TextField loginField;
  @FXML
  private PasswordField passwordField;

  private final AtomicBoolean m_loginBlocked;

  public RegisterView(ApplicationState appState) {
    m_appState = appState;
    m_loginBlocked = new AtomicBoolean(false);
  }

  @FXML
  private void handleLogin() {
    if (!m_loginBlocked.compareAndSet(false, true)) {
      return;
    }

    try {
      double width = m_appState.primaryScene.getWidth();
      Node currentElement = m_appState.rootLayout.getChildren().getFirst();

      FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("LoginView.fxml")));
      loader.setControllerFactory(param -> new LoginView(m_appState));
      Parent element = loader.load();
      element.setTranslateX(-width);
      m_appState.rootLayout.getChildren().add(element);

      Timeline timeline = new Timeline();
      KeyValue kv1 = new KeyValue(element.translateXProperty(), 0, Interpolator.EASE_IN);
      KeyValue kv2 = new KeyValue(currentElement.translateXProperty(), width, Interpolator.EASE_IN);
      KeyFrame kf = new KeyFrame(Duration.seconds(0.2), kv1, kv2);
      timeline.getKeyFrames().add(kf);
      timeline.setOnFinished(event -> {
        m_appState.primaryStage.setTitle("Login");
        m_appState.rootLayout.getChildren().remove(currentElement);
        m_loginBlocked.set(false);
      });
      timeline.play();
    } catch (Exception e) {
      System.out.println("[RegisterView]: Failed to load LoginView");
      System.out.println(e.getMessage());
      e.printStackTrace();
      return;
    }
  }

  @FXML
  private void handleRegister() {
    if (m_appState.authService.GetStatus() != AuthService.Status.LOGGED_OUT) {
      return;
    }

    if (!m_loginBlocked.compareAndSet(false, true)) {
      return;
    }

    // Register
    RegisterRequest requestData = new RegisterRequest();
    requestData.username = loginField.getText();
    requestData.keyHash = MD2.Hash(passwordField.getText().getBytes(StandardCharsets.UTF_8));

    HttpRequest httpRequest = HttpRequest.newBuilder()
      .uri(EndpointService.GetRegisterMapping())
      .POST(HttpRequest.BodyPublishers.ofString(m_appState.network.Serialize(requestData)))
      .setHeader("Content-Type", "application/json")
      .timeout(java.time.Duration.ofSeconds(5))
      .build();
    m_appState.network.SendRequest(httpRequest).thenAccept(response -> {
      if (response.statusCode() / 100 != 2) {
        System.out.println("[RegisterView]: Registration failed");
        m_loginBlocked.set(false);
        return;
      }

      System.out.println("[RegisterView]: Registration successful");
      System.out.println("[RegisterView]: Logging in...");
      m_appState.authService.AddAuthStateListener(this);
      m_appState.authService.LogIn(loginField.getText(), passwordField.getText());
    }).exceptionally(throwable -> {
      System.out.println("[RegisterView]: Registration failed");
      m_loginBlocked.set(false);
      return null;
    });
  }

  @Override
  public void OnAuthStateChanged(AuthService service) {
    if (service.GetStatus() == AuthService.Status.LOGGED_IN) {
      // Switch scene
      try {
        // Load scene
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("MainView.fxml")));
        loader.setControllerFactory(param -> new MainView(m_appState));
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
          m_appState.primaryStage.setTitle("Password Manager");
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
        m_appState.authService.SetUpdating(true);

      } catch (Exception e) {
        System.out.println("[RegisterView]: Failed to load MainView");
        System.out.println(e.getMessage());
        e.printStackTrace();
        return;
      }

      // Remove listeners
      service.RemoveAuthStateListener(this);
      m_loginBlocked.set(false);
    }

    if (service.IsError()) {
      System.out.println("[RegisterView]: Login failed");
      service.RemoveAuthStateListener(this);
      m_loginBlocked.set(false);
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
      System.out.println("[RegisterView]: Failed to load AboutView");
      System.out.println(e.getMessage());
      return;
    }
  }
}
