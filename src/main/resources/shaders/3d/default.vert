#version 450
#ifndef VULKAN

in vec3 position;
in vec2 texCoord;
in vec3 normal;

out vec2 oTextureCoord;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransform;

#else

//layout (location = 0) in vec3 position;
//layout (location = 1) in vec2 texCoord;
//layout (location = 2) in vec3 normal;
//
//layout (location = 0) out vec2 oTextureCoord;
//
//layout (binding = 0) uniform mats {
//    uniform mat4 uProjection;
//    uniform mat4 uView;
//    uniform mat4 uTransform;
//} matrices;

//mat4 uProjection = matrices.uProjection;
//mat4 uView = matrices.uView;
//mat4 uTransform = matrices.uTransform;

#endif

vec2 positions[3] = {
    vec2(0.0, -0.5),
    vec2(0.5, 0.5),
    vec2(-0.5, 0.5)
};

void main() {
    //vec4 worldPos = uTransform * vec4(position, 1.0);
    //gl_Position = uProjection * uView * worldPos;
    //oTextureCoord = texCoord;
    gl_Position = vec4(positions[gl_VertexIndex], 0.0, 1.0);
}
