#ifdef GL_ES
precision mediump float;
#endif

/* this make the difference :) */

uniform sampler2D u_texture;

varying vec2 v_texCoords;

uniform float exposure;
uniform float decay;
uniform float density;
uniform float weight;
uniform vec2 lightPositionOnScreen;
const int NUM_SAMPLES = 100 ;

void main()
{
    float illuminationDecay =  1.0;

    vec2 deltaTextCoord = vec2(v_texCoords - lightPositionOnScreen.xy);
    vec2 textCoo = v_texCoords;
    deltaTextCoord *= 1.0 /  float(NUM_SAMPLES) * density;

    vec4 result = vec4(0.0, 0.0, 0.0, 1.0);
    for(int i=0; i < NUM_SAMPLES; i++) {
        textCoo -= deltaTextCoord;
        vec4 sm = texture2D(u_texture, textCoo);

        sm *= illuminationDecay * weight;

        result += sm;

        illuminationDecay *= decay;
    }

gl_FragColor = (result * exposure);
}