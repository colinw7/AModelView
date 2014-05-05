uniform mat4 u_ViewMatrix;

attribute vec3 a_Position;

void main()                    
{                                	  	  
  gl_Position = u_ViewMatrix*vec4(a_Position.x, a_Position.y, 0.0, 1.0);
}   
