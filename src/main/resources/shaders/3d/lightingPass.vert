#version 330 core

out vec2 TexCoords;

layout (location = 0) in vec3 position;

uniform mat4 uProjection;

void main() {
    gl_Position = uProjection * vec4(position, 1.0);
}