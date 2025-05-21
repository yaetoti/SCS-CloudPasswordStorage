import md2.MD2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import substitution.Caesar;
import utils.ByteUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class TestCaesar {
  @Test
  public void TestCaesar() {
    String password = "ab0ba1337";
    String message = "Hello, world!";
    byte[] key = MD2.Hash(password.getBytes(StandardCharsets.UTF_8));

    byte[] data = message.getBytes(StandardCharsets.UTF_8);
    byte[] encrypted = Caesar.Encrypt(data, key);
    byte[] decrypted = Caesar.Decrypt(encrypted, key);

    Assertions.assertArrayEquals(data, decrypted);
    Assertions.assertNotEquals(ByteUtils.BytesToStringHex(encrypted, false), ByteUtils.BytesToStringHex(data, false));
  }
}
