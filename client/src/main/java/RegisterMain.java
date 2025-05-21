import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import md2.MD2;
import network.adapters.GsonBytesBase64Adapter;
import network.requests.RegisterRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class RegisterMain {
  public static void main(String[] args) {
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(byte[].class, new GsonBytesBase64Adapter())
      .create();

    try (HttpClient client = HttpClient.newHttpClient()) {
      // Init request
      String username = "aboba";
      String password = "1234";
      byte[] keyHash = MD2.Hash(password.getBytes(StandardCharsets.UTF_8));

      RegisterRequest registerRequest = new RegisterRequest();
      registerRequest.username = username;
      registerRequest.keyHash = keyHash;

      // Send request
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/register"))
        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(registerRequest)))
        .header("Content-Type", "application/json")
        .build();

      CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

      System.out.println("Awaiting");
      while (!future.isDone()) {
        Thread.yield();
      }

      // Handle response
      HttpResponse<String> result = future.getNow(null);
      if (result == null) {
        System.out.println("No response");
        return;
      }

      System.out.println("Status: " + result.statusCode());
      System.out.println("Body: " + result.body());

      if (result.statusCode() / 100 == 2) {
        System.out.println("Registered");
        return;
      }

      System.out.println("Failed to register");
    }
  }
}
