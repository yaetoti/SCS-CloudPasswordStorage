package utils;

import galois.Galois;
import permutation.Permutation;
import substitution.Caesar;

public final class EncryptionUtils {
  public static byte[] Encrypt(CipherId cipherId, byte[] data, byte[] key) {
    return switch (cipherId) {
      case DOUBLE_PERMUTATION -> Permutation.Encrypt(data, key);
      case CAESAR_CIPHER -> Caesar.Encrypt(data, key);
      case GALOIS_CONFIGURATION -> Galois.Encrypt(data, key);
      default -> throw new IllegalStateException("Unexpected value: " + cipherId);
    };
  }

  public static byte[] Decrypt(CipherId cipherId, byte[] data, byte[] key) {
    return switch (cipherId) {
      case DOUBLE_PERMUTATION -> Permutation.Decrypt(data, key);
      case CAESAR_CIPHER -> Caesar.Decrypt(data, key);
      case GALOIS_CONFIGURATION -> Galois.Decrypt(data, key);
    };
  }
}
