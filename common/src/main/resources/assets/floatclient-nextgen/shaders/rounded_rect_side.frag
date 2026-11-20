#version 120

uniform vec2 rectSize;
uniform vec4 color;
uniform float radius;
uniform float thickness;

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, vec2(0.0))) - r;
}

void main() {
    vec2 rectHalf = rectSize * 0.5;
    vec2 p = rectHalf - (gl_TexCoord[0].xy * rectSize);
    vec2 b = rectHalf - vec2(radius + 1.0);
    float dist = roundSDF(p, b, radius);
    float distanceToCut = p.x - b.x;
    if (distanceToCut <= 0.0) {
        discard;
    }
    float taper = clamp(distanceToCut / radius, 0.0, 1.0);
    float currentThickness = thickness * taper;
    float halfThickness = currentThickness * 0.5;
    float strokeDist = abs(dist) - halfThickness;
    float strokeAlpha = 1.0 - smoothstep(-0.5, 0.5, strokeDist);
    float leftMask = smoothstep(-0.5, 0.5, p.x - b.x);
    float finalAlpha = strokeAlpha * leftMask * color.a;
    if (finalAlpha <= 0.0) {
        discard;
    }
    gl_FragColor = vec4(color.rgb, finalAlpha);
}