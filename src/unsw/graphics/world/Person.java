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
    private Camera camera;
    private boolean fpsMode = true;
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh model;
    private Texture texture;
    private Terrain terrain;
    private CoordFrame3D trdPerson;
    
    public Person(Terrain t) throws IOException {
    	camera = new Camera(t);//Camera(); // Creates a camera
    	position = new Point3D(2.3f, -1f, 2f); //DO NOT CHANGE UNLESS AVATAR IS NOT THERE
    	terrain = t;
        model = new TriangleMesh("res/models/bunny.ply");      
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
        camera.setView(gl);
        trdPerson = camera.getView();
    	
    }
    
    public Camera getCam() {
    	return camera;
    }
    
    public void drawPerson(GL3 gl) {

        Shader.setPenColor(gl, Color.BLUE);
    	model.draw(gl, trdPerson.translate(position).scale(5f, 5f, 5f).rotateY(50));
   
    }
    
    public CoordFrame3D getfps() {
    	//camera.setView(gl);
    	if(fpsMode) {
    		//trdPerson = camera.getView();
    		return camera.getView();
    	}
    	
    	//Else
    	float dir = -5f;
    	float speed = 0.3f;
		double rads = Math.toRadians(camera.getRotY());
		double dz = dir*Math.cos(rads)*speed;
		double dx = dir*Math.sin(rads)*speed;
		double dzR = dir*Math.cos(rads);
		double dxR = dir*Math.sin(rads);
    	return camera.getView().translate((float)dx, -1f, (float)dz);//.rotateX((float)dzR).rotateZ((float)dxR);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        float upSet = 1f; float offSet = 1f; //float avatarPlacement;
    	if(camera.getPosition().getX() + offSet > 0 
    			&& camera.getPosition().getX() < terrain.getWidth()  + offSet) {
      	  if (camera.getPosition().getZ()  + offSet > 0
      			  && camera.getPosition().getZ() < terrain.getDepth()  + offSet) {
      		//Some (up)off-setting needs to be done so that we have the correct height

      		 float x = camera.getPosition().getX();
      		 float z = camera.getPosition().getZ();
           	if(!fpsMode) {
       			float dir = -1; //float speed = 0.3f; 
       			double rads = Math.toRadians(camera.getRotY()); 
       			z += dir*Math.cos(rads);
       			x += dir*Math.sin(rads);
         	}
      		camera.setHeight(x, upSet,z, !fpsMode);
      		  
      	  }
    	}
    	
    }
    //public void key 
	@Override
	public void keyReleased(KeyEvent e) {
		//camera.keyReleased(e);
		switch(e.getKeyCode()) {
    	case KeyEvent.VK_R:
    		if(fpsMode && !e.isAutoRepeat()) {
    			fpsMode = false;
    			//camera.setRotX(10);
    		}
    		else if(!e.isAutoRepeat()) {
        		//camera.firstPerson(camera.getView());
    			fpsMode = true;
    			//camera.setRotX(-10);

    		}
    	}
	}
	
    public void setTerrain(Terrain terrain) {
    	this.terrain = terrain;
    }
    
    public Terrain getTerrain() {
    	return terrain;
    }
    public boolean isFps() {
    	return fpsMode;
    }
    public Point3D getPosition() {
    	return camera.getPosition();
    }
    public float getRot() {
    	return camera.getRotY();
    }
}
