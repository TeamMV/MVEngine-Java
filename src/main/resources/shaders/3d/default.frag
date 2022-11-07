#version 400

const int MAX_LIGHTS = 5;

in vec2 oTextureCoord;
in vec3 oNormal;
in vec3 oPosition;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

struct SpotLight {
    PointLight pointLight;
    vec3 coneDirection;
    float cutoff;
};

uniform sampler2D uTexSampler;
uniform vec3 uAmbient;
uniform float uSpecularPower;
uniform Material uMaterial;
uniform DirectionalLight uDirectionalLight;
uniform PointLight uPointLights[MAX_LIGHTS];
uniform SpotLight uSpotLights[MAX_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColor(Material material, vec2 texCoords) {
    if (material.hasTexture == 1) {
        ambientC = texture(uTexSampler, texCoords);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDirection, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    vec3 viewDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));
    float specularFactor = max(dot(viewDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, uSpecularPower);
    specularColor = specularC * lightIntensity * specularFactor * uMaterial.reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specularColor);
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDirection, normal);

    float distance = length(lightDirection);
    float attenuationInv = light.constant + light.linear * distance + light.exponent * distance * distance;
    return lightColor / attenuationInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.pointLight.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec3 fromLightDirection = -toLightDirection;
    float spotAlpha = dot(fromLightDirection, normalize(light.coneDirection));
    vec4 lightColor = vec4(0, 0, 0, 0);

    if(spotAlpha > light.cutoff) {
        lightColor = calcPointLight(light.pointLight, position, normal);
        lightColor *= (1.0 - (1.0 - spotAlpha) / (1.0 - light.cutoff));
    }

    return lightColor;
}

void main() {
    setupColor(uMaterial, oTextureCoord);

    vec4 lightResult = calcDirectionalLight(uDirectionalLight, oPosition, oNormal);
    for(int i = 0; i < MAX_LIGHTS; i++) {
        if(uPointLights[i].intensity > 0) {
            lightResult += calcPointLight(uPointLights[i], oPosition, oNormal);
        }
    }
    for(int i = 0; i < MAX_LIGHTS; i++) {
        if(uSpotLights[i].pointLight.intensity > 0) {
            lightResult += calcSpotLight(uSpotLights[i], oPosition, oNormal);
        }
    }


    fragColor = ambientC * vec4(uAmbient, 1.0) + lightResult;
}