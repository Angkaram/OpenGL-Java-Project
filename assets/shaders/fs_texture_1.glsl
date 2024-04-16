#version 430 core

uniform sampler2D TEX_SAMPLER;

in vec4 fColor;
in vec2 fTexCoords;

out vec4 color;

void main()
{
    color = fColor * 0.7f + texture(TEX_SAMPLER, fTexCoords);
}
