#version 450
layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

in vec2 TextureCoords;
in vec3 Normal;
in vec3 FragPos;

uniform sampler2D uDiffuse;
uniform sampler2D uSpecular;

void main() {
    // store the fragment position vector in the first gbuffer texture
    gPosition = FragPos;
    // also store the per-fragment normals into the gbuffer
    gNormal = normalize(Normal);
    // and the diffuse per-fragment color
    gAlbedoSpec.rgb = texture(uDiffuse, TextureCoords).rgb;
    //gAlbedoSpec.rgb = vec3(1.0);
    // store specular intensity in gAlbedoSpec's alpha component
    gAlbedoSpec.a = texture(uSpecular, TextureCoords).r;
}  