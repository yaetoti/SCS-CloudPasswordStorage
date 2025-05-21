package schneier;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class Schneier {
  public static void Erase(RandomAccessFile file, long count) throws IOException {
    long position = file.getFilePointer();
    long index = 0;

    // Pass 1: zeros
    file.seek(position);
    while (index < count) {
      file.writeByte(0);
      ++index;
    }

    index = 0;

    // Pass 2: ones
    file.seek(position);
    while (index < count) {
      file.writeByte(0);
      ++index;
    }

    index = 0;

    // Pass 3-7: ¯\_(ツ)_/¯
    Random random = new Random();

    for (int i = 0; i < 5; ++i) {
      file.seek(position);
      while (index < count) {
        file.writeByte(random.nextInt());
        ++index;
      }

      index = 0;
    }
  }
}
