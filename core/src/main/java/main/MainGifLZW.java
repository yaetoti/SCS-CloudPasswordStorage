package main;

import gif.utils.GifLzwUtils;
import utils.ByteUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainGifLZW {
  public static void main(String[] args) throws IOException {
    String message = "Hello, world! Hello, world! Hello, world! Hello, world! Hello, world! Hello, world! Hello, world! Hello, world!";
    byte[] data = message.getBytes(StandardCharsets.UTF_8);

    byte[] encoded = GifLzwUtils.Encode(8, data);
    byte[] decoded = GifLzwUtils.Decode(8, encoded);
    String decodedMessage = new String(decoded, StandardCharsets.UTF_8);

    System.out.println("Encoded: " + ByteUtils.BytesToStringHex(encoded, false));
    System.out.println("Encoded length: " + encoded.length);
    System.out.println("Decoded: " + ByteUtils.BytesToStringHex(decoded, false));
    System.out.println("Decoded length: " + decoded.length);

    System.out.println("Message: " + message);
    System.out.println("Decoded message: " + decodedMessage);
  }
}
