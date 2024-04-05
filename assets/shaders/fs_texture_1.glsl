#version 430 core

uniform sampler2D TEX_SAMPLER;

in vec4 oColor;
in vec2 oTc;

out vec4 color;

void main()
{
    color = texture(TEX_SAMPLER, oTc);
}
