package org.colinw.modelview;

class Light {
  Light() {
    direction_ = new Vector3D(1.0f, 1.0f, 1.0f);
    ambient_   = new RGBA(0.1f, 0.1f, 0.1f, 1.0f);
    diffuse_   = new RGBA(0.6f, 0.6f, 0.6f, 1.0f);
  }

  public Vector3D getDirection() { return direction_; }
  public void setDirection(Vector3D d) { direction_ = d; }

  public RGBA getAmbient() { return ambient_; }
  public void setAmbient(RGBA ambient) { ambient_ = ambient; }

  public RGBA getDiffuse() { return diffuse_; }
  public void setDiffuse(RGBA diffuse) { diffuse_ = diffuse; }

  private Vector3D direction_;
  private RGBA     ambient_;
  private RGBA     diffuse_;
}
