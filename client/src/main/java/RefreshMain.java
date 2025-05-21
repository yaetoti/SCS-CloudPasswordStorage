import galois.Galois;
import md2.MD2;
import network.NetworkingService;
import network.payload.*;
import network.requests.AuthRequest;
import network.responses.AuthResponse;
import permutation.Permutation;
import substitution.Caesar;
import utils.ByteUtils;
import utils.CipherId;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

final class RequestService {
  public static final URI REGISTER_MAPPING = URI.create("/api/register");
  public static final URI AUTH_MAPPING = URI.create("/api/auth");
  public static final URI REFRESH_MAPPING = URI.create("/api/refresh");
  public static final URI PASSWORD_MAPPING = URI.create("/api/password");

  private final NetworkingService m_net;
  private URI m_baseAddress;

  public RequestService(NetworkingService net, URI baseAddress) {
    m_net = net;
    m_baseAddress = baseAddress;
  }

  public void SetBaseUrl(URI baseAddress) {
    m_baseAddress = baseAddress;
  }

  public HttpRequest BuildAuthRequest(String username, String password) {
    AuthRequest authRequest = new AuthRequest();
    authRequest.username = username;
    authRequest.keyHash = MD2.Hash(password.getBytes(StandardCharsets.UTF_8));

    return HttpRequest.newBuilder()
      .uri(ResolveEndpoint(AUTH_MAPPING))
      .POST(HttpRequest.BodyPublishers.ofString(m_net.Serialize(authRequest)))
      .header("Content-Type", "application/json")
      .build();
  }

  public HttpRequest BuildRefreshRequest(Token refreshToken) {
    return HttpRequest.newBuilder()
      .uri(ResolveEndpoint(REFRESH_MAPPING))
      .POST(HttpRequest.BodyPublishers.ofString(m_net.Serialize(refreshToken)))
      .header("Content-Type", "application/json")
      .build();
  }

  public HttpRequest BuildAddPasswordRequest(PasswordData data, Token accessToken) {
    return HttpRequest.newBuilder()
      .uri(ResolveEndpoint(PASSWORD_MAPPING))
      .POST(HttpRequest.BodyPublishers.ofString(m_net.Serialize(data)))
      .header("Authorization", "Bearer " + m_net.Serialize(accessToken))
      .header("Content-Type", "application/json")
      .build();
  }

  public HttpRequest BuildUpdatePasswordRequest(PasswordData data, Token accessToken) {
    return HttpRequest.newBuilder()
      .uri(ResolveEndpoint(PASSWORD_MAPPING))
      .PUT(HttpRequest.BodyPublishers.ofString(m_net.Serialize(data)))
      .header("Authorization", "Bearer " + m_net.Serialize(accessToken))
      .header("Content-Type", "application/json")
      .build();
  }

  public HttpRequest BuildGetPasswordsRequest(Token accessToken) {
    return HttpRequest.newBuilder()
      .uri(ResolveEndpoint(PASSWORD_MAPPING))
      .GET()
      .header("Authorization", "Bearer " + m_net.Serialize(accessToken))
      .header("Content-Type", "application/json")
      .build();
  }

  public HttpRequest BuildDeletePasswordRequest(Long id, Token accessToken) {
    return HttpRequest.newBuilder()
      .uri(ResolveEndpoint(PASSWORD_MAPPING.resolve(id.toString())))
      .DELETE()
      .header("Authorization", "Bearer " + m_net.Serialize(accessToken))
      .header("Content-Type", "application/json")
      .build();
  }

  private URI ResolveEndpoint(URI endpoint) {
    return m_baseAddress.resolve(endpoint);
  }
}

