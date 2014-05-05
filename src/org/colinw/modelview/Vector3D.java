package org.colinw.modelview;

import android.opengl.Matrix;

class Vector3D {
  Vector3D() {
    data_[0] = 0.0f;
    data_[1] = 0.0f;
    data_[2] = 0.0f;
    data_[3] = 1.0f;
  }

  Vector3D(float x, float y, float z) {
    data_[0] = x;
    data_[1] = y;
    data_[2] = z;
    data_[3] = 1.0f;
  }

  Vector3D(Vector3D v) {
    for (int i = 0; i < 4; ++i)
      data_[i] = v.data_[i];
  }

  float [] getData() { return data_; }

  float x() { return data_[0]; }
  float y() { return data_[1]; }
  float z() { return data_[2]; }
  float w() { return data_[3]; }

  void normalize() {
    float l = length();

    if (l > 0.0)
      scale(1.0f/l);

    data_[3] = 1.0f;
  }

  void add(Vector3D v) {
    for (int i = 0; i < 3; ++i)
      data_[i] += v.data_[i];
  }

  void scale(float s) {
    for (int i = 0; i < 3; ++i)
      data_[i] *= s;
  }

  void transform(Matrix3D m) {
    Matrix.multiplyMV(work_, 0, m.getData(), 0, data_, 0);

    for (int i = 0; i < 4; ++i)
      data_[i] = work_[i];
  }

  float length() {
    return (float) Math.sqrt(x()*x() + y()*y() + z()*z());
  }

  static Vector3D diff(Vector3D lhs, Vector3D rhs) {
    return new Vector3D(lhs.x() - rhs.x(), lhs.y() - rhs.y(), lhs.z() - rhs.z());
  }

  private float [] data_ = new float [4];
  private float [] work_ = new float [4];
}
