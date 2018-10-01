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
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
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
    private Camera camera;
    private TriangleMesh terrainMesh;
    private float rotationY;
    private Point3DBuffer vertexBuffer;
    private IntBuffer indicesBuffer;
    private int verticesName;
    private int indicesName;


    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        camera = new Camera(); // Creates a camera
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain);
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
    public void display(GL3 gl) {
        super.display(gl);
        Shader.setPenColor(gl, Color.MAGENTA);
        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, 0, -2)
                .scale(0.5f, 0.5f, 0.5f);
//        drawCube(gl, frame.rotateY(rotationY));
        
        terrain.draw(gl, frame.rotateY(rotationY));

        rotationY += 1;
    }

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
        gl.glDeleteBuffers(2, new int[] { indicesName, verticesName }, 0);
        terrain.destroy(gl);
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		getWindow().addKeyListener(camera);
		getWindow().addKeyListener(this);
        
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong_sunlight.glsl",
                "shaders/fragment_tex_phong_sunlight.glsl");
        shader.use(gl);

        // cube vertices from lecture example
		vertexBuffer = new Point3DBuffer(Arrays.asList(
				new Point3D(-1,-1,1), 
				new Point3D(1,-1,1), 
				new Point3D(1,1,1),
				new Point3D(-1,1,1),
				new Point3D(-1,-1,-1), 
				new Point3D(1,-1,-1), 
				new Point3D(1,1,-1),
				new Point3D(-1,1,-1)));

		// cube indices 
		indicesBuffer = GLBuffers.newDirectIntBuffer(new int[] {
				0,1,2,
				2,3,0,
				1,5,6,
				6,2,1,
				5,4,7,
				7,6,5,
				4,0,3,
				3,7,4,
				3,2,6,
				6,7,3,
				4,5,1,
				1,0,4
		});

		int[] names = new int[2];
		gl.glGenBuffers(2, names, 0);

		verticesName = names[0];
		indicesName = names[1];

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES,
				vertexBuffer.getBuffer(), GL.GL_STATIC_DRAW);

		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
				indicesBuffer, GL.GL_STATIC_DRAW);

		try {
			terrainMesh = new TriangleMesh("res/models/cube.ply");
			terrainMesh.init(gl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
