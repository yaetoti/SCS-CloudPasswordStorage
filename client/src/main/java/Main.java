import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import network.ApplicationState;
import ui.AddPasswordView;
import ui.LoginView;

import java.io.IOException;
import java.net.URI;

public class Main extends Application {
  public Main() {
    System.out.println("Hello, constructor");
  }

//  @Override
//  public void start(Stage primaryStage) throws IOException {
//    ApplicationState appState = new ApplicationState(URI.create("http://localhost:8080"));
//
//    FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/AddPasswordView.fxml"));
//    loader.setControllerFactory(param -> new AddPasswordView(appState));
//    Parent element = loader.load();
//
//    primaryStage.setScene(new Scene(element));
//    primaryStage.setTitle("Add Password");
//    primaryStage.show();
//  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    // Initialize
    ApplicationState appState = new ApplicationState(URI.create("http://localhost:8080"));
    appState.primaryStage = primaryStage;
    appState.rootLayout = new StackPane();
    appState.primaryScene = new Scene(appState.rootLayout);
    appState.primaryStage.setScene(appState.primaryScene);

    // Load scene
    //FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/MainView.fxml"));
    FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/LoginView.fxml"));
    //FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/AboutView.fxml"));
    loader.setControllerFactory(param -> new LoginView(appState));
    //loader.setControllerFactory(param -> new MainView(appState));
    Parent element = loader.load();
    //AboutView aboutView = loader.getController();

    //primaryStage.setScene(new Scene(root));
    //primaryStage.show();

    appState.rootLayout.getChildren().add(element);
    appState.primaryStage.setTitle("Login");
    appState.primaryStage.show();
  }

  public static void main(String[] args) {
    System.setProperty("prism.verbose", "true");
    System.setProperty("prism.lcdtext", "false");
    System.setProperty("prism.allowhidpi", "true");
    System.setProperty("prism.text", "t2k");
    System.setProperty("prism.order", "es2");
    launch(args);
  }
}
