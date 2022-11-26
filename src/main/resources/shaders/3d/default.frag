#version 450

in vec2 oTextureCoord;

out vec4 outColor;

uniform sampler2D uTexSampler;

//layout (location = 0) in vec2 oTextureCoord;

//layout (binding = 0) uniform sampler2D uTexSampler;

void main() {
    outColor = texture(uTexSampler, oTextureCoord);
    //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}