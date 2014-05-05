package org.colinw.modelview;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

public class Slider {
  public Slider(ModelViewRenderer renderer) {
    renderer_ = renderer;

    color_ = new RGBA(0.9f, 0.9f, 0.9f, 1.0f);

    pointData_   = new float [12];
    vertexArray_ = new VertexArray(pointData_);

    program_ = new SliderShaderProgram(renderer_.context());

    sliderPos_ = -999.0f;
  }

  public void draw() {
    float w = 0.02f;
    float h = 1.0f;
    float b = 0.01f;

    program_.useProgram();
    program_.setViewMatrix(renderer_.viewMatrix2D());

    // background
    x1_ = renderer_.xmax() - w - 6*b;
    x2_ = x1_ + w;
    y1_ = renderer_.ymin() + 3*b;
    y2_ = y1_ + h;

    pos_ = 0;

    addQuad(x1_, y1_, x2_, y2_);

    vertexArray_.updateBuffer(pointData_, 0, 12);

    vertexArray_.setVertexAttribPointer(0,
      program_.getPositionAttributeLocation(), 2, 2*Constants.BYTES_PER_FLOAT);

    program_.setColor(color_);

    glDrawArrays(GL_TRIANGLES, 0, 6);

    // bar
    if (sliderPos_ < y1_)
      sliderPos_ = y1_;

    float x12 = (x1_ + x2_)/2.0f - 3.0f*w;
    float x22 = x12 + 6.0f*w;
    float y12 = sliderPos_ - w;
    float y22 = y12 + 2.0f*w;

    pos_ = 0;

    addQuad(x12, y12, x22, y22);

    vertexArray_.updateBuffer(pointData_, 0, 12);

    program_.setColor(color_);

    glDrawArrays(GL_TRIANGLES, 0, 6);
  }

  public void addQuad(float x1, float y1, float x2, float y2) {
    pointData_[pos_++] = x1; pointData_[pos_++] = y1;
    pointData_[pos_++] = x2; pointData_[pos_++] = y1;
    pointData_[pos_++] = x2; pointData_[pos_++] = y2;
    pointData_[pos_++] = x2; pointData_[pos_++] = y2;
    pointData_[pos_++] = x1; pointData_[pos_++] = y2;
    pointData_[pos_++] = x1; pointData_[pos_++] = y1;
  }

  public boolean isInside(float x, float y) {
    float b = 0.05f;

    return (x >= x1_ - b && x <= x2_ + b && y >= y1_ - b && y <= y2_ + b);
  }

  public void handlePress(float x, float y) {
    setSliderPos(y);
  }

  public void setSliderPos(float pos) {
    sliderPos_ = pos;

    if (sliderPos_ < y1_) sliderPos_ = y1_;
    if (sliderPos_ > y2_) sliderPos_ = y2_;
  }

  public void handleDrag(float x, float y) {
    float dy = y - sliderPos_;

    renderer_.dragScale(0.0f, dy);

    setSliderPos(y);
  }

  private ModelViewRenderer   renderer_;
  private float []            pointData_;
  private RGBA                color_;
  private final VertexArray   vertexArray_;
  private SliderShaderProgram program_;
  private int                 pos_;
  private float               x1_, y1_, x2_, y2_;
  private float               sliderPos_;
}
