precision mediump float;

uniform float u_Lighted;
uniform float u_Textured;

uniform sampler2D u_TextureUnit;
uniform vec4      u_Color;
varying vec2      v_TextureCoordinates;

void main()
{
  if (u_Textured > 0.5) {
    //vec4 tex = texture2D(u_TextureUnit, v_TextureCoordinates);
    vec4 tex = vec4(1.0, 0.0, 0.0, 1.0);

    gl_FragColor = vec4(tex.r*u_Color.r, tex.g*u_Color.g, tex.b*u_Color.b, tex.a*u_Color.a);
  }
  else
    gl_FragColor = u_Color;
}
