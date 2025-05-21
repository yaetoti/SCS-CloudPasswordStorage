package utils;

public class ByteUtils {
  public static byte ToByte(int value) {
    return (byte) (value & 0xFF);
  }

  public static int AsUnsignedByte(byte value) {
    return value & 0xFF;
  }

  public static String BytesToStringHex(byte[] bytes, boolean withSpaces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(String.format("%02X", bytes[i]));

      if (withSpaces && i != bytes.length - 1) {
        sb.append(" ");
      }
    }

    return sb.toString();
  }

  public static String BytesToStringOct(byte[] bytes, boolean withSpaces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bytes.length; i++) {
      sb.append(String.format("%03o", bytes[i]));

      if (withSpaces && i != bytes.length - 1) {
        sb.append(" ");
      }
    }

    return sb.toString();
  }
}
