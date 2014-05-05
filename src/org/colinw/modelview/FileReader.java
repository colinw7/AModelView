package org.colinw.modelview;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

class FileReader {
  FileReader(Context context, String filename) {
    filename_ = filename;

    try {
      AssetManager a = context.getAssets();

      is_ = a.open(filename);
    }
    catch (IOException e) {
      is_ = null;
    }
  }

  boolean isValid() { return is_ != null; }

  float readFloat() throws IOException {
    byte [] b = new byte [4];

    is_.read(b);

    int asInt =  (b[0] & 0xFF)        | ((b[1] & 0xFF) << 8) |
                ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);

    return Float.intBitsToFloat(asInt);
  }

  int readShort() throws IOException {
    byte [] b = new byte [2];

    is_.read(b);

    return ((b[0] & 0xFF) | ((b[1] & 0xFF) << 8));
  }

  long readLong() throws IOException {
    byte [] b = new byte [4];

    is_.read(b);

    int asInt =  (b[0] & 0xFF)        | ((b[1] & 0xFF) << 8) |
                ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);

    return asInt;
  }

  int readChar() throws IOException {
    return is_.read();
  }

  String readLine() {
    final int _CR = 13;
    final int _LF = 10;

    StringBuffer sb = new StringBuffer("");

    try {
      int ch = is_.read();

      if (ch == -1) return null;

      while (ch != _CR && ch != _LF)  {
        sb.append((char) ch);

        ch = is_.read();

        if (ch == -1)
          break;
      }

      // Read the next byte and check if it's a LF
      is_.mark(1);

      ch = is_.read();

      if (ch != -1 && ch != _LF) {
        is_.reset();
      }
    }
    catch (IOException e) {
      return null;
    }

    return new String(sb);
  }

  private String      filename_;
  private InputStream is_;
}
