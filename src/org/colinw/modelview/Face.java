package org.colinw.modelview;

import java.util.Vector;

class Face {
  Face(Object object, Vector<Integer> vertices) {
    init(object);

    vertices_ = vertices;
  }

  Face(Object object, int v1, int v2, int v3) {
    init(object);

    vertices_.add(v1);
    vertices_.add(v2);
    vertices_.add(v3);
  }

  Face(Object object, int v1, int v2, int v3, int v4) {
    init(object);

    vertices_.add(v1);
    vertices_.add(v2);
    vertices_.add(v3);
    vertices_.add(v4);
  }

  Face(Object object, Face f) {
    init(object);

    for (Integer i : f.vertices_)
      vertices_.add(i);

    for (Point2D p : f.tpoints_)
      tpoints_.add(new Point2D(p.x(), p.y()));

    for (Face face : f.subFaces_)
      subFaces_.add(new Face(object, face));

    for (Line line : f.subLines_)
      subLines_.add(new Line(object, line));

    material_ = new Material(f.material_);

    normal_ = new Vector3D(f.normal_);

    hasNormal_ = f.hasNormal_;
  }

  void init(Object object) {
    object_   = object;
    vertices_ = new Vector<Integer>();
    tpoints_  = new Vector<Point2D>();
    subFaces_ = new Vector<Face>();
    subLines_ = new Vector<Line>();

    bbox_ = new BBox();

    material_ = new Material();

    normal_ = new Vector3D();

    hasNormal_ = false;
  }

  void setMaterial(Material m) {
    material_ = m;
  }

  Material getMaterial() { return material_; }

  void setTwoSided(boolean b) {
    twoSided_ = b;
  }

  int getNumVertices() {
    return vertices_.size();
  }

  Vertex getVertex(int i) {
    return object_.getVertex(vertices_.get(i));
  }

  Point2D getTexturePoint(int i) {
    if (tpoints_.size() == 0) {
      setTextureRange(0.0f, 0.0f, 1.0f, 1.0f, false);
    }

    return tpoints_.get(i);
  }

  void setTextureRange(float x1, float y1, float x2, float y2, boolean invert) {
    tpoints_.clear();

    if      (vertices_.size() == 3) {
      float xc = (x1 + x2)/2;

      if (! invert) {
        tpoints_.add(new Point2D(x1, y2));
        tpoints_.add(new Point2D(x2, y2));
        tpoints_.add(new Point2D(xc, y1));
      }
      else {
        tpoints_.add(new Point2D(x1, y1));
        tpoints_.add(new Point2D(x2, y1));
        tpoints_.add(new Point2D(xc, y2));
      }
    }
    else if (vertices_.size() == 4) {
      tpoints_.add(new Point2D(x1, y2));
      tpoints_.add(new Point2D(x2, y2));
      tpoints_.add(new Point2D(x2, y1));
      tpoints_.add(new Point2D(x1, y1));
    }
    else {
      double xc = (x1 + x2)/2.0;
      double xr = Math.abs(x2 - x1)/2.0;
      double yc = (y1 + y2)/2.0;
      double yr = Math.abs(y2 - y1)/2.0;

      double da = 2.0*Math.PI/vertices_.size();
      double a  = 0;

      for (int i1 = 0; i1 < vertices_.size(); ++i1) {
        double c = Math.cos(a);
        double s = Math.sin(a);

        tpoints_.add(new Point2D((float) (xc + xr*c), (float) (yc + yr*s)));

        a += da;
      }
    }
  }

  int addSubFace(Face face) {
    subFaces_.add(face);

    return getNumSubFaces() - 1;
  }

  Face getSubFace(int i) {
    return subFaces_.get(i);
  }

  int getNumSubFaces() {
    return subFaces_.size();
  }

  int addSubLine(Line line) {
    subLines_.add(line);

    return getNumSubLines() - 1;
  }

  Line getSubLine(int i) {
    return subLines_.get(i);
  }

  int getNumSubLines() {
    return subLines_.size();
  }

  void calcNormal() {
    Vertex v1 = getVertex(0);
    Vertex v2 = getVertex(1);
    Vertex v3 = getVertex(2);

    Vector3D v21 = Vector3D.diff(v2.v(), v1.v());
    Vector3D v32 = Vector3D.diff(v3.v(), v2.v());

    normal_ = Math3D.CrossProduct(v21.x(), v21.y(), v21.z(), v32.x(), v32.y(), v32.z());

    normal_.normalize();

    hasNormal_ = true;
  }

  boolean hasNormal() { return hasNormal_; }

  Vector3D getNormal() { return normal_; }

  void setNormal(Vector3D n) { normal_ = n; hasNormal_ = true; }

  void setColor(RGBA c) {
    material_.setDiffuse(c);
  }

  void setSubFaceColor(RGBA c) {
    for (Face face : subFaces_)
      face.setColor(c);
  }

  BBox getBBox() {
    if (! bbox_.isSet()) {
      for (Integer i : vertices_) {
        Vertex v = object_.getVertex(i);

        bbox_.addPoint(v.x(), v.y(), v.z());
      }
    }

    return bbox_;
  }

  void drawSubFaces() {
    for (Face face : subFaces_) 
      object_.drawSubFace(face);
  }

  void drawSubLines() {
    for (Line line : subLines_) 
      object_.drawSubLine(line);
  }

  private Object          object_;
  private Vector<Integer> vertices_;
  private Vector<Point2D> tpoints_;
  private Vector<Face>    subFaces_;
  private Vector<Line>    subLines_;
  private Material        material_;
  private boolean         twoSided_;
  private BBox            bbox_;
  private Vector3D        normal_;
  private boolean         hasNormal_;
}
