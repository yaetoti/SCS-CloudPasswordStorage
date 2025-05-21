package network.payload;

public class ServerPasswordData {
  public Long id;
  public String name;
  public byte[] encrypted_password;
  public Integer cipherId;
  public byte[] keyHash;
}
