package rsa;

import java.io.Serializable;
import java.math.BigInteger;

public class RsaPrivateKey implements Serializable {
  public BigInteger modulus;
  public BigInteger privateExponent;

  public RsaPrivateKey(BigInteger modulus, BigInteger privateExponent) {
    this.modulus = modulus;
    this.privateExponent = privateExponent;
  }
}
