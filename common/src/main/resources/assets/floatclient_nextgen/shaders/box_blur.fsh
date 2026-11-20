#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;
uniform sampler2D inTexture;
layout(std140) uniform Parameters {
    vec2 direction;
    float radius;
};

void main() {
    vec2 stepUV = direction / vec2(textureSize(inTexture, 0));
    vec4 total = vec4(0.0);
    for (int i = 0; i <= int(floor(2.0 * radius)); ++i) {
        total += texture(inTexture, texCoord + (float(i) - radius) * stepUV);
    }
    fragColor = vec4(total.rgb / (2.0 * radius + 1.0), min(total.a, 1.0));
}
