package main;

import schneier.Schneier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class MainSchneier {
  public static void main(String[] args) throws IOException {
    RandomAccessFile file = new RandomAccessFile("schneier.txt", "rw");
    file.write("Secret data".getBytes(StandardCharsets.US_ASCII));
    int size = (int)file.getFilePointer();

    file.seek(0);
    Schneier.Erase(file, size);
  }
}
