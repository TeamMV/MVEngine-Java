#version 450
#ifndef VULKAN

in vec2 oTextureCoord;

out vec4 fragColor;

uniform sampler2D uTexSampler;

#else

layout (location = 0) in vec2 oTextureCoord;

layout (location = 0) out vec4 fragColor;

layout (binding = 0) uniform sampler2D uTexSampler;

#endif
void main() {
    fragColor = texture(uTexSampler, oTextureCoord);
}