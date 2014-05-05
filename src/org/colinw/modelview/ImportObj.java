package org.colinw.modelview;

import android.content.Context;
import android.util.Log;

import java.util.Vector;

class ImportObj extends ImportModel {
  private static final String TAG = "ImportObj";

  public ImportObj(ModelViewRenderer renderer) {
    super(renderer);
  }

  protected boolean readContents() {
    object_ = new Object(scene_);

    object_.setName("obj");

    scene_.addObject(object_);

    vnum_  = 0;
    vnnum_ = 0;

    String line;

    while ((line = file_.readLine()) != null) {
      String line1 = StrUtil.stripSpaces(line);

      int len = line1.length();

      if (len <= 0 || line1.charAt(0) == '#')
        continue;

      if      (len > 2 && line1.charAt(0) == 'v' && line1.charAt(1) == ' ') {
        line1 = StrUtil.stripSpaces(line1.substring(2));

        if (! readVertex(line1))
          Log.d(TAG, "Invalid vertex line " + line);

        ++vnum_;
      }
      else if (len > 2 && line1.charAt(0) == 'v' && line1.charAt(1) == 't') {
        line1 = StrUtil.stripSpaces(line1.substring(2));

        if (! readTextureVertex(line1))
          Log.d(TAG, "Invalid texture vertex line " + line);

        ++vtnum_;
      }
      else if (len > 2 && line1.charAt(0) == 'v' && line1.charAt(1) == 'n') {
        line1 = StrUtil.stripSpaces(line1.substring(2));

        if (! readVertexNormal(line1))
          Log.d(TAG, "Invalid vertex normal line " + line);

        ++vnnum_;
      }
      else if (len > 2 && line1.charAt(0) == 'v' && line1.charAt(1) == 'p') {
        line1 = StrUtil.stripSpaces(line1.substring(2));

        if (! readParameterVertex(line1))
          Log.d(TAG, "Invalid parameter vertex line " + line);
      }
      else if (len > 2 && line1.charAt(0) == 'g' && line1.charAt(1) == ' ') {
        line1 = StrUtil.stripSpaces(line1.substring(2));

        if (! readGroupName(line1))
          Log.d(TAG, "Invalid group name line " + line);
      }
      else if (len > 2 && line1.charAt(0) == 'f' && line1.charAt(1) == ' ') {
        line1 = StrUtil.stripSpaces(line1.substring(2));

        if (! readFace(line1))
          Log.d(TAG, "Invalid face line " + line);
      }
      else if (len > 2 && line1.charAt(0) == 'o' && line1.charAt(1) == ' ') {
        // skip object name
      }
      else if (len > 2 && line1.charAt(0) == 's' && line1.charAt(1) == ' ') {
        // skip smoothing group
      }
      else if (len > 6 && line1.substring(0, 6).equals("mtllib")) {
        // todo
      }
      else if (len > 6 && line1.substring(0, 6).equals("usemtl")) {
        // todo
      }
      else
        Log.d(TAG, "Unrecognised line " + line);
    }

    return true;
  }

  boolean readVertex(String line) {
    Vector<String> words = StrUtil.lineToWords(line);

    if (words.size() != 3)
      return false;

    if (! StrUtil.isReal(words.get(0)) ||
        ! StrUtil.isReal(words.get(1)) ||
        ! StrUtil.isReal(words.get(2)))
      return false;

    double x = StrUtil.toReal(words.get(0));
    double y = StrUtil.toReal(words.get(1));
    double z = StrUtil.toReal(words.get(2));

    Vertex v = new Vertex((float) x, (float) y, (float) z);

    object_.addVertex(v);

    return true;
  }

  boolean readTextureVertex(String line) {
    Vector<String> words = StrUtil.lineToWords(line);

    if (words.size() != 2)
      return false;

    if (! StrUtil.isReal(words.get(0)) ||
        ! StrUtil.isReal(words.get(1)))
      return false;

    //double x = StrUtil.toReal(words.get(0));
    //double y = StrUtil.toReal(words.get(1));

    //CPoint2D p(x, y);

    //CGeomVertex3D &vertex = object_.getVertex(vtnum_);

    //vertex.setTextureMap(p);

    return true;
  }

  boolean readVertexNormal(String line) {
    Vector<String> words = StrUtil.lineToWords(line);

    if (words.size() != 3)
      return false;

    if (! StrUtil.isReal(words.get(0)) ||
        ! StrUtil.isReal(words.get(1)) ||
        ! StrUtil.isReal(words.get(2)))
      return false;

    double x = StrUtil.toReal(words.get(0));
    double y = StrUtil.toReal(words.get(1));
    double z = StrUtil.toReal(words.get(2));

    Vector3D n = new Vector3D((float) x, (float) y, (float) z);

    Vertex vertex = object_.getVertex(vnnum_);

    vertex.setNormal(n);

    return true;
  }

  boolean readParameterVertex(String line) {
    return true;
  }

  boolean readGroupName(String line) {
    return true;
  }

  boolean readFace(String line) {
    Vector<Integer> vertices = new Vector<Integer>();

    Vector<String> words = StrUtil.lineToWords(line);

    int num_words = words.size();

    for (int i = 0; i < num_words; i++) {
      Vector<String> fields = StrUtil.lineToFields(words.get(i), "/");

      while (fields.size() < 3)
        fields.add(new String(""));

      int num1 = -1;
      int num2 = -1;
      int num3 = -1;

      if (StrUtil.isInteger(fields.get(0)))
        num1 = StrUtil.toInteger(fields.get(0));

      if (StrUtil.isInteger(fields.get(1)))
        num2 = StrUtil.toInteger(fields.get(1));

      if (StrUtil.isInteger(fields.get(2)))
        num3 = StrUtil.toInteger(fields.get(2));

      if (num1 > 0)
        vertices.add(num1 - 1);

      assert(num2 > 0 && num3 > 0);
    }

    Face face = new Face(object_, vertices);

    face.calcNormal();

    object_.addFace(face);

    return true;
  }

  private Object object_;
  private int    vnum_;
  private int    vnnum_;
  private int    vtnum_;
}
