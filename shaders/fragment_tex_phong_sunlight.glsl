
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
//uniform vec3 lightPos; // no light pos, should be uniform, just shift vector to correct position
uniform vec3 lightVec;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;     // vertex normal in view coordinates


in vec2 texCoordFrag;


// use view matrix to multiply light vector to convert from camera coords to view coords

void main()
{
    // Compute the s, v and r vectors
    vec3 s = normalize(-lightVec); // source vector does not change for sunlight based on light pos
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
