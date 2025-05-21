package network;

import java.net.URI;

public final class EndpointService {
  private static final URI REGISTER_MAPPING = URI.create("/api/register");
  private static final URI AUTH_MAPPING = URI.create("/api/auth");
  private static final URI REFRESH_MAPPING = URI.create("/api/refresh");
  private static final URI PASSWORD_MAPPING = URI.create("/api/password");
  private static final URI PASSWORD_DELETE_MAPPING = URI.create("/api/password/");
  private static URI sBaseAddress;

  public static void SetBaseAddress(URI baseAddress) {
    sBaseAddress = baseAddress;
  }

  public static URI GetRegisterMapping() {
    return sBaseAddress.resolve(REGISTER_MAPPING);
  }

  public static URI GetAuthMapping() {
    return sBaseAddress.resolve(AUTH_MAPPING);
  }

  public static URI GetRefreshMapping() {
    return sBaseAddress.resolve(REFRESH_MAPPING);
  }

  public static URI GetPasswordMapping() {
    return sBaseAddress.resolve(PASSWORD_MAPPING);
  }

  public static URI GetPasswordDeleteMapping(String id) {
    return sBaseAddress.resolve(PASSWORD_DELETE_MAPPING).resolve(id);
  }
}
