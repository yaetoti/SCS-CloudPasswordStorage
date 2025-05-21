package main;

import md2.MD2;
import utils.ByteUtils;

import java.nio.charset.StandardCharsets;

public class MainMD2 {
  public static void main(String[] args) {
    System.out.println("MD2");
    System.out.println();

    String message0 = "Hello, world!";
    byte[] hash0 = MD2.Hash(message0.getBytes(StandardCharsets.US_ASCII));
    System.out.println("Message: " + message0);
    System.out.println("Hash: " + ByteUtils.BytesToStringHex(hash0, false));

    String message1 = "Hello, world.";
    byte[] hash1 = MD2.Hash(message1.getBytes(StandardCharsets.US_ASCII));
    System.out.println("Message: " + message1);
    System.out.println("Hash: " + ByteUtils.BytesToStringHex(hash1, false));
  }
}
