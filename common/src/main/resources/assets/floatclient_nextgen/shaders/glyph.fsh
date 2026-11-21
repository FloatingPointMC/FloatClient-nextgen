#version 330
#extension GL_ARB_separate_shader_objects : require
layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;
uniform sampler2D Sampler0;
layout(std140) uniform Parameters {
    uint argb;
};

void main() {
    vec4 tint = vec4((argb >> 16u) & 255u, (argb >> 8u) & 255u, argb & 255u, argb >> 24u) / 255.0;
    fragColor = vec4(tint.rgb, tint.a * texture(Sampler0, texCoord).a);
}
