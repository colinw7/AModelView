package org.colinw.modelview;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexArray {
  public VertexArray(float[] vertexData) {
    floatBuffer_ = ByteBuffer.allocateDirect(vertexData.length*Constants.BYTES_PER_FLOAT).
                     order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexData);
  }

  public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                     int componentCount, int stride) {
    floatBuffer_.position(dataOffset);
    glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer_);
    glEnableVertexAttribArray(attributeLocation);
    floatBuffer_.position(0);
  }

  public void updateBuffer(float[] vertexData, int start, int count) {
    floatBuffer_.position(start);
    floatBuffer_.put(vertexData, start, count);
    floatBuffer_.position(0);
  }

  private final FloatBuffer floatBuffer_;
}
