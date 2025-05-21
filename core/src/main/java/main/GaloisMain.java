package main;

import galois.Galois;
import md2.MD2;
import utils.ByteUtils;

import java.nio.charset.StandardCharsets;

public class GaloisMain {
  public static void main(String[] args) {
    String password = "ab0ba1337";
    String message = "Hello, world! Hello, world! Hello, world! Hello, world! Hello, world! Hello, world! Hello, world!";
    byte[] key = MD2.Hash(password.getBytes(StandardCharsets.UTF_8));

    System.out.println("Password: " + password);
    System.out.println("Message: " + message);
    System.out.println("Key: " + ByteUtils.BytesToStringHex(key, false));

    byte[] data = message.getBytes(StandardCharsets.UTF_8);
    byte[] encoded = Galois.Encrypt(data, key);
    byte[] decoded = Galois.Decrypt(encoded, key);

    String encodedString = new String(encoded, StandardCharsets.UTF_8);
    String decodedString = new String(decoded, StandardCharsets.UTF_8);
    System.out.println("Encoded: " + ByteUtils.BytesToStringHex(encoded, false));
    System.out.println("Decoded: " + ByteUtils.BytesToStringHex(decoded, false));
    System.out.println("Encoded string: " + encodedString);
    System.out.println("Decoded string: " + decodedString);
  }
}
