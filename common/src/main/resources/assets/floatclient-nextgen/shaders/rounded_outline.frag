#version 120

uniform vec2 rectSize;
uniform vec4 color;
uniform float radius;
uniform float thickness;

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    vec2 rectHalf = rectSize * .5;
    vec2 pixelPos = gl_TexCoord[0].st * rectSize;
    float dist = roundSDF(rectHalf - pixelPos, rectHalf - radius - 1., radius);
    float outerAlpha = 1.0 - smoothstep(0.0, 1.0, dist);
    float innerAlpha = 1.0 - smoothstep(0.0, 1.0, dist + thickness);
    float strokeAlpha = (outerAlpha - innerAlpha) * color.a;
    if (strokeAlpha <= 0.0) {
        discard;
    }
    gl_FragColor = vec4(color.rgb, strokeAlpha);
}