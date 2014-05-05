package org.colinw.modelview;

import java.util.Vector;

class StrUtil {
  static final Vector<String> lineToWords(String line) {
    Vector<String> words = new Vector<String>();

    int i    = 0;
    int len  = line.length();
    int wlen = 0;

    StringBuffer sb = new StringBuffer("");

    while (i < len) {
      char c = line.charAt(i);

      ++i;

      if (! Character.isSpace(c)) {
        sb.append(c);

        ++wlen;
      }
      else {
        if (wlen > 0) {
          words.add(new String(sb));

          sb = new StringBuffer("");

          wlen = 0;
        }

        while (i < len && Character.isSpace(line.charAt(i)))
          ++i;
      }
    }

    if (wlen > 0)
      words.add(new String(sb));

    return words;
  }

  static final Vector<String> lineToFields(String line, String sep) {
    String [] strs = line.split(sep);


    Vector<String> vstrs = new Vector<String>();

    for (int i = 0; i < strs.length; ++i)
      vstrs.add(strs[i]);

    return vstrs;
  }

  public static String stripSpaces(String str) {
    return str.trim();
  }

  public static boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
    }
    catch(NumberFormatException e) {
      return false;
    }

    return true;
  }

  public static boolean isReal(String str) {
    try {
      Double.parseDouble(str);
    }
    catch(NumberFormatException e) {
      return false;
    }

    return true;
  }

  public static int toInteger(String str) {
    return Integer.parseInt(str);
  }

  public static double toReal(String str) {
    return Double.parseDouble(str);
  }
}
