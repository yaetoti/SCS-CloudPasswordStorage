package network;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import listeners.AuthStateListener;
import md2.MD2;
import network.payload.AccessTokenData;
import network.payload.RefreshTokenData;
import network.payload.Token;
import network.requests.AuthRequest;
import network.responses.AuthResponse;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AuthService {
  public enum Status {
    LOGGED_OUT,
    LOGGING_IN,
    LOGGED_IN,
  }

  // State
  private final NetworkingService m_network;
  private volatile Status m_status;
  private volatile boolean m_isRefreshing;
  private volatile boolean m_isError;
  private volatile String m_errorMessage;
  private volatile Token m_accessToken;
  private volatile Token m_refreshToken;
  private volatile AccessTokenData m_accessTokenData;
  private volatile RefreshTokenData m_refreshTokenData;
  private final long m_refreshThreshold;

  // Events
  final List<AuthStateListener> m_authStateListener;
  final Timeline m_updateTimeline;

  public AuthService(NetworkingService network) {
    m_network = network;
    m_status = Status.LOGGED_OUT;
    m_isError = false;
    m_errorMessage = null;
    m_accessToken = null;
    m_refreshToken = null;
    m_accessTokenData = null;
    m_refreshTokenData = null;
    m_refreshThreshold = 60;

    m_authStateListener = Collections.synchronizedList(new ArrayList<>());
    m_updateTimeline = new Timeline(
      new KeyFrame(
        javafx.util.Duration.seconds(5),
        event -> Platform.runLater(this::Update)
      )
    );
    m_updateTimeline.setCycleCount(Timeline.INDEFINITE);
  }

  public Status GetStatus() {
    return m_status;
  }

  public boolean IsError() {
    return m_isError;
  }

  public boolean IsRefreshing() {
    return m_isRefreshing;
  }

  public String GetErrorMessage() {
    return m_errorMessage;
  }

  public Token GetAccessToken() {
    return m_accessToken;
  }

  public Token GetRefreshToken() {
    return m_refreshToken;
  }

  public void ClearError() {
    m_isError = false;
    m_errorMessage = null;
  }

  public void SetUpdating(boolean isUpdating) {
    System.out.println("[AuthService]: Updating was set to " + isUpdating);
    if (isUpdating) {
      m_updateTimeline.play();
    } else {
      m_updateTimeline.stop();
    }
  }

  public boolean IsUpdating() {
    return m_updateTimeline.getStatus() == Timeline.Status.RUNNING;
  }

  public void Update() {
    HandleRefresh();
  }

  public void LogOut() {
    if (m_status != Status.LOGGED_IN || m_isRefreshing) {
      return;
    }

    System.out.println("[AuthService]: Logging out");
    SetUpdating(false);
    m_accessToken = null;
    m_refreshToken = null;
    m_accessTokenData = null;
    m_refreshTokenData = null;

    ClearError();
    m_status = Status.LOGGED_OUT;
    System.out.println("[AuthService]: Logged out");
  }

  public void LogIn(String username, String password) {
    byte[] keyHash = MD2.Hash(password.getBytes(StandardCharsets.UTF_8));
    LogIn(username, keyHash);
  }

  public void LogIn(String username, byte[] keyHash) {
    if (m_status != Status.LOGGED_OUT) {
      return;
    }

    ClearError();
    m_status = Status.LOGGING_IN;
    System.out.println("[AuthService]: Logging in");

    // Build request
    AuthRequest requestData = new AuthRequest();
    requestData.username = username;
    requestData.keyHash = keyHash;

    HttpRequest httpRequest = HttpRequest.newBuilder()
      .uri(EndpointService.GetAuthMapping())
      .POST(HttpRequest.BodyPublishers.ofString(m_network.Serialize(requestData)))
      .header("Content-Type", "application/json")
      .timeout(Duration.ofSeconds(5))
      .build();

    // Send request
    m_network.SendRequest(httpRequest).thenAccept(response -> {
      // Handle response
      if (response.statusCode() / 100 != 2) {
        m_status = Status.LOGGED_OUT;
        m_isError = true;
        m_errorMessage = response.body();
        System.out.println("[AuthService]: Failed to login");

        NotifyAuthStateListeners();
        return;
      }

      try {
        AuthResponse authResponse = m_network.Deserialize(response.body(), AuthResponse.class);
        var accessToken = authResponse.accessToken;
        var responseToken = authResponse.refreshToken;
        var accessTokenData = m_network.Deserialize(accessToken.GetJsonData(), AccessTokenData.class);
        var refreshTokenData = m_network.Deserialize(responseToken.GetJsonData(), RefreshTokenData.class);

        m_accessToken = accessToken;
        m_refreshToken = responseToken;
        m_accessTokenData = accessTokenData;
        m_refreshTokenData = refreshTokenData;

        m_status = Status.LOGGED_IN;
        System.out.println("[AuthService]: Logged in");
      } catch (Exception e) {
        m_status = Status.LOGGED_OUT;
        m_isError = true;
        m_errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
        System.out.println("[AuthService]: Failed to login (Deserialization)");
        System.out.println("[AuthService]: " + e.getMessage());
      }

      NotifyAuthStateListeners();
    }).exceptionally(throwable -> {
      m_status = Status.LOGGED_OUT;
      m_isError = true;
      m_errorMessage = throwable.getMessage();
      System.out.println("[AuthService]: Failed to login (Exceptionally)");
      System.out.println("[AuthService]: " + throwable.getMessage());

      NotifyAuthStateListeners();
      return null;
    });
  }

  private void HandleRefresh() {
    if (m_status != Status.LOGGED_IN || m_isRefreshing) {
      return;
    }

    long time = Instant.now().getEpochSecond();

    // Check if tokens are valid
    if (m_accessTokenData.expiresAt - time > m_refreshThreshold && m_refreshTokenData.expiresAt - time > m_refreshThreshold) {
      return;
    }

    // Check if tokens are expired
    if (time >= m_accessTokenData.expiresAt && time >= m_refreshTokenData.expiresAt) {
      System.out.println("[AuthService]: Tokens expired");
      LogOut();
      return;
    }

    // Refresh tokens
    ClearError();
    m_isRefreshing = true;
    System.out.println("[AuthService]: Refreshing");

    HttpRequest httpRequest = HttpRequest.newBuilder()
      .uri(EndpointService.GetRefreshMapping())
      .POST(HttpRequest.BodyPublishers.ofString(m_network.Serialize(m_refreshToken)))
      .header("Content-Type", "application/json")
      .timeout(Duration.ofSeconds(5))
      .build();

    m_network.SendRequest(httpRequest).thenAccept(response -> {
      if (response.statusCode() / 100 != 2) {
        m_isRefreshing = false;
        m_isError = true;
        m_errorMessage = response.body();
        System.out.println("[AuthService]: Failed to refresh");
        System.out.println("[AuthService]: " + response.body());

        NotifyAuthStateListeners();
        return;
      }

      try {
        AuthResponse authResponse = m_network.Deserialize(response.body(), AuthResponse.class);
        var accessToken = authResponse.accessToken;
        var responseToken = authResponse.refreshToken;
        var accessTokenData = m_network.Deserialize(accessToken.GetJsonData(), AccessTokenData.class);
        var refreshTokenData = m_network.Deserialize(responseToken.GetJsonData(), RefreshTokenData.class);

        if (m_status != Status.LOGGED_IN) {
          m_isRefreshing = false;
          System.out.println("[AuthService]: Refreshing cancelled (Logged out)");
          return;
        }

        m_accessToken = accessToken;
        m_refreshToken = responseToken;
        m_accessTokenData = accessTokenData;
        m_refreshTokenData = refreshTokenData;
        m_isRefreshing = false;
        System.out.println("[AuthService]: Refreshed");
      } catch (Exception e) {
        m_isRefreshing = false;
        m_isError = true;
        m_errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
        System.out.println("[AuthService]: Failed to refresh (Deserialization)");
        System.out.println("[AuthService]: " + e.getMessage());
      }

      NotifyAuthStateListeners();
    }).exceptionally(throwable -> {
      m_isRefreshing = false;
      m_isError = true;
      m_errorMessage = throwable.getMessage();
      System.out.println("[AuthService]: Failed to refresh (Exceptionally)");
      System.out.println("[AuthService]: " + throwable.getMessage());

      NotifyAuthStateListeners();
      return null;
    });
  }

  public void AddAuthStateListener(AuthStateListener listener) {
    m_authStateListener.add(listener);
  }

  public void RemoveAuthStateListener(AuthStateListener listener) {
    m_authStateListener.remove(listener);
  }

  public void NotifyAuthStateListeners() {
    ArrayList<AuthStateListener> listeners = new ArrayList<>(m_authStateListener);
    for (var listener : listeners) {
      listener.OnAuthStateChanged(this);
    }
  }
}
