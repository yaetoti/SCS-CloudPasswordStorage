package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import network.adapters.GsonBytesBase64Adapter;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public final class NetworkingService implements AutoCloseable {
  private final Gson m_gson;
  private final HttpClient m_client;

  public NetworkingService() {
    m_gson = new GsonBuilder()
      .registerTypeAdapter(byte[].class, new GsonBytesBase64Adapter())
      .create();
    m_client = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(5))
      .build();
  }

  public CompletableFuture<HttpResponse<String>> SendRequest(HttpRequest request) {
    return m_client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
  }

  public <T> T Deserialize(String json, Class<T> type) throws JsonSyntaxException {
    return m_gson.fromJson(json, type);
  }

  public <T> String Serialize(T obj) {
    return m_gson.toJson(obj);
  }

  @Override
  public void close() throws Exception {
    if (m_client != null) {
      m_client.close();
    }
  }
}
