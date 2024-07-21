#version 320 es

layout (location = 0) in vec2 pos;
layout (location = 1) in vec2 uv_coord;

out vec2 uvCoord;
out vec2 posNormal;

void main() {
    gl_Position = vec4(pos, 1.0, 1.0);
    posNormal = pos;
    uvCoord = uv_coord;
}