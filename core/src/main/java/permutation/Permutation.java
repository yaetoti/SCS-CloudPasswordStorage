package permutation;

import utils.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;

public class Permutation {
  public static int[] StringToIndices(String str) {
    char[] chars = str.toCharArray();
    char[] sortedChars = chars.clone();
    Arrays.sort(sortedChars);

    HashMap<Character, Integer> occurrences = new HashMap<>();
    int[] indices = new int[chars.length];

    for (int i = 0; i < chars.length; i++) {
      indices[i] = ArrayUtils.Search(sortedChars, chars[i]);
      if (occurrences.containsKey(chars[i])) {
        int occurrencesCount = occurrences.get(chars[i]);
        indices[i] += occurrencesCount;
        occurrences.put(chars[i], occurrencesCount + 1);
      }
      else {
        occurrences.put(chars[i], 1);
      }
    }

    return indices;
  }

  public static int[] BytesToIndices(byte[] data) {
    byte[] sortedData = data.clone();
    Arrays.sort(sortedData);

    HashMap<Byte, Integer> occurrences = new HashMap<>();
    int[] indices = new int[data.length];

    for (int i = 0; i < data.length; i++) {
      indices[i] = ArrayUtils.Search(sortedData, data[i]);
      if (occurrences.containsKey(data[i])) {
        int occurrencesCount = occurrences.get(data[i]);
        indices[i] += occurrencesCount;
        occurrences.put(data[i], occurrencesCount + 1);
      }
      else {
        occurrences.put(data[i], 1);
      }
    }

    return indices;
  }

  public static byte[] Encrypt(byte[] data, byte[] key) {
    int requiredKeySize = 2 * data.length;

    // Repeat key if size is not enough, shrink if large enough
    if (key.length < requiredKeySize) {
      byte[] tempKey = new byte[requiredKeySize];
      int size = 0;

      while (size < requiredKeySize) {
        int extendSize = Math.min(requiredKeySize - size, key.length);
        System.arraycopy(key, 0, tempKey, size, extendSize);
        size += extendSize;
      }

      key = tempKey;
    }
    else if (key.length > requiredKeySize) {
      key = Arrays.copyOfRange(key, 0, requiredKeySize);
    }

    byte[][] keyParts = { new byte[data.length], new byte[data.length] };
    System.arraycopy(key, 0, keyParts[0], 0, data.length);
    System.arraycopy(key, data.length, keyParts[1], 0, data.length);
    byte[] result = data.clone();

    for (byte[] keyPart : keyParts) {
      int[] indices = BytesToIndices(keyPart);
      byte[] temp = new byte[result.length];
      for (int j = 0; j < result.length; j++) {
        temp[j] = result[indices[j]];
      }

      result = temp;
    }

    return result;
  }

  public static byte[] Decrypt(byte[] data, byte[] key) {
    int requiredKeySize = 2 * data.length;

    // Repeat key if size is not enough, shrink if large enough
    if (key.length < requiredKeySize) {
      byte[] tempKey = new byte[requiredKeySize];
      int size = 0;

      while (size < requiredKeySize) {
        int extendSize = Math.min(requiredKeySize - size, key.length);
        System.arraycopy(key, 0, tempKey, size, extendSize);
        size += extendSize;
      }

      key = tempKey;
    }
    else if (key.length > requiredKeySize) {
      key = Arrays.copyOfRange(key, 0, requiredKeySize);
    }

    byte[][] keyParts = { new byte[data.length], new byte[data.length] };
    System.arraycopy(key, 0, keyParts[0], 0, data.length);
    System.arraycopy(key, data.length, keyParts[1], 0, data.length);
    byte[] result = data.clone();

    for (int i = keyParts.length - 1; i >= 0; i--) {
      int[] indices = BytesToIndices(keyParts[i]);
      byte[] temp = new byte[data.length];
      for (int j = 0; j < data.length; j++) {
        temp[indices[j]] = result[j];
      }

      result = temp;
    }

    return result;
  }
}
