package rsa;

import java.io.Serializable;
import java.math.BigInteger;

public class RsaPublicKey implements Serializable {
  public BigInteger modulus;
  public BigInteger publicExponent;

  public RsaPublicKey(BigInteger modulus, BigInteger publicExponent) {
    this.modulus = modulus;
    this.publicExponent = publicExponent;
  }
}
