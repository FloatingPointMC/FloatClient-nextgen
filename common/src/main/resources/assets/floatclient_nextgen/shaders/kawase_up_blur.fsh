#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

uniform sampler2D inTexture;
uniform sampler2D textureToCheck;
#include <floatclient_nextgen:kawase.glsl>

void main() {
    vec2 uv = texCoord;
    vec2 halfpixel = vec2(inverseWidth, inverseHeight);
    vec4 sum = pyramidSample(uv + vec2(-halfpixel.x * 2.0, 0.0) * offset, true);
    sum += pyramidSample(uv + vec2(-halfpixel.x, halfpixel.y) * offset, true) * 2.0;
    sum += pyramidSample(uv + vec2(0.0, halfpixel.y * 2.0) * offset, true);
    sum += pyramidSample(uv + vec2(halfpixel.x, halfpixel.y) * offset, true) * 2.0;
    sum += pyramidSample(uv + vec2(halfpixel.x * 2.0, 0.0) * offset, true);
    sum += pyramidSample(uv + vec2(halfpixel.x, -halfpixel.y) * offset, true) * 2.0;
    sum += pyramidSample(uv + vec2(0.0, -halfpixel.y * 2.0) * offset, true);
    sum += pyramidSample(uv + vec2(-halfpixel.x, -halfpixel.y) * offset, true) * 2.0;

    fragColor = vec4(sum.rgb / 12.0, mix(1.0, texture(textureToCheck, texCoord).a, mode));
}