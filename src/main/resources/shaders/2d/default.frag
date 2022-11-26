#version 450

in vec4 fColor;
in vec2 fTexCoords;
in float fTexID;
in float fRadius;

out vec4 outColor;

uniform sampler2D TEX_SAMPLER[16];

void main() {
    if (fTexID > 0) {
        vec4 c = texture(TEX_SAMPLER[int(fTexID)], fTexCoords);

        outColor = c;

        if (fColor.xyz != vec3(0.0) && fColor.w > 0.0) {
            outColor = vec4(fColor.x, fColor.y, fColor.z, c.w * (fColor.w / 1.0));
        }

        if (fColor.xyz == vec3(0.0) && fColor.w > 0.0) {
            outColor = vec4(c.x, c.y, c.z, c.w * (fColor.w / 1.0));
        }
    }
    else {

        outColor = fColor;
    }
}