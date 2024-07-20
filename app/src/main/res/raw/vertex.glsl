#version 320 es

layout (location = 0) in vec2 pos;
layout (location = 1) in vec3 uv_coord;

out vec3 color;

void main() {
    gl_Position = vec4(pos, 1.0, 1.0);
    color = vec3(uv_coord);
}