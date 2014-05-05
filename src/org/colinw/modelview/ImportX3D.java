package org.colinw.modelview;

import android.content.Context;

import java.util.Vector;

class ImportX3D extends ImportModel {
  private static final String TAG = "ImportX3D";

  public ImportX3D(ModelViewRenderer renderer) {
    super(renderer);
  }

  protected boolean readContents() {
    object_ = new Object(scene_);

    object_.setName("obj");

    scene_.addObject(object_);

    String line = file_.readLine();

    Vector<String> words = StrUtil.lineToWords(line);

    int num_points = Integer.parseInt(words.get(0));
    int num_lines  = Integer.parseInt(words.get(1));

    for (int i = 0; i < num_points; i++) {
      line = file_.readLine();

      Vector<String> words1 = StrUtil.lineToWords(line);

      double x =  Double.parseDouble(words1.get(0));
      double y = -Double.parseDouble(words1.get(1));
      double z =  Double.parseDouble(words1.get(2));

      object_.addVertex(new Vertex((float) x, (float) y, (float) z));
    }

    for (int j = 0; j < num_lines; j++) {
      line = file_.readLine();

      Vector<String> words1 = StrUtil.lineToWords(line);

      int start = Integer.parseInt(words1.get(0));
      int end   = Integer.parseInt(words1.get(1));

      object_.addLine(new Line(object_, start, end));
    }

    return true;
  }

  private Object object_;
}
