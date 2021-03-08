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

uniform int renderLight;

void main() {
    //texel0 - scene
    //texel1 - light
    vec4 texel0 = texture2D(u_texture0, v_texCoords);
    vec4 texel1 = texture2D(u_texture1, v_texCoords);

    texel1 *= 1.0;
    texel1.a=1.0;

    vec4 light = vec4(1.0-texel1.r,1.0 -texel1.g,1.0 -texel1.b,texel1.a);

    vec4 blackOut = vec4(0,0,0,texel0.a);
    //colour is hardcoded in but should be passed in from ambient light colour
    vec4 whiteOut = vec4(1,0.9,0.95,texel0.a);

    //if (renderLight > 0.5) gl_FragColor = vec4(texel1 - blackOut);
    if (renderLight > 0.5) gl_FragColor = texel0;
    else gl_FragColor = blackOut;

}
