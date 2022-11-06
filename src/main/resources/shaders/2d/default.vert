#version 400

precision mediump float;

layout (location = 0) in vec3 aVertPos;
layout (location = 1) in float aRotation;
layout (location = 2) in vec4 aColor;
layout (location = 3) in vec2 aTexCoords;
layout (location = 4) in float aTexID;
layout (location = 5) in float aCameraMode;
layout (location = 6) in vec2 aRotationOrigin;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexID;

uniform mat4 uProjection;
uniform mat4 uView;

uniform float uResX;
uniform float uResY;

void main() {
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexID = aTexID;

    vec2 pos = aVertPos.xy;

    if (aRotation != 0) {
        mat2 rot;
        rot[0] = vec2(cos(aRotation), -sin(aRotation));
        rot[1] = vec2(sin(aRotation), cos(aRotation));
        pos.xy -= aRotationOrigin.xy;
        pos = rot * pos;
        pos.xy += aRotationOrigin.xy;
    }

    //camMode: 0 = dynamic; 1 = static;
    if (aCameraMode == 0) {
        gl_Position = uProjection * uView * vec4(pos, aVertPos.z, 1.0);
    } else {
        gl_Position = uProjection * vec4(pos, aVertPos.z, 1.0);
    }
}