package org.colinw.modelview;

class Vertex {
  Vertex() {
    init();
  }

  Vertex(float x, float y, float z) {
    init();

    v_ = new Vector3D(x, y, z);
  }

  Vertex(Vertex v) {
    init();

    v_ = new Vector3D(v.v_);
  }

  void init() {
    v_ = new Vector3D();

    normal_ = new Vector3D();

    hasNormal_ = false;
  }

  Vector3D v() { return v_; }

  float x() { return v_.x(); }
  float y() { return v_.y(); }
  float z() { return v_.z(); }

  boolean hasNormal() { return hasNormal_; }

  Vector3D getNormal() { return normal_; }

  void setNormal(Vector3D n) { normal_ = n; hasNormal_ = true; }

  void transform(Matrix3D m) {
    v_.transform(m);
  }

  private Vector3D v_;
  private Vector3D normal_;
  private boolean  hasNormal_;
}
