#version 450

in vec3 position;
in vec2 texCoord;
in vec3 normal;

out vec2 oTextureCoord;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransform;

vec2 positions[3] = {
    vec2(0.0, -0.5),
    vec2(0.5, 0.5),
    vec2(-0.5, 0.5)
};

void main() {
    vec4 worldPos = uTransform * vec4(position, 1.0);
    gl_Position = uProjection * worldPos;
    oTextureCoord = texCoord;
    //gl_Position = vec4(positions[gl_VertexIndex], 0.0, 1.0);
}
