#version 400

in vec3 position;
in vec2 texCoord;

out vec2 fragTexCoord;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransform;

void main() {
    gl_Position = uProjection * uView * uTransform * vec4(position, 1.0);
    fragTexCoord = texCoord;
}