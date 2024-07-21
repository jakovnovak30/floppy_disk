#version 320 es

precision highp float;

in vec2 uvCoord;
in vec2 posNormal;

out vec4 fragColor;

uniform float currY;
uniform float currX;

uniform bool lastFrame;
uniform float YHeight;
uniform float XWidth;
const float koef = 0.5f;

uniform sampler2D texSampler;

void main() {
    if(lastFrame) {
        fragColor = texture(texSampler, uvCoord);
    }
    else if(posNormal.y < currY) {
        discard;
    }
    else if(posNormal.y > currY && posNormal.y < currY + YHeight) {
        if(posNormal.x > currX && posNormal.x < currX + XWidth)
            fragColor = vec4(0.2f, 0.2f, 0.2f, 1.f) * koef + texture(texSampler, uvCoord) * (1.f - koef);
        else if(posNormal.x < currX + XWidth)
            fragColor = vec4(0.8f, 0.8f, 0.8f, 1.f) * koef + texture(texSampler, uvCoord) * (1.f - koef);
        else
            discard;
    }
    else {
        fragColor = texture(texSampler, uvCoord);
    }
}