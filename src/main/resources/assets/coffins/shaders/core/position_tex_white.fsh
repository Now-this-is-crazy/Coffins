#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float lerp(float a, float min, float max) {
    return min + a * (max - min);
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = vec4(lerp(0.4, color.r, 1), lerp(0.4, color.g, 1), lerp(0.4, color.b, 1), color.a) * ColorModulator;
}