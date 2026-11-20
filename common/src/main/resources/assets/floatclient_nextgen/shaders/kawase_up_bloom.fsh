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
    sum.rgb *= sum.a;
    vec4 smpl1 = pyramidSample(uv + vec2(-halfpixel.x, halfpixel.y) * offset, true);
    smpl1.rgb *= smpl1.a;
    sum += smpl1 * 2.0;
    vec4 smp2 = pyramidSample(uv + vec2(0.0, halfpixel.y * 2.0) * offset, true);
    smp2.rgb *= smp2.a;
    sum += smp2;
    vec4 smp3 = pyramidSample(uv + vec2(halfpixel.x, halfpixel.y) * offset, true);
    smp3.rgb *= smp3.a;
    sum += smp3 * 2.0;
    vec4 smp4 = pyramidSample(uv + vec2(halfpixel.x * 2.0, 0.0) * offset, true);
    smp4.rgb *= smp4.a;
    sum += smp4;
    vec4 smp5 = pyramidSample(uv + vec2(halfpixel.x, -halfpixel.y) * offset, true);
    smp5.rgb *= smp5.a;
    sum += smp5 * 2.0;
    vec4 smp6 = pyramidSample(uv + vec2(0.0, -halfpixel.y * 2.0) * offset, true);
    smp6.rgb *= smp6.a;
    sum += smp6;
    vec4 smp7 = pyramidSample(uv + vec2(-halfpixel.x, -halfpixel.y) * offset, true);
    smp7.rgb *= smp7.a;
    sum += smp7 * 2.0;
    vec4 result = sum / 12.0;
    fragColor = vec4(result.a > 0.0 ? result.rgb / result.a : vec3(0.0), mix(result.a, result.a * (1.0 - texture(textureToCheck, texCoord).a), mode));
}