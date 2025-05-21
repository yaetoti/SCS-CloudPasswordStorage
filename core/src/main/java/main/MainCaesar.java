package main;

import md2.MD2;
import substitution.Caesar;
import utils.ByteUtils;

import java.nio.charset.StandardCharsets;

public class MainCaesar {
  public static void main(String[] args) {
    String password = "ab0ba1337";
    String message = "Hello, world!";
    byte[] key = MD2.Hash(password.getBytes(StandardCharsets.UTF_8));

    byte[] encrypted = Caesar.Encrypt(message.getBytes(StandardCharsets.UTF_8), key);
    byte[] decrypted = Caesar.Decrypt(encrypted, key);

    System.out.println("Password: " + password);
    System.out.println("Message: " + message);
    System.out.println("Encrypted: " + ByteUtils.BytesToStringHex(encrypted, false));
    System.out.println("Decrypted: " + new String(decrypted, StandardCharsets.UTF_8));
  }
}
