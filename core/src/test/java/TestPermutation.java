import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import permutation.Permutation;

import java.nio.charset.StandardCharsets;

public class TestPermutation {
  @Test
  public void TestIndices() {
    int[] indices0 = Permutation.BytesToIndices("aboba".getBytes(StandardCharsets.US_ASCII));
    Assertions.assertArrayEquals(new int[] { 0, 2, 4, 3, 1 }, indices0);

    int[] indices1 = Permutation.StringToIndices("aboba");
    Assertions.assertArrayEquals(new int[] { 0, 2, 4, 3, 1 }, indices1);
  }

  @Test
  public void TestEncryptDecrypt() {
    String message = "Hello, world!";
    String key = "pseudopseudohypoparathyroidism";
    byte[] encoded = Permutation.Encrypt(message.getBytes(StandardCharsets.US_ASCII), key.getBytes(StandardCharsets.US_ASCII));
    byte[] decoded = Permutation.Decrypt(encoded, key.getBytes(StandardCharsets.US_ASCII));
    Assertions.assertArrayEquals(message.getBytes(StandardCharsets.US_ASCII), decoded);
  }
}
