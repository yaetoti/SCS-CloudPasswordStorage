import md2.MD2;
import org.junit.jupiter.api.Test;
import utils.ByteUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class TestMD2 {
  @Test
  public void TestEmptyInput() {
    byte[] input = new byte[0];
    byte[] hash = MD2.Hash(input);
    assertNotNull(hash);
    assertEquals(16, hash.length);
  }

  @Test
  public void TestKnownHash() {
    String input = "abc";
    byte[] bytes = input.getBytes(StandardCharsets.US_ASCII);
    byte[] hash = MD2.Hash(bytes);
    String hexHash = ByteUtils.BytesToStringHex(hash, false);
    assertEquals("70F77CCA81F21B28A87EA3C4F702B32B", hexHash);
  }
}
