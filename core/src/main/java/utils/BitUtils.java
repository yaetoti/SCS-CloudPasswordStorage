package utils;

import org.jetbrains.annotations.Range;

public final class BitUtils {
  public static byte[] ShiftLeftBE(byte[] data, int amount) {
    final int shiftMod = amount % 8;
    final byte carryMask = (byte) ((1 << shiftMod) - 1);
    final int offsetBytes = (amount / 8);

    int sourceIndex;
    for (int i = 0; i < data.length; i++) {
      sourceIndex = i + offsetBytes;
      if (sourceIndex >= data.length) {
        data[i] = 0;
      }
      else {
        byte src = data[sourceIndex];
        byte dst = (byte) (src << shiftMod);
        if (sourceIndex + 1 < data.length) {
          dst |= (byte) (data[sourceIndex + 1] >>> (8 - shiftMod) & carryMask);
        }

        data[i] = dst;
      }
    }

    return data;
  }

  public static byte[] ShiftRightBE(byte[] data, int amount) {
    final int shiftMod = amount % 8;
    final byte carryMask = (byte) (0xFF << (8 - shiftMod));
    final int offsetBytes = (amount / 8);

    int sourceIndex;
    for (int i = data.length - 1; i >= 0; i--) {
      sourceIndex = i - offsetBytes;
      if (sourceIndex < 0) {
        data[i] = 0;
      }
      else {
        byte src = data[sourceIndex];
        byte dst = (byte) ((0xff & src) >>> shiftMod);
        if (sourceIndex - 1 >= 0) {
          dst |= (byte) (data[sourceIndex - 1] << (8 - shiftMod) & carryMask);
        }

        data[i] = dst;
      }
    }

    return data;
  }

  public static int GetBitLE(byte[] data, int index) {
    if (index < 0 || index >= data.length * Byte.SIZE) {
      throw new IndexOutOfBoundsException();
    }

    int byteIndex = index / Byte.SIZE;
    int bitIndex = index % Byte.SIZE;
    return (data[byteIndex] >>> bitIndex) & 1;
  }

  public static void SetBitLE(byte[] data, int index, int value) {
    if (index < 0 || index >= data.length * Byte.SIZE) {
      throw new IndexOutOfBoundsException();
    }

    int byteIndex = index / Byte.SIZE;
    int bitIndex = index % Byte.SIZE;
    int mask = 1 << bitIndex;
    if (value == 0) {
      data[byteIndex] &= (byte) ~mask;
    }
    else {
      data[byteIndex] |= (byte) mask;
    }
  }

  public static int GetBitBE(byte[] data, int index) {
    if (index < 0 || index >= data.length * Byte.SIZE) {
      throw new IndexOutOfBoundsException();
    }

    int byteIndex = data.length - index / Byte.SIZE - 1;
    int bitIndex = index % Byte.SIZE;
    return (data[byteIndex] >>> bitIndex) & 1;
  }

  public static void SetBitBE(byte[] data, int index, int value) {
    if (index < 0 || index >= data.length * Byte.SIZE) {
      throw new IndexOutOfBoundsException();
    }

    int byteIndex = data.length - index / Byte.SIZE - 1;
    int bitIndex = index % Byte.SIZE;
    int mask = 1 << bitIndex;
    if (value == 0) {
      data[byteIndex] &= (byte) ~mask;
    }
    else {
      data[byteIndex] |= (byte) mask;
    }
  }

  static byte CreateByteMask(@Range(from = 0, to = Byte.SIZE) int bits) {
    return (byte)((1 << bits) - 1);
  }

  static short CreateShortMask(@Range(from = 0, to = Short.SIZE) int bits) {
    return (short)((1 << bits) - 1);
  }

  static int CreateIntMask(@Range(from = 0, to = Integer.SIZE) int bits) {
    return (1 << bits) - 1;
  }

  static long CreateLongMask(@Range(from = 0, to = Long.SIZE) int bits) {
    return ((1L << bits) - 1L);
  }

  public static byte MaskBits(byte value, @Range(from = 0, to = Byte.SIZE) int bits) {
    return (byte)(value & CreateByteMask(bits));
  }

  public static short MaskBits(short value, @Range(from = 0, to = Short.SIZE) int bits) {
    return (short)(value & CreateShortMask(bits));
  }

  public static int MaskBits(int value, @Range(from = 0, to = Integer.SIZE) int bits) {
    return value & CreateIntMask(bits);
  }

  public static long MaskBits(long value, @Range(from = 0, to = Long.SIZE) int bits) {
    return value & CreateLongMask(bits);
  }

  /**
   * @param value value
   * @return The number of bits in which a value can be represented
   */
  public static int GetBitLength(int value) {
    if (value == 0) {
      return 1;
    }

    if (value == Integer.MIN_VALUE) {
      return Integer.SIZE;
    }

    return Integer.SIZE - Integer.numberOfLeadingZeros(Math.abs(value));
  }

  /**
   * @param value value
   * @return The amount of bits in which a value can be represented
   */
  public static int GetBitLength(long value) {
    if (value == 0) {
      return 1;
    }

    if (value == Long.MIN_VALUE) {
      return Long.SIZE;
    }

    return Long.SIZE - Long.numberOfLeadingZeros(Math.abs(value));
  }
}

