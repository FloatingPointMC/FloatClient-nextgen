#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

layout(std140) uniform Parameters {
    vec2 rectSize;
    vec4 topColor;
    vec4 bottomColor;
    float radius;
    float yLimit;
};

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    float gradientFactor = texCoord.y;
    vec4 mixedColor = mix(topColor, bottomColor, gradientFactor);
    vec2 rectHalf = rectSize * .5;
    float currentRadius = radius;
    vec2 pixelPos = texCoord * rectSize;
    if (pixelPos.y > yLimit) {
        currentRadius = 0.0;
    }
    float smoothedAlpha = (1.0 - smoothstep(0.0, 1.0, roundSDF(rectHalf - (texCoord * rectSize), rectHalf - currentRadius - 1., currentRadius))) * mixedColor.a;
    fragColor = vec4(mixedColor.rgb, smoothedAlpha);
}