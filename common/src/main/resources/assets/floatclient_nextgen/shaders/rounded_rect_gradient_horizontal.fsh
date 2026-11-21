#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

layout(std140) uniform Parameters {
    vec2 rectSize;
    float radius;
    uint leftArgb;
    uint rightArgb;
};

vec4 rgba(uint argb) {
    return vec4((argb >> 16u) & 255u, (argb >> 8u) & 255u,
            argb & 255u, argb >> 24u) / 255.0;
}

float roundSDF(vec2 p, vec2 b, float r) {
    return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
    float gradientFactor = texCoord.x;
    vec4 mixedColor = mix(rgba(leftArgb), rgba(rightArgb), gradientFactor);
    vec2 rectHalf = rectSize * .5;
    float smoothedAlpha = (1.0 - smoothstep(0.0, 1.0, roundSDF(rectHalf - (texCoord * rectSize), rectHalf - radius - 1., radius))) * mixedColor.a;
    fragColor = vec4(mixedColor.rgb, smoothedAlpha);
}
