package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.world.Person;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.world.Camera;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements KeyListener {

    private Terrain terrain;
    //private Camera camera;
    private Person person;
    private TriangleMesh terrainMesh;

    private int verticesName;
    private int texCoordsName;
    private int indicesName;

    private Texture grass;

    public World(Terrain terrain) {
    	super("Assignment 2", 2000, 2000);
    	//super("Assignment 2", 600, 600);
        this.terrain = terrain;
        //camera = new Camera(); // Creates a camera
        try {
			person = new Person();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
   
    
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain); 
        world.start();
    }

	
	@Override
	public void init(GL3 gl) {
		super.init(gl);
		getWindow().addKeyListener(person);
		getWindow().addKeyListener(person.getCam());
		getWindow().addKeyListener(this);
        

		person.init(gl);
		
        //terrainMesh = terrain.makeTerrain(gl); // gets vertex, indices, and tex coord buffers for terrain 
		
		
        terrain.makeTerrain(gl); // gets vertex, indices, and tex coord buffers for terrain
		terrainMesh = terrain.getTriMesh();
        terrainMesh.init(gl);
        
        //camera = new Camera(terrainMesh);
        
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/sunlight.glsl"); //"shaders/fragment_tex_phong.glsl");
        shader.use(gl);
        
        //setLighting(gl); // set the lighting properties for the shader 
      
        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);

        verticesName = names[0];
        texCoordsName = names[1];
        indicesName = names[2];
        
        //camera.setView(gl);
        //camera.firstPerson(gl, camera.getView());
	}
	
	@Override
    public void display(GL3 gl)  {
        super.display(gl);
        grass = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        
        Shader.setInt(gl,"tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, grass.getId());

        Shader.setPenColor(gl, Color.GREEN);
                
        setLighting(gl); // set the lighting properties for the shader 

        gl.glPolygonMode(GL3.GL_FRONT_AND_BACK,GL3.GL_FILL); // GL3.GL_LINE); // DEBUG: shows as lines vs. filled in ground 
        
        terrainMesh.draw(gl, person.getfps());
        terrain.drawTrees(gl, person.getfps());
        person.TrdPerson(gl);

    }

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		terrainMesh.destroy(gl);
        //gl.glDeleteBuffers(2, new int[] { indicesName, verticesName }, 0);
        //terrain.destroy(gl);
	}

	
    /**
     * Draw a terrain centered around (0,0) with bounds of length 1 in each
     * direction.
     * 
     * @param gl
     * @param frame
     */
    private void drawTerrain(GL3 gl, CoordFrame3D frame) {
        
    }

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}

	// implement methods to implement KeyListener interface
	@Override
	public void keyPressed(KeyEvent e) {
		person.keyPressed(e); // do what camera would do 
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		person.keyReleased(e); // do what camera would do 
		switch(e.getKeyCode()) {
    	case KeyEvent.VK_R:
    		
		}
	}
	
    /**
     * Set lighting and material properties 
     * 
     * @param gl
     * @param shader
     */
	public void setLighting(GL3 gl) {
		 // Set the lighting properties
        Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setFloat(gl, "phongExp", 16f);
	}
}