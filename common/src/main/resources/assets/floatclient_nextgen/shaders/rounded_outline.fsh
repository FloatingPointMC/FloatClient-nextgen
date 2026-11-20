#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

#include <minecraft:dynamictransforms.glsl>

layout(std140) uniform Parameters {
    float radiusFraction;
    float thicknessFraction;
};

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    vec2 pixelSize = 1.0 / max(fwidth(texCoord), vec2(1e-5));
    float minSize = min(pixelSize.x, pixelSize.y);
    float radius = clamp(radiusFraction, 0.0, 0.5) * minSize;
    float thickness = clamp(thicknessFraction, 0.0, 1.0) * minSize;
    vec2 rectHalf = pixelSize * 0.5;
    vec2 q = abs((texCoord - 0.5) * pixelSize) - (rectHalf - radius);
    float dist = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
    float aa = max(fwidth(dist), 1e-4);
    float outerAlpha = 1.0 - smoothstep(-aa, aa, dist);
    float innerAlpha = 1.0 - smoothstep(-aa, aa, dist + thickness);
    float strokeAlpha = max(outerAlpha - innerAlpha, 0.0) * ColorModulator.a;
    if (strokeAlpha <= 0.0) {
        discard;
    }
    fragColor = vec4(ColorModulator.rgb, strokeAlpha);
}
