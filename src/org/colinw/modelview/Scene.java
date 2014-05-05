package org.colinw.modelview;

import java.util.Vector;

import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_LINES;

class Scene {
  private static final int MAX_VERTEX_DATA = 65536;

  Scene(ModelViewRenderer renderer) {
    renderer_ = renderer;

    light_ = new Light();

    objects_    = new Vector<Object>();
    primitives_ = new Vector<Object>();

    vertexData_  = new float [MAX_VERTEX_DATA]; // calc !!!
    vertexArray_ = new VertexArray(vertexData_);

    program_ = renderer_.getSceneShaderProgram();
    texture_ = renderer.getTexture("bw_gradient");

    faceTexture_ = 0;

    bbox_ = new BBox();

    xm_ = 0.0f;
    ym_ = 0.0f;
    zm_ = 0.0f;
    s_  = 1.0f;
  }

  void addObject(Object object) {
    objects_.add(object);
  }

  void addPrimitive(Object primitive) {
    primitives_.add(primitive);
  }

  BBox getBBox() {
    if (! bbox_.isSet()) {
      for (Object obj : objects_) {
        bbox_.addBBox(obj.getBBox());
      }
    }

    return bbox_;
  }

  void move(float dx, float dy, float dz) {
    for (Object obj : objects_)
      obj.move(dx, dy, dz);
  }

  void rotate(float xa, float ya, float za) {
    for (Object obj : objects_)
      obj.rotate(xa, ya, za);
  }

  void scale(float s) {
    for (Object obj : objects_)
      obj.scale(s);
  }

  void resetView() {
    for (Object obj : objects_)
      obj.resetView();
  }

  void draw() {
    faceTexture_ = texture_;

    BBox bbox = getBBox();

    xm_ = (float) bbox.xMid();
    ym_ = (float) bbox.yMid();
    zm_ = (float) bbox.zMid();

    s_ = (float) Math.max(Math.max(bbox.xSize(), bbox.ySize()), bbox.zSize());

    program_.useProgram(renderer_.isLighted(), renderer_.isTextured());
    program_.setViewMatrix(renderer_.viewMatrix3D());

    if (renderer_.isTextured())
      program_.setTexture(faceTexture_);

    program_.setLight(light_);

    for (Object obj : objects_) {
      if (renderer_.isTextured()) {
        int faceTexture = obj.getTexture();

        if (faceTexture == 0)
          faceTexture = texture_;

        //if (faceTexture != faceTexture_) {
          faceTexture_ = faceTexture;

          program_.setTexture(faceTexture_);
        //}
      }

      obj.draw();
    }
  }

  void startFace(Material material) {
    program_.setMaterial(material);

    pos_ = 0;
  }

  void addFaceVertex(Vector3D v, Point2D t, Vector3D normal) {
    vertexData_[pos_++] = ((v.x() - xm_)/s_);
    vertexData_[pos_++] = ((v.y() - ym_)/s_);
    vertexData_[pos_++] = ((v.z() - zm_)/s_) - 2.0f;

    if (renderer_.isLighted()) {
      vertexData_[pos_++] = normal.x();
      vertexData_[pos_++] = normal.y();
      vertexData_[pos_++] = normal.z();
    }

    if (renderer_.isTextured()) {
      vertexData_[pos_++] = t.x();
      vertexData_[pos_++] = t.y();
    }
  }

  void endFace() {
    int numData = 3;

    if (renderer_.isLighted ()) numData += 3;
    if (renderer_.isTextured()) numData += 2;

    int stride = numData*Constants.BYTES_PER_FLOAT;

    int numVertices = pos_ / numData;

    vertexArray_.updateBuffer(vertexData_, 0, pos_);

    int pi = 0;

    vertexArray_.setVertexAttribPointer(pi, program_.getPositionAttrLoc(), 3, stride);

    pi += 3;

    if (renderer_.isLighted()) {
      vertexArray_.setVertexAttribPointer(pi, program_.getNormalAttrLoc(), 3, stride);

      pi += 3;
    }

    if (renderer_.isTextured()) {
      vertexArray_.setVertexAttribPointer(pi, program_.getTextureCoordinatesAttrLoc(), 2, stride);

      pi += 2;
    }

    if (renderer_.isLineMode()) {
      glDrawArrays(GL_LINE_LOOP, 0, numVertices);
    }
    else {
      if      (numVertices == 3)
        glDrawArrays(GL_TRIANGLES, 0, numVertices);
      else if (numVertices == 4)
        glDrawArrays(GL_TRIANGLE_FAN, 0, numVertices);
    }
  }

  void startLine(RGBA c) {
    program_.setColor(c);

    pos_ = 0;
  }

  void startSubFace() {
    android.opengl.GLES20.glDepthFunc(android.opengl.GLES20.GL_LEQUAL);

    android.opengl.GLES20.glEnable(android.opengl.GLES20.GL_POLYGON_OFFSET_FILL);
    android.opengl.GLES20.glPolygonOffset(-1.0f, -2.0f);
  }

  void endSubFace() {
    android.opengl.GLES20.glDisable(android.opengl.GLES20.GL_POLYGON_OFFSET_FILL);

    android.opengl.GLES20.glDepthFunc(android.opengl.GLES20.GL_LESS);
  }

  void addLineVertex(Vector3D v) {
    int numData = (renderer_.isLighted() ? 6 : 3);

    if (pos_ + numData > MAX_VERTEX_DATA)
      return;

    vertexData_[pos_++] = ((v.x() - xm_)/s_);
    vertexData_[pos_++] = ((v.y() - ym_)/s_);
    vertexData_[pos_++] = ((v.z() - zm_)/s_) - 2.0f;

    if (renderer_.isLighted()) {
      vertexData_[pos_++] = 0.0f;
      vertexData_[pos_++] = 0.0f;
      vertexData_[pos_++] = 0.0f;
    }
  }

  void endLine() {
    int numData = (renderer_.isLighted() ? 6 : 3);

    int stride = numData*Constants.BYTES_PER_FLOAT;

    int numVertices = pos_ / numData;

    vertexArray_.updateBuffer(vertexData_, 0, pos_);

    vertexArray_.setVertexAttribPointer(0, program_.getPositionAttrLoc(), 3, stride);

    if (renderer_.isLighted()) {
      vertexArray_.setVertexAttribPointer(3, program_.getNormalAttrLoc(), 3, stride);
    }

    glDrawArrays(GL_LINES, 0, numVertices);
  }

  Object getObject(String name) {
    for (Object o : objects_)
      if (o.getName().equals(name))
         return o;

    return null;
  }

  Object getPrimitive(String name) {
    for (Object o : primitives_)
      if (o.getName().equals(name))
        return o;

    return null;
  }

  private ModelViewRenderer  renderer_;
  private Light              light_;
  private Vector<Object>     objects_;
  private Vector<Object>     primitives_;
  private int                pos_;
  private float []           vertexData_;
  private VertexArray        vertexArray_;
  private SceneShaderProgram program_;
  private BBox               bbox_;
  private float              xm_, ym_, zm_;
  private float              s_;
  private int                texture_;
  private int                faceTexture_;
}
