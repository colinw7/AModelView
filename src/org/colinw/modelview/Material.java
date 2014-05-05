package org.colinw.modelview;

class Material {
  Material() {
    init();
  }

  Material(Material m) {
    init();

    name = m.name;

    transparency = m.transparency;
    shading      = m.shading;
    two_sided    = m.two_sided;

    ambient_   = new RGBA(m.ambient_);
    diffuse_   = new RGBA(m.diffuse_);
    specular_  = new RGBA(m.specular_);
    emission_  = new RGBA(m.emission_);
    shininess_ = m.shininess_;
    mirror_    = m.mirror_;
  }

  void init() {
    name = new String("");

    transparency = 0.0;
    shading      = 0;
    two_sided    = false;

    ambient_   = new RGBA(0.1f, 0.1f, 0.1f, 1.0f);
    diffuse_   = new RGBA(1.0f, 1.0f, 1.0f, 1.0f);
    specular_  = new RGBA(0.0f, 0.0f, 0.0f, 1.0f);
    emission_  = new RGBA(0.0f, 0.0f, 0.0f, 1.0f);
    shininess_ = 1.0;
    mirror_    = false;
  }

  public RGBA getAmbient() { return ambient_; }
  public void setAmbient(RGBA ambient) { ambient_ = ambient; }

  public RGBA getDiffuse() { return diffuse_; }
  public void setDiffuse(RGBA diffuse) { diffuse_ = diffuse; }

  public RGBA getSpecular() { return specular_; }
  public void setSpecular(RGBA specular) { specular_ = specular; }

  public RGBA getEmission() { return emission_; }
  public void setEmission(RGBA emission) { emission_ = emission; }

  public double getShininess() { return shininess_; }
  public void setShininess(double shininess) { shininess_ = shininess; }

  public boolean isMirror() { return mirror_; }
  public void setMirror(boolean mirror) { mirror_ = mirror; }

  String  name;
  double  transparency;
  int     shading; // 1=flat, 2=gouraud, 3=phong, 4=metal
  boolean two_sided;

  private RGBA    ambient_;
  private RGBA    diffuse_;
  private RGBA    specular_;
  private RGBA    emission_;
  private double  shininess_;
  private boolean mirror_;
}
