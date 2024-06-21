#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform int[8] allthetrims_TrimPalette;
uniform int allthetrims_Debug;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;

out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if (texColor.a < 0.1) {
        discard;
    }
    int index = int(texColor.r * 7.0);
    vec4 color;
    if (allthetrims_Debug == 1) {
    switch (index) {
        case 0:
            color = vec4(1, 0, 0, 1); // red
            break;
        case 1:
            color = vec4(0, 0, 1, 1); // blue
            break;
        case 2:
            color = vec4(0, 1, 0, 1); // green
            break;
        case 3:
            color = vec4(1, 0, 1, 1); // magenta
            break;
        case 4:
            color = vec4(0, 1, 1, 1); // cyan
            break;
        case 5:
            color = vec4(1, 1, 0, 1); // yellow
            break;
        case 6:
            color = vec4(0.5, 0, 0.5, 1); // purple
            break;
        case 7:
            color = vec4(0.5, 1, 0.1, 1); // lime
            break;
        }
    } else {
        int trimColor = allthetrims_TrimPalette[index];
        int red = trimColor >> 16 & 0xFF;
        int green = trimColor >> 8 & 0xFF;
        int blue = trimColor & 0xFF;
        float one255ths = 0.003921568627;
        color = vec4(red * one255ths, green * one255ths, blue * one255ths, 1.0);
        color *= vertexColor * ColorModulator;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
