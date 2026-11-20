#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;

#include <minecraft:dynamictransforms.glsl>

layout(std140) uniform Parameters {
    float radiusFraction;
};

layout(location = 0) out vec4 fragColor;

void main() {
    vec2 pixelSize = 1.0 / max(fwidth(texCoord), vec2(1e-5));

    float radius = clamp(radiusFraction, 0.0, 0.5) * min(pixelSize.x, pixelSize.y);

    vec2 halfSize = pixelSize * 0.5;
    vec2 q = abs((texCoord - 0.5) * pixelSize) - (halfSize - radius);

    float distance = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;

    float aa = fwidth(distance);
    float coverage = 1.0 - smoothstep(-aa * 0.5, aa * 0.5, distance);

    float alpha = coverage * ColorModulator.a;

    if (alpha <= 0.0) discard;

    fragColor = vec4(
            ColorModulator.rgb,
            alpha
    );
}
