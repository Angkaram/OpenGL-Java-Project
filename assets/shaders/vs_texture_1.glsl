#version 430 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTc;

uniform mat4 uProjMatrix;
uniform mat4 uViewMatrix;

out vec4 oColor;
out vec2 oTc;

void main()
{
    oColor = aColor;
    oTc = aTc;
    gl_Position = uProjMatrix * uViewMatrix * vec4(aPos, 1.0);
}
