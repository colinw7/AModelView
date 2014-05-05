precision mediump float;

uniform float u_Lighted;
uniform float u_Textured;

uniform mat4 u_ViewMatrix;

uniform vec3 u_LightDirection;
uniform vec4 u_LightAmbient;
uniform vec4 u_LightDiffuse;

uniform vec4 u_MaterialAmbient;
uniform vec4 u_MaterialDiffuse;

attribute vec3 a_Position;
attribute vec2 a_TextureCoordinates;
attribute vec3 a_Normal;

varying vec2 v_TextureCoordinates;
varying vec4 v_LightColor;

void main()                    
{ 
  v_TextureCoordinates = a_TextureCoordinates;	  	  

  if (u_Lighted > 0.5) {
    vec4 mcPosition = vec4(a_Position, 1.0);
    vec3 mcNormal   = a_Normal;

    //vec3 eyeNormal = vec3(u_ViewMatrix*vec4(mcNormal, 0.0));
    //eyeNormal = eyeNormal / length(eyeNormal);
    vec3 eyeNormal = mcNormal;

    float eyeNormalDot = max(0.0, dot(eyeNormal, u_LightDirection));

    // Ambient light
    vec4 ambient = u_LightAmbient*u_MaterialAmbient;

    // Diffuse light
    vec4 diffuse = eyeNormalDot*u_LightDiffuse*u_MaterialDiffuse;

    v_LightColor = ambient + diffuse;

    gl_Position = u_ViewMatrix*mcPosition;
  //gl_Position = vec4(a_Position, 1.0);
  }          
  else {
    gl_Position = u_ViewMatrix*vec4(a_Position, 1.0);
  }
}
