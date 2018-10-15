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
    private boolean fuckKeyList = true;
    
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
    	camera.keyPressed(e);
    	//DO WHEN WE HAVE ROATION SOLVED IN CAMERA
    	//I add y position everytime, maybe set it instead of adding
    	//System.out.println("\n\n\nX is " + camera.getX() + "\nY is " + camera.getY() + "\nZ is " + camera.getZ());
/*    	float dx, dz;
    	double rads;
    	
    	if(-camera.getPosition().getX() > 0 && -camera.getPosition().getX() < terrain.getWidth()) {
      	  if (-camera.getPosition().getZ() > 0 && -camera.getPosition().getZ() < terrain.getDepth()) {
      		//camera.setView(camera.getView().translate(0, -terrain.altitude(-camera.getPosition().getX(), -camera.getPosition().getZ()), 0));
      		  //camera.setPosition(new Point3D(-camera.getPosition().getX(), -terrain.altitude(-camera.getPosition().getX(), -camera.getPosition().getZ()), -camera.getPosition().getZ()));
      		  //camera.computeView();
      		  rads = Math.toRadians(camera.getRotation()); 
      		  dz = (float) Math.cos(rads)*0.3f;
      		  dx = (float) Math.sin(rads)*0.3f;
      		  camera.translate(dx, dz);
      	  }
    	}*/
/*    	else {
    		camera.setView(frame);
    	}*/
    }

	//@Override
	public void keyReleased(KeyEvent e) {
		camera.keyReleased(e);
		
		switch(e.getKeyCode()) {
    	case KeyEvent.VK_R:
    		if(fpsMode && !e.isAutoRepeat()) {
    			if(fuckKeyList) {
    				fuckKeyList = false;
    			} else {
    				camera.setView(frame);
        			camera.translateXZ(0, 10f);
        			camera.computeView();
    				fpsMode = false;
    				fuckKeyList = true;
    			}
    		} else if(!e.isAutoRepeat()) {
    			if(fuckKeyList) {
    				fuckKeyList = false;
    			} else {
    				camera.translateXZ(0, -10f);
    				camera.computeView();
        			//camera.firstPerson(camera.getView());
    				fpsMode = true;
    				fuckKeyList = true;
    			}
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
