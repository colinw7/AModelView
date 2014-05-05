package org.colinw.modelview;

class Math3D {
  static final float DotProduct(float x1, float y1, float z1,
                                 float x2, float y2, float z2) {
    return (x1*x2 + y1*y2 + z1*z2);
  }

  static final Vector3D CrossProduct(float x1, float y1, float z1,
                                     float x2, float y2, float z2) {
    return new Vector3D(y1*z2 - z1*y2, z1*x2 - x1*z2, x1*y2 - y1*x2);
  }

  static final int PolygonOrientation(float x1, float y1, float z1,
                                      float x2, float y2, float z2,
                                      float x3, float y3, float z3,
                                      float eye_x, float eye_y, float eye_z) {
    float off_x = eye_x - x1;
    float off_y = eye_y - y1;
    float off_z = eye_z - z1;

    Vector3D d1d2 = CrossProduct(x2 - x1, y2 - y1, z2 - z1, x3 - x2, y3 - y2, z3 - z2);

    float dotprod = DotProduct(d1d2.x(), d1d2.y(), d1d2.z(), off_x, off_y, off_z);

    if      (dotprod > 0.0f) return  1;
    else if (dotprod < 0.0f) return -1;

    return 0;
  }
}
