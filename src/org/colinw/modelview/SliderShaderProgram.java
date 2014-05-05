package org.colinw.modelview;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glUniformMatrix4fv;

import android.content.Context;

public class SliderShaderProgram {
  // Uniform constants
  protected static final String U_VIEW_MATRIX = "u_ViewMatrix";
  protected static final String U_COLOR       = "u_Color";

  // Attribute constants
  protected static final String A_POSITION = "a_Position";

  public SliderShaderProgram(Context context) {
    program_ = ShaderHelper.buildProgram(
      TextResourceReader.readTextFileFromResource(context, R.raw.slider_vertex_shader  ),
      TextResourceReader.readTextFileFromResource(context, R.raw.slider_fragment_shader));

    // Retrieve uniform locations for the shader program.
    uViewMatrixLocation_ = glGetUniformLocation(program_, U_VIEW_MATRIX);
    uColorLocation_      = glGetUniformLocation(program_, U_COLOR);

    // Retrieve attribute locations for the shader program.
    aPositionLocation_ = glGetAttribLocation(program_, A_POSITION);
  }

  public void useProgram() {
    // Set the current OpenGL shader program to this program.
    glUseProgram(program_);
  }

  public void setViewMatrix(float [] matrix) {
    glUniformMatrix4fv(uViewMatrixLocation_, 1, false, matrix, 0);
  }

  public void setColor(RGBA color) {
    glUniform4f(uColorLocation_, color.r, color.g, color.b, color.a);
  }

  public int getPositionAttributeLocation() { return aPositionLocation_; }

  //---

  private int program_;

  // Uniform locations
  private final int uViewMatrixLocation_;
  private final int uColorLocation_;

  // Attribute locations
  private final int aPositionLocation_;
}
