import network.AuthService;
import network.EndpointService;
import network.NetworkingService;

import java.net.URI;

public class LoginMain {
  public static void main(String[] args) {
    EndpointService.SetBaseAddress(URI.create("http://localhost:8080"));
    NetworkingService network = new NetworkingService();
    AuthService authService = new AuthService(network);


    // Pre loop
    boolean isRunning = true;
    AuthService.Status lastStatus = authService.GetStatus();
    boolean lastIsRefreshing = authService.IsRefreshing();
    authService.LogIn("root", "root");

    while (isRunning) {
      // Pre update
      AuthService.Status status = authService.GetStatus();
      boolean isRefreshing = authService.IsRefreshing();
      boolean isError = authService.IsError();

      // Update
      if (status == AuthService.Status.LOGGED_OUT && isError) {
        System.out.println("Login Error");
        System.out.println("Message: " + authService.GetErrorMessage());
        break;
      }

      if (status != lastStatus) {
        if (status == AuthService.Status.LOGGED_OUT) {
          System.out.println("Logged out");
        }

        if (status == AuthService.Status.LOGGING_IN) {
          System.out.println("Logging in");
        }

        if (status == AuthService.Status.LOGGED_IN) {
          System.out.println("Logged in");
        }
      }

      if (isRefreshing != lastIsRefreshing) {
        if (isRefreshing) {
          System.out.println("Refreshing");
        }

        if (!isRefreshing) {
          System.out.println("Refreshed");
        }
      }

      authService.Update();

      // Post update
      lastStatus = status;
      lastIsRefreshing = isRefreshing;
    }
  }
}
