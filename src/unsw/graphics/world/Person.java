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
import unsw.graphics.geometry.Point2D;
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
    Point3D oldCamPos;
    CoordFrame3D frame = CoordFrame3D.identity() ;
    //private WorldObject parent;
    //private Terrain terrain; 
    private float yOff = 1.65f;
    
    public Person(WorldObject parent, Terrain t) throws IOException {
    	super(parent);
    	//this.parent = parent;
    	this.setPosition(parent.getPosition());
    	this.scale(5);
    	this.rotateY(50f);
        model = new TriangleMesh("res/models/bunny.ply", true, true);
        camera = new Camera(parent);
        terrain = t;
        fpsMode = false; //true; 
        setPosition(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
					camera.getPosition().getY() -yOff,
					camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())));
        frame = CoordFrame3D.identity()
     			.translate(getPosition())
     			.scale(getScale())
     			.rotateY(getRotY());
        oldCamPos = camera.getGlobalPosition();
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

    Point2D dis2point(Point3D p1, Point3D p2, float rot){
    	float d  = (float) Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2)
					+ Math.pow(p1.getZ() - p2.getZ(), 2));
			//subtract that distance from camera
  			float rads = (float) Math.toRadians(rot);
  			float dz = (float) (d*Math.cos(rads));
  			float dx = (float) (d*Math.sin(rads)); 
  			return new Point2D(dx, dz);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	Point3D currPos;
    	if(fpsMode) {
    		currPos = camera.getGlobalPosition();
    	}
    	else {
    		currPos = getGlobalPosition();
    	}
    	
    	
        
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
   			translate((float) dx, 0, (float) dz);
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

   	  		// for person  
   			if(!fpsMode) {
   				//Distance between camera and person
   	 			float d  = (float) Math.sqrt(Math.pow(camera.getGlobalPosition().getX() - getGlobalPosition().getX(), 2)
   	 					+ Math.pow(camera.getGlobalPosition().getZ() - getGlobalPosition().getZ(), 2));
   				//subtract that distance from camera
   	   			rads = Math.toRadians(camera.getRotY());
   	   			dz = -d*Math.cos(rads);
   	   			dx = -d*Math.sin(rads); 
   	   			//rotate
   	   			camera.rotateY(rotShift);
   	   			//add that distance back again
   	   			rads = Math.toRadians(camera.getRotY());
   	   			dz = dz + d*Math.cos(rads);
   	   			dx = dx +  d*Math.sin(rads);
   	   			camera.translate((float) dx, 0, (float)dz); 
   				//Rotate the person/avatar
   	   			rotateY(rotShift);
   			}
   			else {
   	   			camera.rotateY(rotShift);
   	   			rotateY(rotShift);
   			}
   			
         	break;
         	
   		case KeyEvent.VK_RIGHT:
   			dir = -1;
   			rotShift = dir*5;
   			if(!fpsMode) {
    			//Distance between camera and person
   	 			float d  = (float) Math.sqrt(Math.pow(camera.getGlobalPosition().getX() - getGlobalPosition().getX(), 2)
   	 					+ Math.pow(camera.getGlobalPosition().getZ() - getGlobalPosition().getZ(), 2));
   				//subtract that distance from camera
   	   			rads = Math.toRadians(camera.getRotY());
   	   			dz = -d*Math.cos(rads);
   	   			dx = -d*Math.sin(rads); 
   	   			//rotate
   	   			camera.rotateY(rotShift);
   	   			//add that distance back again
   	   			rads = Math.toRadians(camera.getRotY());
   	   			dz = dz + d*Math.cos(rads);
   	   			dx = dx +  d*Math.sin(rads);
   	   			camera.translate((float) dx, 0, (float)dz); 
   				//Rotate the person/avatar
   	   			rotateY(rotShift);
   			
   			}
   			else {
   	   			camera.rotateY(rotShift);
   	   			rotateY(rotShift);
   			}
         	break;
         }
         oldCamPos = new Point3D(oldCamPos.getX() + (float)dz*2, oldCamPos.getY(), oldCamPos.getZ() + (float)dz*2);
         frame = CoordFrame3D.identity()
       			.translate(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
       					camera.getPosition().getY() -yOff,
       					camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())))
       			.scale(getScale())
       			.rotateY(getRotY());
          setPosition(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
 					camera.getPosition().getY() -yOff,
 					camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())));
         //Make sure we stay in bound
         setHeight(currPos.getX(), currPos.getZ(), isFps());
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
    //y only to return where it was if out of bounds
    public float getTerrainY(float x,float y, float z) {
    	int w = terrain.getWidth();
    	int d = terrain.getDepth();
    	//float y = 0;

    	if (x >= w || x < 0 || z >= d || z < 0) {return y; } // index out of bounds 
    	else {
    		y = terrain.altitude(x, z);
    	}
    	return y +yOff;
    }
    public float getTerrainY(Point3D p) {
    	return getTerrainY(p.getX(),p.getY(), p.getZ());
    }
    
    
    //public void key 
	@Override
	public void keyReleased(KeyEvent e) {
		
		
		if (e.getKeyCode() == KeyEvent.VK_R) {
    		if(fpsMode && !e.isAutoRepeat()) {
    			fpsMode = false;
    			camera.setPosition(oldCamPos);
       		} 
    		else if(!e.isAutoRepeat()) {
    			fpsMode = true;
    			oldCamPos = camera.getGlobalPosition();
    			Point3D pos = this.getGlobalPosition();
    			camera.setPosition(pos.getX(), pos.getY() + yOff, pos.getZ());
    			//camera.setYOff(yOff + 32);
    		}

    	    camera.setPosition(camera.getPosition().getX(),
    	    		getTerrainY(camera.getPosition()),
    	    				camera.getPosition().getZ());
    	    setPosition(getPosition().getX(),
    	    		getTerrainY(getPosition()),
    	    				getPosition().getZ());
    	    
    		frame = CoordFrame3D.identity()
    	      			.translate(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
    	      					camera.getPosition().getY() -yOff,
    	      					camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())))
    	      			.scale(getScale())
    	      			.rotateY(getRotY());
    	    setPosition(camera.getPosition().getX() - 5*(float)Math.sin(Math.toRadians(camera.getRotY())),
    						camera.getPosition().getY() -yOff,
    						camera.getPosition().getZ() - 5*(float)Math.cos(Math.toRadians(camera.getRotY())));

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
    
    public void setHeight(float x, float yOff, float z, boolean avatar) {
    	int w = getTerrain().getWidth();
    	int d = getTerrain().getDepth();
    	float y = 0;
    	
    	if (x >= w || x < 0 || z >= d || z < 0) { } // index out of bounds 
    	else { y = getTerrain().altitude(x, z); }
    	
    	setPosition(getPosition().getX(), y+yOff, getPosition().getZ());
    }
}

