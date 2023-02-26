#version 400

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in vec3 normal;

out vec2 oTextureCoord;
out vec4 oColor;
out vec3 oNormal;
out vec3 oPos;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransform;

void main() {
    vec4 worldPos = uTransform * vec4(position, 1.0);
    gl_Position = uProjection * uView * worldPos;
    oTextureCoord = texCoord;
    oNormal = normal;
    oPos = worldPos.xyz;
    oColor = color;
}