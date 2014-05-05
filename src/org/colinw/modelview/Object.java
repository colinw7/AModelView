package org.colinw.modelview;

import java.util.Vector;

class Object {
  Object(Scene scene) {
    init(scene);
  }

  Object(Scene scene, String name) {
    init(scene);

    setName(name);
  }

  Object(Scene scene, Object o) {
    init(scene);

    name_ = o.name_;

    for (Vertex v : o.vertices_)
      vertices_.add(new Vertex(v));

    for (Face f : o.faces_)
      faces_.add(new Face(this, f));

    for (Line l : o.lines_)
      lines_.add(new Line(this, l));

    texture_ = o.texture_;
  }

  void init(Scene scene) {
    scene_ = scene;

    vertices_ = new Vector<Vertex>();
    faces_    = new Vector<Face>();
    lines_    = new Vector<Line>();

    texture_ = 0;

    bbox_ = new BBox();

    translate_ = Matrix3D.newTranslation(0.0f, 0.0f, 0.0f);
    scale_     = Matrix3D.newScale      (1.0f, 1.0f, 1.0f);
    rotate_    = Matrix3D.newRotate     (0.0f, 0.0f, 0.0f);

    tv_ = new Vertex();

    updateView();
  }

  String getName() { return name_; }

  void setName(String name) {
    name_ = name;
  }

  int addVertex(Vertex v) {
    vertices_.add(v);

    return getNumVertices() - 1;
  }

  Vertex getVertex(int i) {
    return vertices_.get(i);
  }

  int getNumVertices() {
    return vertices_.size();
  }

  void swapVertices(int i1, int i2) {
    Vertex tv = vertices_.get(i1);

    vertices_.set(i1, vertices_.get(i2));
    vertices_.set(i2, tv);
  }

  int addFace(Face face) {
    faces_.add(face);

    return getNumFaces() - 1;
  }

  Face getFace(int i) {
    return faces_.get(i);
  }

  int getNumFaces() {
    return faces_.size();
  }

  int addLine(Line line) {
    lines_.add(line);

    return getNumLines() - 1;
  }

  Line getLine(int i) {
    return lines_.get(i);
  }

  int getNumLines() {
    return lines_.size();
  }

  int getTexture() { return texture_; }

  void setTexture(int texture) { texture_ = texture; }

  BBox getBBox() {
    if (! bbox_.isSet()) {
      for (Face face : faces_) {
        bbox_.addBBox(face.getBBox());
      }

      for (Line line : lines_) {
        Vertex v1 = line.getVertex(0);
        Vertex v2 = line.getVertex(1);

        bbox_.addPoint(v1.x(), v1.y(), v1.z());
        bbox_.addPoint(v2.x(), v2.y(), v2.z());
      }
    }

    return bbox_;
  }

  void move(float dx, float dy, float dz) {
    translate_.translate(dx, dy, dz);

    updateView();
  }

  void rotate(float xa, float ya, float za) {
    rotate_.rotate(xa, ya, za);

    updateView();
  }

  void scale(float s) {
    scale_.scale(s);

    updateView();
  }

  void resetView() {
    translate_.reset();
    rotate_   .reset();
    scale_    .reset();

    updateView();
  }

  void transform(Matrix3D m) {
    for (Vertex v : vertices_)
      v.transform(m);
  }

  void setFaceColor(RGBA c) {
    for (Face face : faces_)
      face.setColor(c);
  }

  void setSubFaceColor(RGBA c) {
    for (Face face : faces_)
      face.setSubFaceColor(c);
  }

  void addSphere(double radius, int num_xy, int num_patches) {
    double [] x = new double [num_xy];
    double [] y = new double [num_xy];

    double a = -Math.PI*0.5;

    double da = Math.PI/(num_xy - 1);

    for (int i = 0; i < num_xy; ++i) {
      x[i] = radius*Math.cos(a);
      y[i] = radius*Math.sin(a);

      a += da;
    }

    addBodyRev(x, y, num_xy, num_patches);
  }

