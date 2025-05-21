package rsa;

public class RsaKeyPair {
  public RsaPrivateKey privateKey;
  public RsaPublicKey publicKey;

  public RsaKeyPair(RsaPrivateKey privateKey, RsaPublicKey publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }
}
