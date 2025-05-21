package substitution;

import md2.MD2;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Caesar {
  public static final int MAX_ROTATION = 256;

  public static byte[] Encrypt(byte[] message, byte[] key) {
    // TODO accept byte[] key
    int rotation = new BigInteger(1, key).mod(BigInteger.valueOf(MAX_ROTATION)).intValue();

    byte[] result = new byte[message.length];
    for (int i = 0; i < message.length; i++) {
      result[i] = (byte) ((message[i] + rotation) % MAX_ROTATION);
    }

    return result;
  }

  public static byte[] Decrypt(byte[] message, byte[] key) {
    int rotation = new BigInteger(1, key).mod(BigInteger.valueOf(MAX_ROTATION)).intValue();

    byte[] result = new byte[message.length];
    for (int i = 0; i < message.length; i++) {
      result[i] = (byte) ((message[i] - rotation + MAX_ROTATION) % MAX_ROTATION);
    }
    
    return result;
  }
}
