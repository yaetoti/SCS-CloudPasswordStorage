package main;

import rsa.RSA;
import rsa.RsaKeyPair;
import utils.ByteUtils;

import java.nio.charset.StandardCharsets;

public class MainRSA {
  public static void main(String[] args) {
    System.out.println("RSA");
    System.out.println();

    int keySize = 2048;
    RsaKeyPair keys = RSA.GenerateKeys(keySize);
    System.out.println("Key size: " + keySize);
    System.out.println("Public key: " + keys.publicKey.modulus.toString(16));
    System.out.println("Private key: " + keys.privateKey.privateExponent.toString(16));

    System.out.println();
    String message0 = "Hello, world!";
    System.out.println("Message: " + message0);
    byte[] signature = RSA.Sign(message0.getBytes(StandardCharsets.US_ASCII), keys.privateKey);
    System.out.println("Signature: " + ByteUtils.BytesToStringHex(signature, false));
    boolean verified = RSA.Verify(message0.getBytes(StandardCharsets.US_ASCII), signature, keys.publicKey);
    System.out.println("Verified: " + verified);

    System.out.println();
    String message1 = "Hello, world.";
    System.out.println("Message: " + message1);
    verified = RSA.Verify(message1.getBytes(StandardCharsets.US_ASCII), signature, keys.publicKey);
    System.out.println("Verified: " + verified);
  }
}
