#version 330
#extension GL_ARB_separate_shader_objects : require

layout(location = 0) in vec2 texCoord;
layout(location = 0) out vec4 fragColor;

layout(std140) uniform Parameters {
    vec4 color;
    float angle;
    float thickness;
    vec2 size;
};

float segmentDistance(vec2 p, vec2 a, vec2 b) {
    vec2 direction = b - a;
    float t = clamp(dot(p - a, direction) / dot(direction, direction), 0.0, 1.0);
    return length(p - (a + direction * t));
}

void main() {
    vec2 p = texCoord * size - size * 0.5;
    float radians = angle * 0.017453292519943295;
    float sine = sin(radians);
    float cosine = cos(radians);
    p = mat2(cosine, -sine, sine, cosine) * p;

    float arm = min(size.x, size.y) * 0.34;
    vec2 tip = vec2(-arm, 0.0);
    vec2 upper = vec2(arm, -arm);
    vec2 lower = vec2(arm, arm);
    float distanceToLine = min(segmentDistance(p, tip, upper), segmentDistance(p, tip, lower));
    float edge = max(fwidth(distanceToLine), 0.5);
    float alpha = 1.0 - smoothstep(thickness - edge, thickness + edge, distanceToLine);
    alpha *= color.a;
    if (alpha <= 0.0) discard;
    fragColor = vec4(color.rgb, alpha);
}
