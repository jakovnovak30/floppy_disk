#version 320 es

precision mediump float;

in vec2 uvCoord;
in vec2 posNormal;

out vec4 fragColor;

uniform float time;
uniform sampler2D texSampler;

void main() {
    /*
    if(posNormal.y > time)
            discard;
*/

    fragColor = texture(texSampler, uvCoord);
}