  void addCube(double xc, double yc, double zc, double r) {
    int [] vertices = new int [8];

    vertices[0] = addVertex(new Vertex((float) (xc + r/2), (float) (yc - r/2), (float) (zc + r/2)));
    vertices[1] = addVertex(new Vertex((float) (xc + r/2), (float) (yc - r/2), (float) (zc - r/2)));
    vertices[2] = addVertex(new Vertex((float) (xc + r/2), (float) (yc + r/2), (float) (zc - r/2)));
    vertices[3] = addVertex(new Vertex((float) (xc + r/2), (float) (yc + r/2), (float) (zc + r/2)));
    vertices[4] = addVertex(new Vertex((float) (xc - r/2), (float) (yc - r/2), (float) (zc + r/2)));
    vertices[5] = addVertex(new Vertex((float) (xc - r/2), (float) (yc - r/2), (float) (zc - r/2)));
    vertices[6] = addVertex(new Vertex((float) (xc - r/2), (float) (yc + r/2), (float) (zc - r/2)));
    vertices[7] = addVertex(new Vertex((float) (xc - r/2), (float) (yc + r/2), (float) (zc + r/2)));

    Face [] faces = new Face [6];

    faces[0] = new Face(this, vertices[0], vertices[1], vertices[2], vertices[3]);
    faces[1] = new Face(this, vertices[1], vertices[5], vertices[6], vertices[2]);
    faces[2] = new Face(this, vertices[5], vertices[4], vertices[7], vertices[6]);
    faces[3] = new Face(this, vertices[4], vertices[0], vertices[3], vertices[7]);
    faces[4] = new Face(this, vertices[3], vertices[2], vertices[6], vertices[7]);
    faces[5] = new Face(this, vertices[4], vertices[5], vertices[1], vertices[0]);

    for (int i = 0; i < 6; ++i) {
      faces[i].calcNormal();

      addFace(faces[i]);

      faces[i].setTextureRange(0.0f, 0.0f, 1.0f, 1.0f, false);
    }
  }

