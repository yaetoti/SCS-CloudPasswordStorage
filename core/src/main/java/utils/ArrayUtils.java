package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtils {
  public static byte[] ToByteArray(List<Byte> data) {
    var result = new byte[data.size()];
    int index = 0;
    for (var b : data) {
      result[index++] = b;
    }

    return result;
  }

  public static ArrayList<Byte> ToArrayList(byte[] data) {
    var result = new ArrayList<Byte>();
    for (var b : data) {
      result.add(b);
    }

    return result;
  }

  public static <T> T[] Resize(T[] array, int newSize) {
    if (newSize < 0) {
      throw new IllegalArgumentException("New size must be non-negative");
    }

    return Arrays.copyOf(array, newSize);
  }

  public static byte[] Resize(byte[] array, int newSize) {
    if (newSize < 0) {
      throw new IllegalArgumentException("New size must be non-negative");
    }

    return Arrays.copyOf(array, newSize);
  }

  public static <T> T[] Concat(T[] first, T[] second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  public static byte[] Concat(byte[] first, byte[] second) {
    byte[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  public static int Compare(byte[] first, byte[] second) {
    int i = 0;
    while (i < first.length && i < second.length) {
      if (first[i] != second[i]) {
        return Byte.compare(first[i], second[i]);
      }

      i++;
    }

    byte firstByte = i < first.length ? first[i] : 0;
    byte secondByte = i < second.length ? second[i] : 0;
    return Byte.compare(firstByte, secondByte);
  }

  public static int Search(byte[] array, byte value) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == value) {
        return i;
      }
    }

    return -1;
  }

  public static int Search(char[] array, char value) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == value) {
        return i;
      }
    }

    return -1;
  }

  public static byte[] ExtendBegin(byte[] array, int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Amount must be non-negative");
    }

    if (amount == 0) {
      return array;
    }

    byte[] result = new byte[array.length + amount];
    System.arraycopy(array, 0, result, amount, array.length);
    return result;
  }

  public static byte[] Extend(byte[] array, int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Amount must be non-negative");
    }

    if (amount == 0) {
      return array;
    }

    byte[] result = new byte[array.length + amount];
    System.arraycopy(array, 0, result, 0, array.length);
    return result;
  }
}
