#version 450

in vec2 oTextureCoord;

out vec4 outColor;

uniform sampler2D uTexSampler;

void main() {
    outColor = texture(uTexSampler, oTextureCoord);
    outColor = vec4(1.0, 0.0, 0.0, 1.0);
}