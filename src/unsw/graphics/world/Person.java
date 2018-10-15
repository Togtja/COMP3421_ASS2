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
    private boolean fpsMode = true;
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh model;
    private Texture texture;
    private Terrain terrain;
    
    public Person(Terrain t) throws IOException {
    	camera = new Camera(t);//Camera(); // Creates a camera
    	position = new Point3D(0f, 0.5f, -6f);
    	terrain = t;
        model = new TriangleMesh("res/models/bunny.ply");      
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
        camera.setView(gl);
    	frame = camera.getView().translate(position).rotateY(180).scale(5f, 5f, 5f);
    }
    
    public Camera getCam() {
    	return camera;
    }
    
    public void drawPerson(GL3 gl) {

        Shader.setPenColor(gl, Color.BLUE);
    	model.draw(gl, frame);
    }
    
    public CoordFrame3D getfps() {
    	return camera.getView();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	//camera.keyPressed(e);
    	//DO WHEN WE HAVE ROATION SOLVED IN CAMERA
    	//I add y position everytime, maybe set it instead of adding
    	//System.out.println("\n\n\nX is " + camera.getX() + "\nY is " + camera.getY() + "\nZ is " + camera.getZ());
    	
    	
    }
    //public void key 
	@Override

	public void keyReleased(KeyEvent e) {
		//camera.keyReleased(e);
		
		switch(e.getKeyCode()) {
    	case KeyEvent.VK_R:
    		if(fpsMode && !e.isAutoRepeat()) {
        		camera.setView(frame);
        		frame = frame.translate(0, 0, 10f);
    			fpsMode = false;
    		}
    		else if(!e.isAutoRepeat()) {
    			frame = frame.translate(0, 0, -10f);
        		//camera.firstPerson(camera.getView());
    			fpsMode = true;
    			

    		}
    	}
	}
	
    public void setTerrain(Terrain terrain) {
    	this.terrain = terrain;
    }
    
    public Terrain getTerrain() {
    	return terrain;
    }
}
