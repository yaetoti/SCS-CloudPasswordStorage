package main;

import permutation.Permutation;

import java.nio.charset.StandardCharsets;

public class MainPermutation {
  public static void main(String[] args) {
    System.out.println("Permutation");
    System.out.println();

    String message = "Hello, world!";
    String key = "pseudopseudohypoparathyroidism";
    System.out.println("Message: " + message);
    System.out.println("Key: " + key);
    byte[] encoded = Permutation.Encrypt(message.getBytes(StandardCharsets.US_ASCII), key.getBytes(StandardCharsets.US_ASCII));
    System.out.println("Encoded: " + new String(encoded, StandardCharsets.US_ASCII));
    byte[] decoded = Permutation.Decrypt(encoded, key.getBytes(StandardCharsets.US_ASCII));
    System.out.println("Decoded: " + new String(decoded, StandardCharsets.US_ASCII));
  }
}
