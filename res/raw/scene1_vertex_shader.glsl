uniform mat4 u_ViewMatrix;

attribute vec3 a_Position;
attribute vec2 a_TextureCoordinates;
varying vec2   v_TextureCoordinates;

void main()                    
{                            
  v_TextureCoordinates = a_TextureCoordinates;	  	  
  gl_Position          = u_ViewMatrix*vec4(a_Position.x, a_Position.y, a_Position.z, 1.0);
  //gl_Position          = vec4(a_Position.x, a_Position.y, a_Position.z, 1.0);
}          
