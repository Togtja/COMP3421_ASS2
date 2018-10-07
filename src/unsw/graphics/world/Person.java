package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Person implements KeyListener {
    private Point3D position;
    private CoordFrame3D frame;
    private Camera camera;
    
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh model;
    private Texture texture;
    
    public Person() throws IOException {
    	
    	camera = new Camera(); // Creates a camera
    	position = new Point3D(0f, 0f, 6f);
    	
        model = new TriangleMesh("res/models/bunny.ply");      
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
        camera.setView(gl);
    	camera.firstPerson(camera.getView());
    	frame = camera.getfps().translate(position).scale(5f, 5f, 5f);
		//getWindow().addKeyListener(camera);
        //camera.firstPerson(gl, camera.getView());
        
    }
    public Camera getCam() {
    	return camera;
    }
    public void TrdPerson(GL3 gl) {
    	/*texture = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        
        Shader.setInt(gl,"tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
		*/
        Shader.setPenColor(gl, Color.BLUE);
    	//frame = camera.getfps().translate(position).scale(5f, 5f, 5f);
        //frame.
    	model.draw(gl, frame);
    }
    public CoordFrame3D getfps() {
    	return camera.getfps();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	camera.keyPressed(e);
    }

	@Override
	public void keyReleased(KeyEvent e) {
		camera.keyReleased(e);
		
	}
}
