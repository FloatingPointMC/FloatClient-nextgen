#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

layout(std140) uniform Parameters {
    vec2 rectSize;
    uint color;
    float radius;
    float thickness;
};

vec4 rgba(uint argb) {
    return vec4((argb >> 16u) & 255u, (argb >> 8u) & 255u,
            argb & 255u, argb >> 24u) / 255.0;
}

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, vec2(0.0))) - r;
}

void main() {
    vec2 rectHalf = rectSize * 0.5;
    vec2 p = rectHalf - (texCoord * rectSize);
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
    float finalAlpha = strokeAlpha * leftMask * rgba(color).a;
    if (finalAlpha <= 0.0) {
        discard;
    }
    fragColor = vec4(rgba(color).rgb, finalAlpha);
}