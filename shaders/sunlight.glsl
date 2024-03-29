out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 sunlight; // sunlight direction
uniform vec3 lightIntensity; // for sunlight
uniform vec3 ambientIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;


// for torchlight
uniform vec3 lightIntensityTorch; // for torchlight
uniform vec3 dirTorch;  // in object coords
uniform vec3 posTorch;
uniform float cutoff; // in degrees
uniform float attenuationFactor;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

void main()
{
	// Compute the s, v and r vectors for the sunlight
    vec3 s = normalize(view_matrix*vec4(sunlight,0)).xyz;
    //vec3 s = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz;

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

    // for torchlight
    //double cutoff = radians(45);
    vec3 sTorch = normalize(view_matrix*vec4(posTorch,1) - viewPosition).xyz;


    float cosbeta = dot(sTorch, normalize(dirTorch));

    vec3 torchSpecular;
    if (acos(cosbeta) < radians(cutoff)){
    	torchSpecular = specularCoeff*lightIntensityTorch*pow(cosbeta, attenuationFactor);
    } else {
    	torchSpecular = vec3(0);
    }

	// Compute the s, v and r vectors for the torchlight
/*

    vec3 sTorch = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz; // vector with offset beta angle from torch direction
    vec3 vTorch = normalize(-viewPosition.xyz);
    vec3 rTorch = normalize(reflect(-sTorch,m));

    // calculate angle between the spotlight direction vector (s) and the light vector (r?)
    //float beta = dot(normalize(spotDirection), -normalize(lightDirection));
    float beta = dot(normalize(torchDir), sTorch);
    beta = max(angle, 0);*/





    // final calculation output
    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1) + vec4(torchSpecular, 1);

}


