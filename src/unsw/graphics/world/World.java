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
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.world.Camera;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements KeyListener { //, MouseListener {

    private Terrain terrain;
    private Camera camera;
    private Tree tree; 
    
    private TriangleMesh terrainMesh;
    private TriangleMesh treeMesh; 
    
    private String treeFile = "res/models/tree.ply"; 
    
    //Debug stuff
    private float rotationZ,rotationY, rotationX;
    
    
    private Point3DBuffer vertexBuffer;
    private Point2DBuffer texCoordBuffer;
    private IntBuffer indicesBuffer;
    private int verticesName;
    private int texCoordsName;
    private int indicesName;

    private Texture texture;

    public World(Terrain terrain) {
    	super("Assignment 2", 600, 600);
        this.terrain = terrain;
        camera = new Camera(); // Creates a camera
        //treeMesh = new TriangleMesh(treeFile, true, true); // creates a tree 
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
        terrain.setTriangle();
        world.start();
    }
 

	/*@Override
	public void display(GL3 gl) {
		super.display(gl);
		camera.setView(gl);
		CoordFrame3D frame = CoordFrame3D.identity();
		terrain.draw(gl,frame);
        
	}*/
	
	@Override
	public void init(GL3 gl) {
		super.init(gl);
		getWindow().addKeyListener(camera);
		getWindow().addKeyListener(this);
        		
        terrainMesh = terrain.makeTerrain(gl);
        terrainMesh.init(gl);
        
        texture = new Texture(gl, "res/textures/kittens.jpg", "jpg", false);

        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl",
                "shaders/fragment_tex_phong.glsl");
        shader.use(gl);
        
        //terrainMesh.draw(gl, camera.getView());
        
        vertexBuffer = terrain.getVertexBuffer();
        texCoordBuffer = terrain.getTexCoordBuffer();
        indicesBuffer = terrain.getIndicesBuffer();

        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);

        verticesName = names[0];
        texCoordsName = names[1];
        indicesName = names[2];
        
        camera.setView(gl);
        //drawTerrain(gl, camera.getView());
	}
	
	@Override
    public void display(GL3 gl) {
        super.display(gl);
        Shader.setInt(gl,"tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

        Shader.setPenColor(gl, Color.MAGENTA);
        
        setLighting(gl); // set the lighting properties for the shader 
        camera.setView(gl); // sets the view matrix 

        
        //drawTerrain(gl, CoordFrame3D.identity().rotateY(rotationY));
        //terrain.draw(gl, CoordFrame3D.identity().rotateZ(rotationZ)); //.rotateY(rotationY).rotateX(rotationX));
        terrainMesh.draw(gl, camera.getView());
        
        rotationY += 1;
        //rotationZ += 1;
        //rotationX += 1;
        //getDefaultShader().use(gl);
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
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
        
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawElements(GL.GL_TRIANGLES, indicesBuffer.capacity(), 
                GL.GL_UNSIGNED_INT, 0);
        
       /* gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES,
                vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, texCoordBuffer.capacity() * 2 * Float.BYTES,
                texCoordBuffer.getBuffer(), GL.GL_STATIC_DRAW);
       
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
                indicesBuffer, GL.GL_STATIC_DRAW);	
        
        // not sure about this 
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawElements(GL.GL_TRIANGLES, indicesBuffer.capacity(), 
                GL.GL_UNSIGNED_INT, 0);*/
    }


	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}

	// implement methods to implement KeyListener interface
	@Override
	public void keyPressed(KeyEvent e) {
		camera.keyPressed(e); // do what camera would do 
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		camera.keyReleased(e); // do what camera would do 
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