  void addBodyRev(double [] x, double [] y, int num_xy, int num_patches) {
    double [] c = new double [num_patches];
    double [] s = new double [num_patches];

    double theta           = 0.0;
    double theta_increment = 2.0*Math.PI/num_patches;

    for (int i = 0; i < num_patches; ++i) {
      c[i] = Math.cos(theta);
      s[i] = Math.sin(theta);

      theta += theta_increment;
    }

    double tdx = 1.0/((double) num_patches);
    double tdy = 1.0/((double) num_xy - 1);

    //---

    int num_vertices = 0;

    int [] index1 = new int [num_patches + 1];
    int [] index2 = new int [num_patches + 1];

    int [] pindex1 = index1;
    int [] pindex2 = index2;

    if (Math.abs(x[0]) < 1E-6) {
      Vertex p = new Vertex(0.0f, (float) y[0], 0.0f);

      addVertex(p);

      for (int i = 0; i <= num_patches; ++i)
        pindex1[i] = num_vertices;

      ++num_vertices;
    }
    else {
      for (int i = 0; i < num_patches; ++i) {
        Vertex p = new Vertex((float) (x[0]*c[i]), (float) y[0], (float) (-x[0]*s[i]));

        addVertex(p);

        pindex1[i] = num_vertices;

        ++num_vertices;
      }

      pindex1[num_patches] = pindex1[0];
    }

    //---

    double ty1 = 1.0;

    for (int j = 1; j < num_xy; ++j) {
      double ty2 = ty1 - tdy;

      if (Math.abs(x[j]) < 1E-6) {
        Vertex p = new Vertex(0.0f,  (float) y[j], 0.0f);

        addVertex(p);

        for (int i = 0; i <= num_patches; ++i)
          pindex2[i] = num_vertices;

        ++num_vertices;
      }
      else {
        for (int i = 0; i < num_patches; ++i) {
          Vertex p = new Vertex((float) (x[j]*c[i]), (float) y[j], (float) (-x[j]*s[i]));

          addVertex(p);

          pindex2[i] = num_vertices;

          ++num_vertices;
        }

        pindex2[num_patches] = pindex2[0];
      }

      //---

      if (pindex1[0] != pindex1[1]) {
        if (pindex2[0] == pindex2[1]) {
          // bottom (pindex1 is upper circle, pindex2 is single point)
          double tx1 = 0.0;

          for (int i = 0; i < num_patches; ++i) {
            double tx2 = tx1 + tdx;

            Vector<Integer> vertices = new Vector<Integer>();

            vertices.add(pindex1[i    ]);
            vertices.add(pindex1[i + 1]);
            vertices.add(pindex2[i    ]);

            Face face = new Face(this, vertices);

            face.calcNormal();

            face.setTextureRange((float) tx1, (float) ty1, (float) tx2, (float) ty2, true);

            addFace(face);

            tx1 = tx2;
          }
        }
        else {
          // middle (pindex1 is upper circle, pindex2 is lower circle)
          double tx1 = 0.0;

          for (int i = 0; i < num_patches; ++i) {
            double tx2 = tx1 + tdx;

            Vector<Integer> vertices = new Vector<Integer>();

            vertices.add(pindex2[i    ]);
            vertices.add(pindex2[i + 1]);
            vertices.add(pindex1[i + 1]);
            vertices.add(pindex1[i    ]);

            Face face = new Face(this, vertices);

            face.calcNormal();

            face.setTextureRange((float) tx1, (float) ty1, (float) tx2, (float) ty2, false);

            addFace(face);

            tx1 = tx2;
          }
        }
      }
      else {
        // top (pindex1 is single point, pindex2 is lower circle)
        double tx1 = 0.0;

        if (pindex2[0] != pindex2[1]) {
          for (int i = 0; i < num_patches; ++i) {
            double tx2 = tx1 + tdx;

            Vector<Integer> vertices = new Vector<Integer>();

            vertices.add(pindex2[i    ]);
            vertices.add(pindex2[i + 1]);
            vertices.add(pindex1[i    ]);

            Face face = new Face(this, vertices);

            face.setTextureRange((float) tx1, (float) ty1, (float) tx2, (float) ty2, true);

            face.calcNormal();

            addFace(face);

            tx1 = tx2;
          }
        }
      }

      int [] pindex = pindex2;

      pindex2 = pindex1;
      pindex1 = pindex;

      ty1 = ty2;
    }
  }

  void draw() {
    for (Face face : faces_)
      drawFace(face);

    for (Line line : lines_)
      drawLine(line);
  }

  void drawFace(Face face) {
    scene_.startFace(face.getMaterial());

    for (int i = 0; i < face.getNumVertices(); ++i) {
      Vertex  v = face.getVertex(i);
      Point2D t = face.getTexturePoint(i);

      Matrix3D.multiply(tv_.v(), view_, v.v());

      if (v.hasNormal())
        scene_.addFaceVertex(tv_.v(), t, v.getNormal());
      else
        scene_.addFaceVertex(tv_.v(), t, face.getNormal());
    }

    scene_.endFace();

    face.drawSubFaces();

    face.drawSubLines();
  }

  void drawSubFace(Face face) {
    scene_.startSubFace();

    drawFace(face);

    scene_.endSubFace();
  }

  void drawLine(Line line) {
    scene_.startLine(line.getColor());

    Vertex v1 = line.getVertex(0);
    Vertex v2 = line.getVertex(1);

    Matrix3D.multiply(tv_.v(), view_, v1.v());

    scene_.addLineVertex(tv_.v());

    Matrix3D.multiply(tv_.v(), view_, v2.v());

    scene_.addLineVertex(tv_.v());

    scene_.endLine();
  }

  void drawSubLine(Line line) {
    drawLine(line);
  }

  void updateView() {
    view_ = Matrix3D.multiply(translate_, Matrix3D.multiply(scale_, rotate_));
  }

  private Scene          scene_;
  private String         name_;
  private Vector<Vertex> vertices_;
  private Vector<Face>   faces_;
  private Vector<Line>   lines_;
  private int            texture_;
  private BBox           bbox_;
  private Vertex         tv_;
  private Matrix3D       translate_;
  private Matrix3D       scale_;
  private Matrix3D       rotate_;
  private Matrix3D       view_;
}
