#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

uniform sampler2D inTexture;
#include <floatclient_nextgen:kawase.glsl>

// A GUI source-over draw onto transparent black already stores alpha-weighted RGB.
vec4 weightedSample(vec2 uv) {
    vec4 sampleColor = pyramidSample(uv, mode > 0.5);
    if (mode > 0.5) sampleColor.rgb *= sampleColor.a;
    return sampleColor;
}

void main() {
    vec2 d = vec2(inverseWidth, inverseHeight) * offset;
    vec4 sum = 4.0 * weightedSample(texCoord);
    sum += weightedSample(texCoord + d) + weightedSample(texCoord - d);
    sum += weightedSample(texCoord + vec2(d.x, -d.y)) + weightedSample(texCoord - vec2(d.x, -d.y));
    fragColor = vec4(sum.a > 0.0 ? sum.rgb / sum.a : vec3(0.0), sum.a / 8.0);
}
