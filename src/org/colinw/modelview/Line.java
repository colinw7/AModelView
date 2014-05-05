package org.colinw.modelview;

import java.util.Vector;

class Line {
  Line(Object object, int vertex1, int vertex2) {
    object_  = object;
    vertex1_ = vertex1;
    vertex2_ = vertex2;

    color_ = new RGBA(1.0f, 1.0f, 1.0f, 1.0f);
  }

  Line(Object object, Line l) {
    object_  = object;
    vertex1_ = l.vertex1_;
    vertex2_ = l.vertex2_;
    color_   = new RGBA(l.color_);
  }

  void setColor(RGBA c) {
    color_ = c;
  }

  RGBA getColor() { return color_; }

  Vertex getVertex(int i) {
    if (i == 0) return object_.getVertex(vertex1_);
    else        return object_.getVertex(vertex2_);
  }

  private Object object_;
  private int    vertex1_;
  private int    vertex2_;
  private RGBA   color_;
}
