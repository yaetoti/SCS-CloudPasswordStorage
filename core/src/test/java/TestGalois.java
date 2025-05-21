import galois.Galois;
import md2.MD2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utils.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestGalois {
  @Test
  public void TestGalois() {
    String password = "ab0ba1337";
    String message = "Hello, world! Hello, world! Hello, world! Hello, world! Hello, world! Hello, world! Hello, world!";
    byte[] key = MD2.Hash(password.getBytes(StandardCharsets.UTF_8));
    byte[] data = message.getBytes(StandardCharsets.UTF_8);
    byte[] encoded = Galois.Encrypt(data, key);
    byte[] decoded = Galois.Decrypt(encoded, key);
    Assertions.assertArrayEquals(data, decoded);
    Assertions.assertFalse(Arrays.equals(data, encoded));
  }
}
