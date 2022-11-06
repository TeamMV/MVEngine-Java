#version 400

in vec4 fColor;
in vec2 fTexCoords;
in float fTexID;
in float fRadius;

uniform sampler2D TEX_SAMPLER[16];

void main() {
    if (fTexID > 0) {
        vec4 c = texture(TEX_SAMPLER[int(fTexID)], fTexCoords);

        gl_FragColor = c;

        if (fColor.xyz != vec3(0.0) && fColor.w > 0.0) {
            gl_FragColor = vec4(fColor.x, fColor.y, fColor.z, c.w * (fColor.w / 1.0));
        }

        if (fColor.xyz == vec3(0.0) && fColor.w > 0.0) {
            gl_FragColor = vec4(c.x, c.y, c.z, c.w * (fColor.w / 1.0));
        }
    }
    else {

        gl_FragColor = fColor;
    }
}