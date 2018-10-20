package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix3;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Person extends WorldObject implements KeyListener {
    //private Point3D position; //The model position
    private boolean fpsMode;
    private boolean tp = false; //if person just teleporten
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh model;
    private Camera camera;
    CoordFrame3D frame = CoordFrame3D.identity() ;
    //private WorldObject parent;
    //private Terrain terrain; 
    private float yOff = 1.65f;
    
    public Person(WorldObject parent, Terrain t) throws IOException {
    	super(parent);
    	//this.parent = parent;
    	this.setPosition(parent.getPosition());
    	this.scale(5);
        model = new TriangleMesh("res/models/bunny.ply", true, true);
        camera = new Camera(parent);
        terrain = t;
        fpsMode = false; //true; 
        setPosition(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
					camera.getPosition().getY() -yOff,
					camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())));
        frame = CoordFrame3D.identity()
     			.translate(getPosition())
     			.scale(getScale());
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
    	camera.setView(gl);
    }
    
    public void drawPerson(GL3 gl) {
    	if (fpsMode) {
    		//model.destroy(gl);
    	} else {
    	
        Shader.setPenColor(gl, Color.BLUE);
        //camera.translate(
    	//				-(float)Math.sin(Math.toRadians(camera.getRotY())),
    	//				-5f,
    	//				-(float)Math.cos(Math.toRadians(camera.getRotY())));
        
        
        model.draw(gl, frame); 
    	}
    }

    

    @Override
    public void keyPressed(KeyEvent e) {
    	Point3D currPos = getGlobalPosition();
    	
    	setHeight(currPos.getX(), currPos.getZ(), isFps());
        
     	double rads = 0; 
     	double dx = 0, dy = 0, dz = 0; 
     	float rotShift = 0;
     	float dir = 0;
     	float speed = 0.3f;
     	Vector3 personVect = personVector();
     	
     	float x, y, z;
     	Matrix3 rot;
     	
         switch(e.getKeyCode()) {  
         //Delivery keys
   		case KeyEvent.VK_UP:						// Up arrow pressed, camera moves forward 
   			dir = -1;
   			rads = Math.toRadians(camera.getRotY()); 
   			dz = dir*Math.cos(rads)*speed;
   			dx = dir*Math.sin(rads)*speed;
   			translate((float) dx, 0, (float) dz);
   			camera.translate((float) dx, 0, (float)dz); 
   			
   			// for person 
   			/*
   			x  = camera.getPosition().getX() + (float) dx + personVect.getX();
   			y = getPosition().getY();
   			z  = camera.getPosition().getZ() + (float) dz + personVect.getY();
   			setPosition(x,y,z);
   			*/
   
   			break;
   			
   		case KeyEvent.VK_DOWN:						// Down arrow pressed, camera moves backwards
   			dir = 1;
   			rads = Math.toRadians(camera.getRotY());
   			dz = dir*Math.cos(rads)*speed;
   			dx = dir*Math.sin(rads)*speed;
   			camera.translate((float) dx, 0, (float)dz); 
   			/*
   			// for camera
   			x  = camera.getPosition().getX() + (float) dx + personVect.getX();
   			y = getPosition().getY();
   			z  = camera.getPosition().getZ() + (float) dz + personVect.getY();
   			setPosition(x,y,z);
*/
   			break;
   			
   		case KeyEvent.VK_LEFT:
   			dir = 1;
   			rotShift = dir*5;
   			camera.rotateY(rotShift);
   			/*
   	  		// for person  
   			rot = Matrix3.rotation(-rotShift); // get rotation matrix 
   			personVect = rot.multiply(personVect); // rotate vector 
   			
   			x  = camera.getPosition().getX() + personVect.getX();
   			y = camera.getPosition().getY();
   			z  = camera.getPosition().getZ() + personVect.getY();
   			camera.setPosition(x,y,z);
   			*/
         	break;
         	
   		case KeyEvent.VK_RIGHT:
   			dir = -1;
   			rotShift = dir*5;
   			camera.rotateY(rotShift);
   			
   			//for person 
   			rot = Matrix3.rotation(-rotShift); // get rotation matrix 
   			personVect = rot.multiply(personVect); // rotate vector 
   			/*
   			x  = getPosition().getX() + personVect.getX();
   			y = getPosition().getY();
   			z  = getPosition().getZ() + personVect.getY();
   			setPosition(x,y,z);
   			*/
   			
         	break;
         }
         frame = CoordFrame3D.identity()
      			.translate(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
      					camera.getPosition().getY() -yOff,
      					camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())))
      			.scale(getScale());
         setPosition(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
					camera.getPosition().getY() -yOff,
					camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())));
         //Make sure we stay in bound
         
         // if person changed altitudes, make camera change altitudes
        // verticalMove();
    }
    public boolean checkTerrainBounds(float x, float z) {
    	int width = getTerrain().getWidth();
    	int depth = getTerrain().getDepth();
    	 if (x >= width || x < 0 || z >= depth || z < 0) { // index out of bounds 
         	return false;
         }
    	return true; // index within terrain bounds 
    }

    public void setHeight(float x, float z, boolean avatar) {
    	int w = terrain.getWidth();
    	int d = terrain.getDepth();
    	float y = 0;

    	if (x >= w || x < 0 || z >= d || z < 0) { } // index out of bounds 
    	else {
    		
    		y = terrain.altitude(x, z);
    	}

    	camera.setPosition(camera.getPosition().getX(),
    			y+yOff,
    			camera.getPosition().getZ());
    	
    }
    
    
    //public void key 
	@Override
	public void keyReleased(KeyEvent e) {
		
		
		switch(e.getKeyCode()) {
    	case KeyEvent.VK_R:
    		if(fpsMode && !e.isAutoRepeat()) {
    			fpsMode = false;
       		} else if(!e.isAutoRepeat()) {
    			fpsMode = true;
    			Point3D pos = this.getGlobalPosition();
    			camera.setPosition(pos);
    			//camera.setYOff(this.getYOff());
    		}
    	}
	}
	
    public boolean isFps() {
    	return fpsMode;
    }
 
    public boolean getTeleportet() {
    	return tp;
    }
    public void setTeleportet(boolean tf) {
    	tp =tf;
    }
    
    public Terrain getTerrain() {
    	return this.terrain;
    }
    
    public void setTerrain(Terrain terrain) {
    	this.terrain = terrain;
    }

    public void setYOff(float yOff) {
    	this.yOff = yOff;
    }
    
    public float getYOff() {
    	return this.yOff;
    }
    public Camera getCamera() {
    	return camera;
    }
    public void setView(GL3 gl) {
    	camera.setView(gl);
    }
    public void verticalMove() {
    	// get heights in world coords
    	float personHeight = getGlobalPosition().getY() - getYOff();
    	float camHeight = camera.getGlobalPosition().getY() - camera.getYOff();
    	
    	float heightDiff = personHeight - camHeight; 
    	
    	if (heightDiff != 0) {
    		camera.translate(0, heightDiff, 0);
    		//translate(0, heightDiff, 0);
    	}
    }
    public Vector3 personVector() { // calculate the difference in x and z values for camera and person 
    	Point3D personPos = getPosition();
    	Point3D camPos = camera.getPosition();
    	
    	float dx = personPos.getX() - camPos.getX();
    	//float dy = 0;
    	float dz = personPos.getZ() - camPos.getZ();
    	
    	Vector3 personVect = new Vector3(dx, dz, 1); // xz plane only 
    	
    	return personVect;
    }
    public void setHeight(float yOff, float avatar) {
    	float y = getTerrain().altitude(getPosition().getX(),getPosition().getZ() );
        setPosition(new Point3D(getPosition().getX(), y+ yOff, getPosition().getZ()));  
    }
    
    public void setHeight(float x, float yOff, float z, boolean avatar) {
    	int w = getTerrain().getWidth();
    	int d = getTerrain().getDepth();
    	float y = 0;
    	
    	if (x >= w || x < 0 || z >= d || z < 0) { } // index out of bounds 
    	else { y = getTerrain().altitude(x, z); }
    	
    	setPosition(getPosition().getX(), y+yOff, getPosition().getZ());
    }
}

