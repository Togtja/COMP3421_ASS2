
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 lightDir;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

void main()
{
    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(lightDir,0) - viewPosition).xyz; // changed from 1 to 0 for vec4 lightDir vector
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m));

    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);
    vec3 specular;

    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);

    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);

    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
}

void calcOutputColor(uniform mat4 viewMatrix, uniform vec3 lightDirection, uniform vec3 lightIntensity, )
