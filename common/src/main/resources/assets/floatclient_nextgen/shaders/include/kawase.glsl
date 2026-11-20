layout(std140) uniform Parameters {
    float offset;
    float inverseWidth;
    float inverseHeight;
    float mode;
};

// Fold pyramid coordinates for mirrored-repeat linear sampling.
vec4 pyramidSample(vec2 uv, bool mirror) {
    if (mirror) uv = 1.0 - abs(mod(uv, 2.0) - 1.0);
    return texture(inTexture, uv);
}
