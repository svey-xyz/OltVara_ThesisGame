#version 120

#ifdef GL_ES
precision lowp float;
#define MED mediump
#else
#define MED
#endif

varying MED vec2 v_texCoords;
uniform sampler2D u_texture0;
uniform sampler2D u_texture1;
uniform vec4 ambient_color;

void main() {
    vec4 texel0 = texture2D(u_texture0, v_texCoords);
    vec4 texel1 = texture2D(u_texture1, v_texCoords);

    vec4 final = vec4(texel0.rgb * texel1.rgb, texel1.a);

    gl_FragColor = final;
}
