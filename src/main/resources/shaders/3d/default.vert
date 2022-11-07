#version 400

in vec3 position;
in vec2 texCoord;
in vec3 normal;

out vec2 oTextureCoord;
out vec3 oNormal;
out vec3 oPos;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransform;

void main() {
    vec4 worldPos = uTransform * vec4(position, 1.0);
    gl_Position = uProjection * uView * worldPos;
    oTextureCoord = texCoord;
    oNormal = normalize(worldPos).xyz;
    oPos = worldPos.xyz;
}