#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

uniform sampler2D inTexture;
#include <floatclient_nextgen:kawase.glsl>

void main() {
    vec2 uv = texCoord;
    vec2 halfpixel = vec2(inverseWidth, inverseHeight);
    vec4 sum = pyramidSample(texCoord, mode > 0.5);
    sum.rgb *= sum.a;
    sum *= 4.0;
    vec4 smp1 = pyramidSample(uv - halfpixel.xy * offset, mode > 0.5);
    smp1.rgb *= smp1.a;
    sum += smp1;
    vec4 smp2 = pyramidSample(uv + halfpixel.xy * offset, mode > 0.5);
    smp2.rgb *= smp2.a;
    sum += smp2;
    vec4 smp3 = pyramidSample(uv + vec2(halfpixel.x, -halfpixel.y) * offset, mode > 0.5);
    smp3.rgb *= smp3.a;
    sum += smp3;
    vec4 smp4 = pyramidSample(uv - vec2(halfpixel.x, -halfpixel.y) * offset, mode > 0.5);
    smp4.rgb *= smp4.a;
    sum += smp4;
    vec4 result = sum / 8.0;
    fragColor = vec4(result.a > 0.0 ? result.rgb / result.a : vec3(0.0), result.a);
}