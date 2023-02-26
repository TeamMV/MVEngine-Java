#version 450

precision highp float;

in vec4 fColor;
in vec2 fTexCoords;
in float fTexID;
in vec4 fCanvasCoords;
in vec2 fRes;

out vec4 outColor;

uniform sampler2D TEX_SAMPLER[16];

void main() {
    if (fTexID > 0) {
        vec4 c = texture(TEX_SAMPLER[int(fTexID) - 1], fTexCoords);

        if (fColor.w > 0.0) {
            outColor = vec4(fColor.x, fColor.y, fColor.z, c.w);
        } else {
            outColor = c;
        }
    }
    else {
        outColor = fColor;
    }
}