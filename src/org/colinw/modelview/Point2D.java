package org.colinw.modelview;

class Point2D {
  Point2D() {
    data_[0] = 0.0f;
    data_[1] = 0.0f;
  }

  Point2D(float x, float y) {
    data_[0] = x;
    data_[1] = y;
  }

  float [] getData() { return data_; }

  float x() { return data_[0]; }
  float y() { return data_[1]; }

  private float [] data_ = new float [2];
}
