uniform mat4 u_ViewMatrix;

attribute vec3 a_Position;
attribute vec2 a_TextureCoordinates;
varying vec2   v_TextureCoordinates;

void main()
{
  v_TextureCoordinates = a_TextureCoordinates;            

  gl_Position = u_ViewMatrix*vec4(a_Position, 1.0);
}