public class RefreshMain {
  public static void main(String[] args) {
    try (NetworkingService net = new NetworkingService()) {
      URI baseAddress = URI.create("http://localhost:8080");
      RequestService requestService = new RequestService(net, baseAddress);

      // Send request
      String username = "root";
      String password = "root";
      HttpRequest authRequest = requestService.BuildAuthRequest(username, password);
      HttpResponse<String> response = net.SendRequest(authRequest).join();

      // Handle response
      if (response.statusCode() / 100 == 2) {
        System.out.println("Logged in");

        AuthResponse authResponse = net.Deserialize(response.body(), AuthResponse.class);
        System.out.println("Access token: " + authResponse.accessToken);
        System.out.println("Refresh token: " + authResponse.refreshToken);

        System.out.println("Sleep");
        Thread.sleep(500);
        System.out.println("Doing refresh");

        // Doing refresh
        HttpRequest refreshRequest = requestService.BuildRefreshRequest(authResponse.refreshToken);
        response = net.SendRequest(refreshRequest).join();

        // Handle response
        if (response.statusCode() / 100 == 2) {
          System.out.println("Refreshed");

          AuthResponse refreshResponse = net.Deserialize(response.body(), AuthResponse.class);
          System.out.println("Access token: " + refreshResponse.accessToken);
          System.out.println("Refresh token: " + refreshResponse.refreshToken);

          // Add password
          String password0 = "семидесятипятимиллиметровый";
          String key0 = "root";
          byte[] encoded0 = Galois.Encrypt(password0.getBytes(StandardCharsets.UTF_8), key0.getBytes(StandardCharsets.UTF_8));


          PasswordData passwordData = new PasswordData();
          passwordData.name = "Google";
          passwordData.cipherId = (int) CipherId.GALOIS_CONFIGURATION.GetId();
          passwordData.encrypted_password = encoded0;
          passwordData.keyHash = MD2.Hash(key0.getBytes(StandardCharsets.UTF_8));

          HttpRequest postPasswordRequest = requestService.BuildAddPasswordRequest(passwordData, refreshResponse.accessToken);
          response = net.SendRequest(postPasswordRequest).join();

          // Handle response
          if (response.statusCode() / 100 == 2) {
            System.out.println("Added password");

            // Get passwords

            HttpRequest getPasswordsRequest = requestService.BuildGetPasswordsRequest(refreshResponse.accessToken);
            response = net.SendRequest(getPasswordsRequest).join();

            // Handle response
            if (response.statusCode() / 100 == 2) {
              System.out.println("Got passwords");
              ServerPasswordData[] passwords = net.Deserialize(response.body(), ServerPasswordData[].class);
              for (var currentPassword : passwords) {
                System.out.println("ID: " + currentPassword.id);
                System.out.println("- Name: " + currentPassword.name);
                System.out.println("- Cipher ID: " + currentPassword.cipherId);

                String decodedPassword = null;
                if (currentPassword.cipherId == (int) CipherId.DOUBLE_PERMUTATION.GetId()) {
                  byte[] decoded = Permutation.Decrypt(currentPassword.encrypted_password, key0.getBytes(StandardCharsets.UTF_8));
                  decodedPassword = new String(decoded, StandardCharsets.UTF_8);
                }
                else if (currentPassword.cipherId == (int) CipherId.CAESAR_CIPHER.GetId()) {
                  byte[] decoded = Caesar.Decrypt(currentPassword.encrypted_password, key0.getBytes(StandardCharsets.UTF_8));
                  decodedPassword = new String(decoded, StandardCharsets.UTF_8);
                }
                else if (currentPassword.cipherId == (int) CipherId.GALOIS_CONFIGURATION.GetId()) {
                  byte[] decoded = Galois.Decrypt(currentPassword.encrypted_password, key0.getBytes(StandardCharsets.UTF_8));
                  decodedPassword = new String(decoded, StandardCharsets.UTF_8);
                } else {
                  System.out.println("Unknown cipher ID");
                  continue;
                }

                System.out.println("- Password: " + decodedPassword);
                System.out.println("- Key hash: " + ByteUtils.BytesToStringOct(currentPassword.keyHash, false));
                System.out.println();
              }

              return;
            }

            System.out.println("Can't get passwords");
            return;
          }

          System.out.println("Send password failed");
          System.out.println("Status: " + response.statusCode());
          System.out.println("Body: " + response.body());
          return;
        }

        System.out.println("Failed to refresh");

        return;
      }

      System.out.println("Failed to login");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
