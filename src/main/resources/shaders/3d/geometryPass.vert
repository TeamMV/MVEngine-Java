#version 400

layout (location = 0) in vec3 position;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in vec3 normal;

out vec2 TextureCoords;
out vec3 Normal;
out vec3 FragPos;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransform;

void main() {
    vec4 worldPos = uTransform * vec4(position, 1.0);
    gl_Position = uProjection * uView * worldPos;
    TextureCoords = texCoord;
    Normal = normalize(normal);
    FragPos = worldPos.xyz;
}