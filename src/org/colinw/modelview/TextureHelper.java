package org.colinw.modelview;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class TextureHelper {
  private static final String TAG = "TextureHelper";

  /**
   * Loads a texture from a resource ID, returning the OpenGL ID for that
   * texture. Returns 0 if the load failed.
   *
   * @param context
   * @param resourceId
   * @return
   */
  public static int loadTexture(Context context, int resourceId, boolean wrap) {
    final BitmapFactory.Options options = new BitmapFactory.Options();

    options.inScaled = false;

    // Read in the resource
    final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

    return bitmapTexture(bitmap, wrap);
  }

  public static int loadImageTexture(Context context, String filename, boolean wrap) {
    AssetManager a = context.getAssets();

    // Create an input stream to read from the asset folder
    InputStream is = null;

    try {
      is = a.open(filename);
    }
    catch (IOException e) {
      return 0;
    }

    Bitmap bitmap = BitmapFactory.decodeStream(is);

    if (is != null) {
      try {
        is.close();
      } catch (IOException e) {
      }
    }

    return bitmapTexture(bitmap, wrap);
  }

  public static int bitmapTexture(Bitmap bitmap, boolean wrap) {
    final int [] textureObjectIds = new int [1];

    glGenTextures(1, textureObjectIds, 0);

    if (textureObjectIds[0] == 0) {
      Log.w(TAG, "Could not generate a new OpenGL texture object.");
      return 0;
    }

    if (bitmap == null) {
      Log.w(TAG, "Bitmap could not be decoded.");
      glDeleteTextures(1, textureObjectIds, 0);
      return 0;
    }

    // Bind to the texture in OpenGL
    glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

    // Set filtering: a default must be set, or the texture will be black.
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    if (wrap) {
      glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }

    // Load the bitmap into the bound texture.
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

    // Note: Following code may cause an error to be reported in the
    // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
    // Failed to generate texture mipmap levels (error=3)
    // No OpenGL error will be encountered (glGetError() will return
    // 0). If this happens, just squash the source image to be
    // square. It will look the same because of texture coordinates,
    // and mipmap generation will work.

    glGenerateMipmap(GL_TEXTURE_2D);

    // Recycle the bitmap, since its data has been loaded into OpenGL.
    bitmap.recycle();

    // Unbind from the texture.
    glBindTexture(GL_TEXTURE_2D, 0);

    return textureObjectIds[0];
  }
}
