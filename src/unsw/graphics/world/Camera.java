package unsw.graphics.world;


import java.util.List;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.scene.MathUtil;

public class Camera extends WorldObject implements  KeyListener {
    // the local transformation
    private CoordFrame3D viewFrame;
    private Terrain terrain;
    private float myAspectRatio;



    public Camera(WorldObject parent) {
    	super(parent);
    	viewFrame = CoordFrame3D.identity();
    	setRotY(180);
        myAspectRatio = 1;
    }
    
    public Camera(Terrain t, WorldObject parent) {
    	super(parent);
        viewFrame = CoordFrame3D.identity();
        setRotY(230);
        setPosition(new Point3D(0,1,0));
        myAspectRatio = 1;
        terrain = t;
    }
    
    public void setView(GL3 gl) {
        // TODO compute a view transform to account for the cameras aspect ratio
        
    	// compute view transform for dimensions of screen;
    	CoordFrame3D viewFrame = CoordFrame3D.identity()
    			.scale(1/getAspectRatio(), 1, 1)
    	// TODO apply further transformations to account for the camera's global position, 
    	// rotation and scale
                .scale(1/getScale(), 1/getScale(), 1/getScale())
                //.rotateY(-1*getRotation())
                .rotateY(-1*getRotY()).rotateX(-1*getRotX()).rotateZ(-1*getRotZ())
                .translate(-1*getPosition().getX(), -1*getPosition().getY(), -1*getPosition().getZ());
    	
        // TODO set the view matrix to the computed transform
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }
    
    public void setPosition(float x, float z) {
    	float y = terrain.altitude(x, z);
        setPosition(new Point3D(x,y,z));
    }    
    
    
    //Used to set height for an avatar infront of you
    public void setHeight(float yOff, float avatar) {
    	float y = terrain.altitude(getPosition().getX(),getPosition().getZ() );
        setPosition(new Point3D(getPosition().getX(), y+ yOff, getPosition().getZ()));  
        //computeView();
    }

    
    public void setHeight(float x, float yOff, float z, boolean avatar) {
    	int w = terrain.getWidth();
    	int d = terrain.getDepth();
    	float y = 0;
    	
    	if (x >= w || x < 0 || z >= d || z < 0) { } // index out of bounds 
    	else { y = terrain.altitude(x, z); }
    	
    	setPosition(getPosition().getX(), y+yOff, getPosition().getZ());
        computeView();
    }
    
    public void setTerrain(Terrain terrain) {
    	this.terrain = terrain;
    }
    
    public Terrain getTerrain() {
    	return terrain;
    }
    
    public CoordFrame3D getView() {
        return viewFrame;
    }
    
    public float getAspectRatio() {
        return myAspectRatio;
    }
    
    public void translateXZ(float dx, float dz) {
    	setPosition(getPosition().translate(dx, 0, dz)); 
    }
    
    public void translateY(float dy) {
    	setPosition(getPosition().translate(0,dy,0)); 
    }

    public void computeView() {
    	viewFrame = CoordFrame3D.identity()
    			.scale(1/getAspectRatio(), 1, 1)     	// compute view transform for dimensions of screen
    			//further transformations to account for the camera's global position, rotation and scale
                .scale(1/getScale(), 1/getScale(), 1/getScale())
                .rotateY(-1*getRotY()).rotateX(-1*getRotX()).rotateZ(-1*getRotZ())
                .translate(-1*getPosition().getX(), -1*getPosition().getY(), -1*getPosition().getZ());
    }
    
    public void reshape(int width, int height) {
        myAspectRatio = (1f * width) / height;            
    }
    
    public boolean checkTerrainBounds(float x, float z) {
    	int width = terrain.getWidth();
    	int depth = terrain.getDepth();
    	 if (x >= width || x < 0 || z >= depth || z < 0) { // index out of bounds 
         	return false;
         }
    	return true; // index within terrain bounds 
    }
    

    public void keyPressed(KeyEvent e) {
        
    	double rads = 0; 
    	double dx = 0, dy = 0, dz = 0; 
    	float rotShift = 0;
    	float dir = 0;
    	float speed = 0.3f;
    	
        switch(e.getKeyCode()) {  
        case KeyEvent.VK_D:							// D key pressed, camera step right 
        	dir = -1;
  			rads = Math.toRadians(getRotY()); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*0.3f; // dir*Math.sin(rads)*0.3f;
  			translateXZ((float) dx, (float) dz);
        	break;
        
        case KeyEvent.VK_A:							// A key pressed, step left 
        	dir = 1;
  			rads = Math.toRadians(getRotY()); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*0.3f; // dir*Math.sin(rads)*0.3f;
  			translateXZ((float) dx, (float) dz);
        	break;
        	
        case KeyEvent.VK_W: // rotate camera up around x/z axis 
        	if(e.isShiftDown()) {
        		dir = 1;
        		rotShift = dir*1;
  				rotateZ(rotShift);
  				rotateX(rotShift);
        	}
        	else {
        		dir = 1;
        		rads = Math.toRadians(getRotZ()); 
        		dy = dir*Math.cos(rads)*speed;
        		//dx = dir*Math.sin(rads)*speed;
        		translateY((float) dy);
        	}
        	break;
        	
        case KeyEvent.VK_S: // rotate camera down around x/z axis 
        	if(e.isShiftDown()) {
        		dir = -1;
        		rotShift = dir*1;
  				rotateZ(rotShift);
  				rotateX(rotShift);
        	}
        	else {
        		dir = -1;
  				rads = Math.toRadians(getRotZ()); 
  				dy = dir*Math.cos(rads)*speed;
  				//dx = dir*Math.sin(rads)*speed;
  				translateY((float) dy);
        	}
        	break;
        
        
        //Delivery keys
  		case KeyEvent.VK_UP:						// Up arrow pressed, camera moves forward 
  			dir = -1;
  			rads = Math.toRadians(getRotY()); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translateXZ((float) dx, (float) dz);
  			break;
  			
  		case KeyEvent.VK_DOWN:						// Down arrow pressed, camera moves backwards
  			dir = 1;
  			rads = Math.toRadians(getRotY());
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translateXZ((float) dx, (float) dz);
  			break;
  			
  		case KeyEvent.VK_LEFT:
  			dir = 5;
  			rotShift = dir*1;
  			rotateY(rotShift);
        	break;
        	
  		case KeyEvent.VK_RIGHT:
  			dir = -5;
  			rotShift = dir*1;
  			rotateY(rotShift);
        	break;
        }
        //Make sure we stay in bound

    	computeView();
    }
    
    //How to debug control
    // w moves you closer aka +z
    // s move you further away aka -z
    // a moves you to the left aka -x
    // d moves you to the left aka +x
    // q/e rotates with z
    // shift + w/s rotates x 
    // shift + a/d rotates y
    // shift + q/e scales uniformly
    
    @Override
    public void keyReleased(KeyEvent e) {
    	
    }
    
}
