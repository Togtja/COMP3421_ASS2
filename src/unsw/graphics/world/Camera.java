package unsw.graphics.world;


import java.io.IOException;
import java.util.List;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix3;
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
    
    private Person person;
    
   private Vector3 personVect;
   private float yOff = 1.5f;

    public Camera(WorldObject parent) throws IOException {
    	super(parent);
    	viewFrame = CoordFrame3D.identity();
    	//setRotY(180);
        myAspectRatio = 1;        
        person = new Person(parent);
        
        personVect = personVector();
    }
    
    public Camera(Terrain t, WorldObject parent) throws IOException {
    	super(parent);
        viewFrame = CoordFrame3D.identity();
        setRotY(230);
        setPosition(new Point3D(-4f, yOff, -4f));
        myAspectRatio = 1;
        terrain = t;
        person = new Person(parent);
        
        personVect = personVector();
    }
    
    public Vector3 personVector() { // calculate the difference in x and z values for camera and person 
    	Point3D personPos = person.getPosition();
    	Point3D camPos = this.getPosition();
    	
    	float dx = personPos.getX() - camPos.getX();
    	//float dy = 0;
    	float dz = personPos.getZ() - camPos.getZ();
    	
    	Vector3 personVect = new Vector3(dx, dz, 1); // xz plane only 
    	
    	return personVect;
    }
    
    
    
    public void setView(GL3 gl) {
    	// compute view transform for dimensions of screen, further transformations to account for camera's global position, rotation, and scale 
    	CoordFrame3D viewFrame = CoordFrame3D.identity()
    			.scale(1/getAspectRatio(), 1, 1)
                .scale(1/getScale(), 1/getScale(), 1/getScale())
                .rotateY(-1*getRotY()).rotateX(-1*getRotX()).rotateZ(-1*getRotZ())
                .translate(-1*getPosition().getX(), -1*getPosition().getY(), -1*getPosition().getZ());

        // TODO set the view matrix to the computed transform
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
        
        // update children 
        //draw(gl,viewFrame);
        draw(gl,getModel());

    }
    
    public CoordFrame3D getModel() {
    	CoordFrame3D model = CoordFrame3D.identity()
    			.translate(getGlobalPosition())
    			.rotateX(getGlobalRotX()).rotateY(getGlobalRotY()).rotateZ(getGlobalRotZ())
    			.scale(getGlobalScale(), getGlobalScale(), getGlobalScale());
    	
    	return model;
    }
    
    
    
    public void setPosition(float x, float z) {
    	float y = terrain.altitude(x, z);
        setPosition(new Point3D(x,y,z));
    }    
    
    
    //Used to set height for an avatar infront of you
    public void setHeight(float yOff, float avatar) {
    	float y = terrain.altitude(getPosition().getX(),getPosition().getZ() );
        setPosition(new Point3D(getPosition().getX(), y+ yOff, getPosition().getZ()));  
    }
    
    public void setHeight(float x, float yOff, float z, boolean avatar) {
    	int w = terrain.getWidth();
    	int d = terrain.getDepth();
    	float y = 0;
    	
    	if (x >= w || x < 0 || z >= d || z < 0) { } // index out of bounds 
    	else { y = terrain.altitude(x, z); }
    	
    	setPosition(getPosition().getX(), y+yOff, getPosition().getZ());
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
    
    
    
    //How to debug control
    // w moves you closer aka +z
    // s move you further away aka -z
    // a moves you to the left aka -x
    // d moves you to the left aka +x
    // q/e rotates with z
    // shift + w/s rotates x 
    // shift + a/d rotates y
    // shift + q/e scales uniformly

    public void keyPressed(KeyEvent e) {
        
    	double rads = 0; 
    	double dx = 0, dy = 0, dz = 0; 
    	float rotShift = 0;
    	float dir = 0;
    	float speed = 0.3f;
    	
    	float x, y, z;
    	Matrix3 rot;
    	
        switch(e.getKeyCode()) {  
        case KeyEvent.VK_D:							// D key pressed, camera step right 
        	dir = -1;
  			rads = Math.toRadians(getRotY()); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*0.3f; // dir*Math.sin(rads)*0.3f;
  			setPosition(getPosition().translate((float) dx, 0, (float)dz)); 
        	break;
        
        case KeyEvent.VK_A:							// A key pressed, step left 
        	dir = 1;
  			rads = Math.toRadians(getRotY()); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*0.3f; // dir*Math.sin(rads)*0.3f;
  			setPosition(getPosition().translate((float) dx, 0, (float)dz)); 
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
        		translate(0, (float) dy, 0);
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
  				translate(0, (float) dy, 0);
        	}
        	break;
        
        
        //Delivery keys
  		case KeyEvent.VK_UP:						// Up arrow pressed, camera moves forward 
  			dir = -1;
  			rads = Math.toRadians(getRotY()); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translate((float) dx, 0, (float)dz); 
  			
  			// for person 
  			x  = this.getPosition().getX() + (float) dx + personVect.getX();
  			y = person.getPosition().getY();
  			z  = this.getPosition().getZ() + (float) dz + personVect.getY();
  			person.setPosition(x,y,z);
  			break;
  			
  		case KeyEvent.VK_DOWN:						// Down arrow pressed, camera moves backwards
  			dir = 1;
  			rads = Math.toRadians(getRotY());
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translate((float) dx, 0, (float)dz); 
  			
  			// for person 
  			x  = this.getPosition().getX() + (float) dx + personVect.getX();
  			y = person.getPosition().getY();
  			z  = this.getPosition().getZ() + (float) dz + personVect.getY();
  			person.setPosition(x,y,z);

  			break;
  			
  		case KeyEvent.VK_LEFT:
  			dir = 1;
  			rotShift = dir*5;
  			rotateY(rotShift);
  			
  	  		// for person 
  			rot = Matrix3.rotation(-rotShift); // get rotation matrix 
  			personVect = rot.multiply(personVect); // rotate vector 
  			
  			x  = this.getPosition().getX() + personVect.getX();
  			y = person.getPosition().getY();
  			z  = this.getPosition().getZ() + personVect.getY();
  			person.setPosition(x,y,z);
  			//person.rotateY(-rotShift);
        	break;
        	
  		case KeyEvent.VK_RIGHT:
  			dir = -1;
  			rotShift = dir*5;
  			rotateY(rotShift);
  			
  			// for person 
  			rot = Matrix3.rotation(-rotShift); // get rotation matrix 
  			personVect = rot.multiply(personVect); // rotate vector 
  			
  			x  = this.getPosition().getX() + personVect.getX();
  			y = person.getPosition().getY();
  			z  = this.getPosition().getZ() + personVect.getY();
  			person.setPosition(x,y,z);
  			//person.rotateY(-rotShift);
  			
        	break;
        }
        //Make sure we stay in bound
        
        // if person changed altitudes, make camera change altitudes
        verticalMove();
    }
    
    
    public void verticalMove() {
    	// get heights in world coords
    	float personHeight = person.getGlobalPosition().getY() - person.getYOff();
    	float camHeight = this.getGlobalPosition().getY() - this.getYOff();
    	
    	float heightDiff = personHeight - camHeight; 
    	
    	if (heightDiff != 0) {
    		translate(0, heightDiff, 0);
    	}
    }
    
    
    
    @Override
    public void keyReleased(KeyEvent e) {
    	
    }
    
    public void drawAvatar(GL3 gl) {
    	person.init(gl);
    	person.drawPerson(gl);
    }
    
    public void setPerson(Person person) {
    	this.person = person;
    }
    
    
    public Person getPerson() {
    	return this.person;
    }
    
    public void setYOff(float yOff) {
    	this.yOff = yOff;
    }
    
    public float getYOff() {
    	return this.yOff;
    }


}

