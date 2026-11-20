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
    vec4 total = texture(inTexture, texCoord);
    for (int i = 1; i <= int(ceil(radius)); ++i) {
        vec2 delta = stepUV * float(i);
        float weight = min(1.0, radius - float(i) + 1.0);
        total += weight * (texture(inTexture, texCoord - delta) + texture(inTexture, texCoord + delta));
    }
    fragColor = total / (2.0 * radius + 1.0);
}
