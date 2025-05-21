package network.payload;

public final class AccessTokenData {
  public long userId;
  public long issuedAt;
  public long expiresAt;
  public boolean isAdmin;

  @Override
  public String toString() {
    return "AccessTokenData{" +
      "userId=" + userId +
      ", issuedAt=" + issuedAt +
      ", expiresAt=" + expiresAt +
      ", isAdmin=" + isAdmin +
      '}';
  }
}
