#version 120

uniform vec2 rectSize;
uniform vec4 topColor;
uniform vec4 bottomColor;
uniform float radius;
uniform float yLimit;

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    float gradientFactor = gl_TexCoord[0].t;
    vec4 mixedColor = mix(topColor, bottomColor, gradientFactor);
    vec2 rectHalf = rectSize * .5;
    float currentRadius = radius;
    vec2 pixelPos = gl_TexCoord[0].st * rectSize;
    if (pixelPos.y > yLimit) {
        currentRadius = 0.0;
    }
    float smoothedAlpha = (1.0 - smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - currentRadius - 1., currentRadius))) * mixedColor.a;
    gl_FragColor = vec4(mixedColor.rgb, smoothedAlpha);
}