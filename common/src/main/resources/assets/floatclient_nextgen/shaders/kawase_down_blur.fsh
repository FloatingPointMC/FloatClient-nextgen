#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

uniform sampler2D inTexture;
#include <floatclient_nextgen:kawase.glsl>

void main() {
    vec2 uv = texCoord;
    vec2 halfpixel = vec2(inverseWidth, inverseHeight);
    vec4 sum = pyramidSample(texCoord, mode > 0.5) * 4.0;
    sum += pyramidSample(uv - halfpixel.xy * offset, mode > 0.5);
    sum += pyramidSample(uv + halfpixel.xy * offset, mode > 0.5);
    sum += pyramidSample(uv + vec2(halfpixel.x, -halfpixel.y) * offset, mode > 0.5);
    sum += pyramidSample(uv - vec2(halfpixel.x, -halfpixel.y) * offset, mode > 0.5);
    fragColor = vec4(sum.rgb * .125, 1.0);
}