package org.colinw.modelview;

import android.content.Context;
import android.util.Log;

import java.util.Vector;

class ImportV3D extends ImportModel {
  private static final String TAG = "ImportV3D";

  public ImportV3D(ModelViewRenderer renderer) {
    super(renderer);

    both_sides_ = false;
  }

  protected boolean readContents() {
    object_ = new Object(scene_);

    object_.setName("obj");

    scene_.addObject(object_);

    String line = file_.readLine();

    if (line == null || ! line.equals("3DG1")) {
      Log.d(TAG, "Not a V3D File");
      return false;
    }

    line = file_.readLine();

    if (line == null)
      return false;

    long num_vertices = Integer.parseInt(line);

    for (int i = 0; i < num_vertices; i++) {
      line = file_.readLine();

      if (line == null)
         return false;

      Vector<String> words = lineToWords(line);

      if (words.size() != 3) {
        Log.d(TAG, "Invalid Point");
        return false;
      }

      double x = Double.parseDouble(words.get(0));
      double y = Double.parseDouble(words.get(1));
      double z = Double.parseDouble(words.get(2));

      if (debug_)
        Log.d(TAG, "Point " + i + ": " + x + " " + y + " " + z);

      object_.addVertex(new Vertex((float) x, (float) y, (float) z));
    }

    int face_num = -1;

    while ((line = file_.readLine()) != null) {
      Vector<String> words = lineToWords(line);

      if (words.size() == 0)
        continue;

      if (line.charAt(0) == ' ') {
        if (face_num == -1) {
          Log.d(TAG, "Invalid Sub Face/Line");
          return false;
        }

        int is = 0;

        while (is < line.length() && line.charAt(is) == ' ')
          ++is;

        line = line.substring(is);

        long num_faces = Integer.parseInt(line);

        for (int i = 0; i < num_faces; i++) {
          line = file_.readLine();

          if (line == null)
            return false;

          Vector<String> words1 = lineToWords(line);

          int num_vertices1 = Integer.parseInt(words1.get(0));

          if (words1.size() < num_vertices1 + 2) {
            Log.d(TAG, "Invalid Sub Face/Line");
            return false;
          }

          if (num_vertices1 <= 1) {
            Log.d(TAG, "Invalid Sub Face/Line");
            return false;
          }

          long color = Integer.parseInt(words1.get(num_vertices1 + 1));

          RGBA rgba = getColorRGBA(color);

          if (num_vertices1 > 2) {
            Vector<Integer> vertices = new Vector<Integer>();

            for (int j = 0; j < num_vertices1; j++) {
              int vertex_num = Integer.parseInt(words1.get(j + 1));

              if (vertex_num < 0 || vertex_num >= object_.getNumVertices()) {
                Log.d(TAG, "Invalid Sub Face");
                return false;
              }

              vertices.add(vertex_num);
            }

            Face face2 = new Face(object_, vertices);

            face2.calcNormal();

            //int ind = object_.addFaceSubFace(face_num, face2);

            face2.getMaterial().setDiffuse(rgba);
          }
          else {
            int vertex_num1 = Integer.parseInt(words1.get(1));
            int vertex_num2 = Integer.parseInt(words1.get(2));

            Line line2 = new Line(object_, vertex_num1, vertex_num2);

            //int ind = object_.addFaceSubLine(face_num, line2);

            line2.setColor(rgba);
          }
        }
      }
      else {
        int num_vertices1 = Integer.parseInt(words.get(0));

        if (words.size() < num_vertices1 + 2) {
          Log.d(TAG, "Invalid Face/Line");
          return false;
        }

        if (num_vertices1 <= 1) {
          Log.d(TAG, "Invalid Face/Line");
          return false;
        }

        long color = Integer.parseInt(words.get(num_vertices1 + 1));

        RGBA rgba = getColorRGBA(color);

        if (num_vertices1 > 2) {
          Vector<Integer> vertices = new Vector<Integer>();

          for (int i = 0; i < num_vertices1; i++) {
            int vertex_num = Integer.parseInt(words.get(i + 1));

            if (vertex_num < 0 || vertex_num >= object_.getNumVertices()) {
              Log.d(TAG, "Invalid Face");
              return false;
            }

            vertices.add(vertex_num);
          }

          Face face = new Face(object_, vertices);

          face.calcNormal();

          face_num = object_.addFace(face);

          face.getMaterial().setDiffuse(rgba);

          if (both_sides_) {
            Vector<Integer> vertices1 = new Vector<Integer>();

            for (int i = num_vertices1 - 1; i >= 0; i--) {
              int vertex_num = Integer.parseInt(words.get(i + 1));

              if (vertex_num < 0 || vertex_num >= object_.getNumVertices()) {
                Log.d(TAG, "Invalid Face");
                return false;
              }

              vertices1.add(vertex_num);
            }

            Face face1 = new Face(object_, vertices1);

            face1.calcNormal();

            face_num = object_.addFace(face1);

            face1.getMaterial().setDiffuse(rgba);
          }
        }
        else {
          int vertex_num1 = Integer.parseInt(words.get(1));
          int vertex_num2 = Integer.parseInt(words.get(2));

          Line line1 = new Line(object_, vertex_num1, vertex_num2);

          int ind = object_.addLine(line1);

          line1.setColor(rgba);
        }
      }
    }

    return true;
  }

  Vector<String> lineToWords(String line) {
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

  RGBA getColorRGBA(long color) {
    if (color < 0)
      color = -color;

    return new RGBA(((color >> 0) & 0x7)/7.0f,
                    ((color >> 3) & 0x7)/7.0f,
                    ((color >> 6) & 0x7)/7.0f,
                    1.0f);
  }

  private Object  object_;
  private boolean both_sides_;
}
