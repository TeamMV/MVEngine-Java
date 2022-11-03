#version 400

in vec2 fragTextureCoord;

out vec4 fragColor;

uniform sampler2D TEX_SAMPLER;

void main() {
    fragColor = texture(TEX_SAMPLER, fragTextureCoord);
    //fragColor = vec4(1.0f, 0.0f, 0.0f, 1.0f);
}