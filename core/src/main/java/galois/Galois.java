package galois;

import io.BitOutputStreamBE;
import utils.BitUtils;

public class Galois {
  public static byte[] Encrypt(byte[] message, byte[] key) {
    int dataLength = message.length * Byte.SIZE;
    byte[] sequence = key.clone();
    byte[] stream = new byte[message.length];

    for (int i = 0; i < dataLength; i++) {
      int bitFirst = BitUtils.GetBitBE(sequence, 0);
      int bitLast = BitUtils.GetBitBE(sequence, sequence.length * Byte.SIZE - 1);
      BitUtils.ShiftRightBE(sequence, 1);
      BitUtils.SetBitBE(sequence, sequence.length * Byte.SIZE - 1, bitFirst ^ bitLast);
      BitUtils.SetBitBE(stream, i, bitFirst);
    }

    for (int byteIndex = 0; byteIndex < stream.length; byteIndex++) {
      stream[byteIndex] ^= message[byteIndex];
    }

    return stream;
  }

  public static byte[] Decrypt(byte[] message, byte[] key) {
    return Encrypt(message, key);
  }
}
