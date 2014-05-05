package org.colinw.modelview;

import java.util.HashMap;
import java.util.Random;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glDisable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class ModelViewRenderer implements Renderer {
  public ModelViewRenderer(ModelViewActivity activity) {
    activity_ = activity;

    modelName_ = "";
    import_    = null;
    slider_    = null;

    sceneShaderProgram_ = null;

    textureMap_ = new HashMap<String,Integer>();

    projectionMatrix_     = new Matrix3D();
    viewMatrix2D_         = new Matrix3D();
    viewMatrix3D_         = new Matrix3D();
    viewProjectionMatrix_ = new Matrix3D();

    lineMode_  = false;
    depthTest_ = false;
    cullFace_  = false;
    lighted_   = false;
    textured_  = false;
    move_      = false;
  }

  public Context context() { return activity_; }

  public float [] viewMatrix2D() { return viewMatrix2D_        .getData(); }
  public float [] viewMatrix3D() { return viewProjectionMatrix_.getData(); }

  public float xmin() { return xmin_; }
  public float ymin() { return ymin_; }
  public float xmax() { return xmax_; }
  public float ymax() { return ymax_; }

  public float width () { return xmax_ - xmin_; }
  public float height() { return ymax_ - ymin_; }

  public float mapNormalized(float v, float min, float max) {
    return v*(max - min) + min;
  }

  public void handleTouchPress(float normalizedX, float normalizedY) {
    pressX_ = mapNormalized(normalizedX, xmin_, xmax_);
    pressY_ = mapNormalized(normalizedY, ymin_, ymax_);

    insideSlider_ = slider_.isInside(pressX_, pressY_);

    if (insideSlider_)
      slider_.handlePress(pressX_, pressY_);
  }

  public void handleTouchDrag(float normalizedX, float normalizedY) {
    float dragX = mapNormalized(normalizedX, xmin(), xmax());
    float dragY = mapNormalized(normalizedY, ymin(), ymax());

    float deltaX = dragX - pressX_;
    float deltaY = dragY - pressY_;

    if (insideSlider_) {
      slider_.handleDrag(dragX, dragY);
    }
    else {
      if (! isMove())
        dragRotate(deltaX, deltaY);
      else
        dragMove(deltaX, deltaY);
    }

    pressX_ = dragX;
    pressY_ = dragY;
  }

  public void dragMove(float dx, float dy) {
    if (scene() != null)
      scene().move(10.0f*dx, 10.0f*dy, 0.0f);
  }

  public void dragRotate(float dx, float dy) {
    if (scene() != null)
      scene().rotate(50.0f*dy, 50.0f*dx, 0.0f);
  }

  public void dragScale(float dx, float dy) {
    float s;

    if (Math.abs(dx) > Math.abs(dy))
      s = 1.0f + dx;
    else
      s = 1.0f + dy;

    if (scene() != null)
      scene().scale(s);
  }

  @Override
  public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    getSceneShaderProgram();

    slider_ = new Slider(this);

    addTexture("bw_gradient" , R.drawable.bw_gradient, false);
    addTexture("planet03.jpg", R.drawable.planet03   , false);
    addTexture("floor1.jpg"  , R.drawable.floor1     , false);

    //loadModel("F15.V3D");
  }

  @Override
  public void onSurfaceChanged(GL10 glUnused, int width, int height) {
    glViewport(0, 0, width, height);

    // set 2D view matrix
    final float aspectRatio = width > height ?
      (float) width / (float) height : (float) height / (float) width;

    if (width > height) {
      // Landscape
      viewMatrix2D_ = Matrix3D.newOrtho(-aspectRatio, aspectRatio, -1f, 1f);

      xmin_ = -aspectRatio; ymin_ = -1.0f;
      xmax_ =  aspectRatio; ymax_ =  1.0f;
    }
    else {
      // Portrait or square
      viewMatrix2D_ = Matrix3D.newOrtho(-1f, 1f, -aspectRatio, aspectRatio);

      xmin_ = -1.0f; ymin_ = -aspectRatio;
      xmax_ =  1.0f; ymax_ =  aspectRatio;
    }

    // set 3D projection matrix
    projectionMatrix_ = Matrix3D.newPerspective(45.0f, (1.0f*width)/height, 1.0f, 10.0f);

    //viewMatrix3D_ = Matrix3D.newLookAt(0, 0, -10, 0, 0, 0);
  }

  @Override
  public void onDrawFrame(GL10 glUnused) {
    // Clear the rendering surface.
    if (isDepthTest())
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    else
      glClear(GL_COLOR_BUFFER_BIT);

    if (isDepthTest())
      glEnable(GL_DEPTH_TEST);
    else
      glDisable(GL_DEPTH_TEST);

    if (isCullFace())
      glEnable(GL_CULL_FACE);
    else
      glDisable(GL_CULL_FACE);

    //glEnable(GL_TEXTURE_2D);

    //glEnable(GL_BLEND);
    //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    viewMatrix3D_.reset();

    Matrix3D.multiply(viewProjectionMatrix_, projectionMatrix_, viewMatrix3D_);

    if (scene() != null)
      scene().draw();

    slider_.draw();
  }

  public void addTexture(String name, int resourceId, boolean wrap) {
    int texture = TextureHelper.loadTexture(context(), resourceId, wrap);

    textureMap_.put(name, texture);
  }

  public void addImageTexture(String name, boolean wrap) {
    int texture = TextureHelper.loadImageTexture(context(), name, wrap);

    textureMap_.put(name, texture);
  }

  public int getTexture(String name) {
    if (! textureMap_.containsKey(name))
      return -1;

    return textureMap_.get(name);
  }

  public Scene scene() {
    if (import_ != null)
      return import_.scene();
    else
      return null;
  }

  public void resetView() {
    if (scene() != null)
      scene().resetView();
  }

  public void setModelName(String name) {
    modelName_ = name;

    updateModel();
  }

  public void updateModel() {
    if (import_ != null && import_.modelName().equals(modelName_))
      return;

    activity_.stopRender();

    loadModel(modelName_);

    activity_.startRender();
  }

  public void loadModel(String name) {
    if (name.endsWith(".scene")) {
      ImportScene imp = new ImportScene(this);

      if (imp.read(name)) {
        import_ = imp;
      }
    }
    else if (name.endsWith(".V3D")) {
      ImportV3D imp = new ImportV3D(this);

      if (imp.read(name)) {
        import_ = imp;
      }
    }
    else if (name.endsWith(".3ds")) {
      Import3DS imp = new Import3DS(this);

      if (imp.read(name)) {
        import_ = imp;
      }
    }
    else if (name.endsWith(".X3D")) {
      ImportX3D imp = new ImportX3D(this);
    
      if (imp.read(name)) {
        import_ = imp;
      }
    }
    else if (name.endsWith(".obj")) {
      ImportObj imp = new ImportObj(this);

      if (imp.read(name)) {
        import_ = imp; 
      }
    }
  }

  SceneShaderProgram getSceneShaderProgram() {
    if (sceneShaderProgram_ == null)
      sceneShaderProgram_ = new SceneShaderProgram(context());

    return sceneShaderProgram_;
  }

  public boolean isLineMode() { return lineMode_; }
  public void setLineMode(boolean b) { lineMode_ = b; }

  public boolean isDepthTest() { return depthTest_; }
  public void setDepthTest(boolean b) { depthTest_ = b; }

  public boolean isCullFace() { return cullFace_; }
  public void setCullFace(boolean b) { cullFace_ = b; }

  public boolean isLighted() { return lighted_; }
  public void setLighted(boolean b) { lighted_ = b; }

  public boolean isTextured() { return textured_; }
  public void setTextured(boolean b) { textured_ = b; }

  public boolean isMove() { return move_; }
  public void setMove(boolean b) { move_ = b; }

  private ModelViewActivity       activity_;
  private String                  modelName_;
  private ImportModel             import_;
  private Slider                  slider_;
  private SceneShaderProgram      sceneShaderProgram_;
  private HashMap<String,Integer> textureMap_;
  private float                   pressX_;
  private float                   pressY_;
  private float                   xmin_, ymin_, xmax_, ymax_;
  private Matrix3D                viewMatrix2D_;
  private Matrix3D                projectionMatrix_;
  private Matrix3D                viewMatrix3D_;
  private Matrix3D                viewProjectionMatrix_;
  private boolean                 insideSlider_;
  private boolean                 lineMode_;
  private boolean                 depthTest_;
  private boolean                 cullFace_;
  private boolean                 lighted_;
  private boolean                 textured_;
  private boolean                 move_;
}
