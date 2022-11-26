#version 450

in vec3 position;
in vec2 texCoord;
in vec3 normal;

out vec2 oTextureCoord;
out vec4 outPosition;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransform;

void main() {
    vec4 worldPos = uTransform * vec4(position, 1.0);
    outPosition = uProjection * uView * worldPos;
    oTextureCoord = texCoord;
}