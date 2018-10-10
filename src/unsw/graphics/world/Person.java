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
    	position = new Point3D(0f, 0f, 6f);
    	terrain = t;
        model = new TriangleMesh("res/models/bunny.ply");      
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
        camera.setView(gl);
    	frame = camera.getView().translate(position).scale(5f, 5f, 5f);
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
    	/*
    	if(-camera.getX() > 0 && -camera.getX() < terrain.getWidth()) {
      	  if (-camera.getZ() > 0 && -camera.getZ() < terrain.getDepth()) {
      		camera.setView(camera.getView().translate(0, -terrain.altitude(-camera.getX(), -camera.getY()), 0));
      	  }
    	}
    	else {
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
        			camera.translate(0, 10f);
        			camera.computeView();
    				fpsMode = false;
    				fuckKeyList = true;
    			}
    		} else if(!e.isAutoRepeat()) {
    			if(fuckKeyList) {
    				fuckKeyList = false;
    			} else {
    				camera.translate(0, -10f);
    				camera.computeView();
        			//camera.firstPerson(camera.getView());
    				fpsMode = true;
    				fuckKeyList = true;
    			}
    		}
    	}
	}
}
