package org.colinw.modelview;

import android.content.Context;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class SceneShaderProgram {
  // Uniform constants
  protected static final String U_VIEW_MATRIX      = "u_ViewMatrix";
  protected static final String U_TEXTURE_UNIT     = "u_TextureUnit";
  protected static final String U_LIGHT_DIRECTION  = "u_LightDirection";
  protected static final String U_LIGHT_AMBIENT    = "u_LightAmbient";
  protected static final String U_LIGHT_DIFFUSE    = "u_LightDiffuse";
  protected static final String U_MATERIAL_AMBIENT = "u_MaterialAmbient";
  protected static final String U_MATERIAL_DIFFUSE = "u_MaterialDiffuse";
  protected static final String U_COLOR            = "u_Color";
  protected static final String U_LIGHTED          = "u_Lighted";
  protected static final String U_TEXTURED         = "u_Textured";

  // Attribute constants
  protected static final String A_POSITION            = "a_Position";
  protected static final String A_NORMAL              = "a_Normal";
  protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

  public SceneShaderProgram(Context context) {
    lighted_  = false;
    textured_ = false;

    program_ = ShaderHelper.buildProgram(
      TextResourceReader.readTextFileFromResource(context, R.raw.scene_vertex_shader  ),
      TextResourceReader.readTextFileFromResource(context, R.raw.scene_fragment_shader));

    // Retrieve uniform locations for the shader program.
    uViewMatrixLoc_      = glGetUniformLocation(program_, U_VIEW_MATRIX);
    uTextureUnitLoc_     = glGetUniformLocation(program_, U_TEXTURE_UNIT);
    uLightDirectionLoc_  = glGetUniformLocation(program_, U_LIGHT_DIRECTION);
    uLightAmbientLoc_    = glGetUniformLocation(program_, U_LIGHT_AMBIENT);
    uLightDiffuseLoc_    = glGetUniformLocation(program_, U_LIGHT_DIFFUSE);
    uColorLoc_           = glGetUniformLocation(program_, U_COLOR);
    uMaterialAmbientLoc_ = glGetUniformLocation(program_, U_MATERIAL_AMBIENT);
    uMaterialDiffuseLoc_ = glGetUniformLocation(program_, U_MATERIAL_DIFFUSE);
    uLightedLoc_         = glGetUniformLocation(program_, U_LIGHTED);
    uTexturedLoc_        = glGetUniformLocation(program_, U_TEXTURED);

    // Retrieve attribute locations for the shader program.
    aPositionLoc_           = glGetAttribLocation(program_, A_POSITION);
    aNormalLoc_             = glGetAttribLocation(program_, A_NORMAL);
    aTextureCoordinatesLoc_ = glGetAttribLocation(program_, A_TEXTURE_COORDINATES);
  }

  public void useProgram(boolean lighted, boolean textured) {
    lighted_  = lighted;
    textured_ = textured;

    // Set the current OpenGL shader program to this program.
    glUseProgram(program_);

    //---

    glUniform1f(uLightedLoc_ , (lighted_  ? 1.0f : 0.0f));
    glUniform1f(uTexturedLoc_, (textured_ ? 1.0f : 0.0f));
  }

  public void setViewMatrix(float [] viewMatrix) {
    glUniformMatrix4fv(uViewMatrixLoc_, 1, false, viewMatrix, 0);
  }

  public void setTexture(int textureId) {
    // Set the active texture unit to texture unit 0.
    glActiveTexture(GL_TEXTURE0);
    // Bind the texture to this unit.
    glBindTexture(GL_TEXTURE_2D, textureId);
    // Tell the texture uniform sampler to use the texture in the shader texture unit 0.
    glUniform1i(uTextureUnitLoc_, 0);
  }

  public void setLight(Light light) {
    Vector3D dir     = light.getDirection();
    RGBA     ambient = light.getAmbient();
    RGBA     diffuse = light.getDiffuse();

    glUniform3f(uLightDirectionLoc_, dir.x(), dir.y(), dir.z());
    glUniform4f(uLightAmbientLoc_, ambient.r, ambient.g, ambient.b, ambient.a);
    glUniform4f(uLightDiffuseLoc_, diffuse.r, diffuse.g, diffuse.b, diffuse.a);
  }

  public void setMaterial(Material material) {
    RGBA diffuse = material.getDiffuse();

    RGBA ambient = material.getAmbient();

    glUniform4f(uMaterialAmbientLoc_, ambient.r, ambient.g, ambient.b, ambient.a);
    glUniform4f(uMaterialDiffuseLoc_, diffuse.r, diffuse.g, diffuse.b, diffuse.a);
    glUniform4f(uColorLoc_          , diffuse.r, diffuse.g, diffuse.b, diffuse.a);
  }

  public void setColor(RGBA c) {
    glUniform4f(uMaterialAmbientLoc_, 0.1f, 0.1f, 0.01f, 1.0f);
    glUniform4f(uMaterialDiffuseLoc_, c.r, c.g, c.b, c.a);
    glUniform4f(uColorLoc_          , c.r, c.g, c.b, c.a);
  }

  public int getPositionAttrLoc() { return aPositionLoc_; }

  public int getNormalAttrLoc() { return aNormalLoc_; }

  public int getTextureCoordinatesAttrLoc() { return aTextureCoordinatesLoc_; }

  private boolean lighted_;
  private boolean textured_;

  // Shader program
  private final int program_;

  // Uniform locations
  private final int uViewMatrixLoc_;
  private final int uTextureUnitLoc_;
  private final int uLightDirectionLoc_;
  private final int uLightAmbientLoc_;
  private final int uLightDiffuseLoc_;
  private final int uMaterialAmbientLoc_;
  private final int uMaterialDiffuseLoc_;
  private final int uColorLoc_;
  private final int uLightedLoc_;
  private final int uTexturedLoc_;

  // Attribute locations
  private final int aPositionLoc_;
  private final int aNormalLoc_;
  private final int aTextureCoordinatesLoc_;
}
