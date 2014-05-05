precision mediump float;

uniform float u_Lighted;
uniform float u_Textured;

uniform sampler2D u_TextureUnit;
uniform vec4      u_Color;

varying vec2 v_TextureCoordinates;
varying vec4 v_LightColor;

void main()
{
  vec4 tex;

  if (u_Textured > 0.5) {
    tex = texture2D(u_TextureUnit, v_TextureCoordinates);
  }

  if (u_Lighted > 0.5) {
    if (u_Textured > 0.5)
      gl_FragColor = vec4(tex.r*v_LightColor.r, tex.g*v_LightColor.g, tex.b*v_LightColor.b, tex.a);
    else
      gl_FragColor = v_LightColor;
  }
  else {
    if (u_Textured > 0.5)
      gl_FragColor = vec4(tex.r*u_Color.r, tex.g*u_Color.g, tex.b*u_Color.b, tex.a*u_Color.a);
    else
      gl_FragColor = u_Color;
  }
}
