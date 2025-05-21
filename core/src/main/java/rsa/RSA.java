package rsa;

import md2.MD2;
import utils.Pair;

import java.math.BigInteger;

public class RSA {
  public static RsaKeyPair GenerateKeys(int keySize) {
    // Generate prime numbers
    BigInteger p = BigInteger.probablePrime(keySize, new java.util.Random());
    BigInteger q = BigInteger.probablePrime(keySize, new java.util.Random());
    // Calculate modulus
    BigInteger n = p.multiply(q);
    // Find phi
    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    // Public exponent
    BigInteger e = new BigInteger("65537");
    // Private exponent
    BigInteger d = e.modInverse(phi);

    RsaPrivateKey privateKey = new RsaPrivateKey(n, d);
    RsaPublicKey publicKey = new RsaPublicKey(n, e);
    return new RsaKeyPair(privateKey, publicKey);
  }

  public static byte[] Sign(byte[] data, RsaPrivateKey privateKey) {
    byte[] hash = MD2.Hash(data);
    BigInteger hashBI = new BigInteger(1, hash);
    return hashBI.modPow(privateKey.privateExponent, privateKey.modulus).toByteArray();
  }

  public static boolean Verify(byte[] data, byte[] signature, RsaPublicKey publicKey) {
    byte[] hash = MD2.Hash(data);
    BigInteger signatureBI = new BigInteger(1, signature);
    BigInteger hashBI = new BigInteger(1, hash);
    return signatureBI.modPow(publicKey.publicExponent, publicKey.modulus).equals(hashBI);
  }
}
