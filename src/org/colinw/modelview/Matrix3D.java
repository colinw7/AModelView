package org.colinw.modelview;

import android.opengl.Matrix;

class Matrix3D {
  Matrix3D() {
    data_ = new float [16];

    reset();
  }

  float [] getData() { return data_; }

  void reset() {
    Matrix.setIdentityM(data_, 0);
  }

  static void multiply(Matrix3D res, Matrix3D lhs, Matrix3D rhs) {
    Matrix.multiplyMM(res.getData(), 0, lhs.getData(), 0, rhs.getData(), 0);
  }

  static void multiply(Vector3D res, Matrix3D lhs, Vector3D rhs) {
    Matrix.multiplyMV(res.getData(), 0, lhs.getData(), 0, rhs.getData(), 0);
  }

  static Matrix3D multiply(Matrix3D lhs, Matrix3D rhs) {
    Matrix3D res = new Matrix3D();

    multiply(res, lhs, rhs);

    return res;
  }

  static Matrix3D newTranslation(float dx, float dy, float dz) {
    Matrix3D res = new Matrix3D();

    res.translate(dx, dy, dz);

    return res;
  }

  static Matrix3D newScale(float sx, float sy, float sz) {
    Matrix3D res = new Matrix3D();

    res.scale(sx, sy, sz);

    return res;
  }

  static Matrix3D newRotate(float xa, float ya, float za) {
    Matrix3D res = new Matrix3D();

    res.rotateXAxis(xa);
    res.rotateYAxis(ya);
    res.rotateZAxis(za);

    return res;
  }

  void translate(float dx, float dy, float dz) { Matrix.translateM(getData(), 0, dx, dy, dz); }

  void scale(float s) {Matrix.scaleM(getData(), 0, s, s, s); }

  void scale(float sx, float sy, float sz) {Matrix.scaleM(getData(), 0, sx, sy, sz); }

  void rotateXAxis(float a) { Matrix.rotateM(getData(), 0, a, 1.0f, 0.0f, 0.0f); }
  void rotateYAxis(float a) { Matrix.rotateM(getData(), 0, a, 0.0f, 1.0f, 0.0f); }
  void rotateZAxis(float a) { Matrix.rotateM(getData(), 0, a, 0.0f, 0.0f, 1.0f); }

  void rotate(float xa, float ya, float za) {
    rotateXAxis(xa);
    rotateYAxis(ya);
    rotateZAxis(za);
  }

  static Matrix3D newOrtho(float left, float right, float bottom, float top) {
    Matrix3D res = new Matrix3D();

    res.ortho(left, right, bottom, top);

    return res;
  }

  void ortho(float left, float right, float bottom, float top) {
    Matrix.orthoM(getData(), 0, left, right, bottom, top, -1.0f, 1.0f);
  }

  void ortho(float left, float right, float bottom, float top, float near, float far) {
    Matrix.orthoM(getData(), 0, left, right, bottom, top, near, far);
  }

  static Matrix3D newPerspective(float fov, float aspect, float near, float far) {
    Matrix3D res = new Matrix3D();

    res.perspective(fov, aspect, near, far);

    return res;
  }

  void perspective(float fov, float aspect, float near, float far) {
    Matrix.perspectiveM(getData(), 0, fov, aspect, near, far);
  }

  static Matrix3D newLookAt(float eye_x, float eye_y, float eye_z,
                            float target_x, float target_y, float target_z) {
    Matrix3D res = new Matrix3D();

    res.lookAt(eye_x, eye_y, eye_z, target_x, target_y, target_z);

    return res;
  }

  void lookAt(float eye_x, float eye_y, float eye_z,
              float target_x, float target_y, float target_z) {
    Matrix.setLookAtM(getData(), 0, eye_x, eye_y, eye_z, target_x, target_y, target_z, 0, 1, 0);
  }

  private float [] data_;
}
