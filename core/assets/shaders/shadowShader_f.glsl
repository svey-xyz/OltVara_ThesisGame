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
uniform sampler2D u_texture2;
uniform vec4 ambient_color;
uniform vec4 shadow_color;
uniform int mulLights;
uniform float intensity;

void main() {
    //texel0 - lightmap
    //texel1 - texture
    vec4 texel0 = texture2D(u_texture0, v_texCoords);
    vec4 texel1 = texture2D(u_texture1, v_texCoords);
    vec4 texel2 = texture2D(u_texture2, v_texCoords);
    vec4 final;

    //texel1 += shadow_color;
    //texel1 += (1.0 - texel1 * ambient_color);

    texel2 *= intensity;

    if (mulLights == 0) {
        final = vec4(texel0.rgb * texel1.rgb, texel0.a);
    } else {
        final = vec4(texel0.rgb * (texel1.rgb + texel2.rgb), texel0.a);
    }

    gl_FragColor = final;
}
