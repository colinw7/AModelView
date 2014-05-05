package org.colinw.modelview;

class Point3D {
  Point3D() {
    data_[0] = 0.0f;
    data_[1] = 0.0f;
    data_[2] = 0.0f;
  }

  Point3D(float x, float y, float z) {
    data_[0] = x;
    data_[1] = y;
    data_[2] = z;
  }

  float [] getData() { return data_; }

  float x() { return data_[0]; }
  float y() { return data_[1]; }
  float z() { return data_[2]; }

  void add(Point3D p) {
    for (int i = 0; i < 3; ++i)
      data_[i] += p.data_[i];
  }

  void scale(float s) {
    for (int i = 0; i < 3; ++i)
      data_[i] *= s;
  }

  static Point3D diff(Point3D lhs, Point3D rhs) {
    return new Point3D(lhs.x() - rhs.x(), lhs.y() - rhs.y(), lhs.z() - rhs.z());
  }

  private float [] data_ = new float [3];
}
