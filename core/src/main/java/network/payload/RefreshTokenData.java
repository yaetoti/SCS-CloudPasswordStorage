package network.payload;

public final class RefreshTokenData {
  public long userId;
  public long issuedAt;
  public long expiresAt;

  @Override
  public String toString() {
    return "RefreshTokenData{" +
      "userId=" + userId +
      ", issuedAt=" + issuedAt +
      ", expiresAt=" + expiresAt +
      '}';
  }
}
