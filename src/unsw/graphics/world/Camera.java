package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.scene.MathUtil;

public class Camera implements  KeyListener {
    // the local transformation
    private CoordFrame3D viewFrame;
    private Terrain terrain;
    private Point3D myTranslation;
    private float myRotation; //normalised to the range [-180..180)
    private float myScale;
    private float myAspectRatio;
    private float xPos;
    private float zPos;
    private float yPos;
    

    public Camera() {
    	viewFrame = CoordFrame3D.identity();
    	myRotation = 180;
        myScale = 1;
        myTranslation = new Point3D(0,0,0);
        myAspectRatio = 1;
        xPos = 0; yPos = 0; zPos = 0;
    }
    
    public Camera(Terrain t) {
        viewFrame = CoordFrame3D.identity();
        myRotation = 180;
        myScale = 1;
        myTranslation = new Point3D(0,0,0);
        myAspectRatio = 1;
        terrain = t;
        xPos = 0; yPos = 0; zPos = 0; 
    }

    public void setRotation(float rotation) {
        myRotation = MathUtil.normaliseAngle(rotation);
    }
    public float getRotation() {
        return myRotation;
    }
    
    public float getScale() {
        return myScale;
    }
    
    public void setScale(float scale) {
        myScale = scale;
    }
    
    public Point3D getPosition() {
        return myTranslation;
    }
    
    public void setPosition(float x, float z) {
    	float y = terrain.altitude(x, z);
        setPosition(new Point3D(x,y,z));
    }
    
    public void setPosition(Point3D p) {
        myTranslation = p;
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
    
    public void setView(CoordFrame3D frame) { 
        viewFrame = frame;     
    }
    
    public void setView(GL3 gl) {
        computeView(); // compute a view transform to account for the cameras aspect ratio and further transformations for camera
        Shader.setViewMatrix(gl, viewFrame.getMatrix()); // set the view matrix to the computed transform
    }
    
    public float getAspectRatio() {
        return myAspectRatio;
    }
    
    public void translate(float dx, float dz) {
    	float dy;
    	//float yPrev = yPos;
        xPos += dx;
        zPos += dz;
/*
        System.out.println("x position: " + xPos);
        System.out.println("z position: " + zPos);
    	if (checkTerrainBounds(-xPos, -zPos) == true) {
            yPos = terrain.altitude(-xPos, -zPos);
            System.out.println("y position: " + yPos);
    		dy = yPos - yPrev; 
    	} else { */
    	dy = 0;
    	//}
    	myTranslation = myTranslation.translate(dx, dy, dz);
    }
    
    public void scale(float factor) {
        myScale *= factor;
    }
    
    public void rotate(float angle) {
        myRotation += angle;
        myRotation = MathUtil.normaliseAngle(myRotation);
    }
    
    public void computeView() {
    	viewFrame = CoordFrame3D.identity()
    			.scale(1/getAspectRatio(), 1, 1)     	// compute view transform for dimensions of screen
    			//further transformations to account for the camera's global position, rotation and scale
                .scale(1/getScale(), 1/getScale(), 1/getScale())
                .rotateY(-1*getRotation())
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
        	dir = 1;
  			rads = Math.toRadians(myRotation); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = 1*0.3f; // dir*Math.sin(rads)*0.3f;
  			translate((float) dx, (float) dz);
        	break;
        
        case KeyEvent.VK_A:							// A key pressed, step left 
        	dir = -1;
  			rads = Math.toRadians(myRotation); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*0.3f; // dir*Math.sin(rads)*0.3f;
  			translate((float) dx, (float) dz);
        	break;
        
        
        //Delivery keys
  		case KeyEvent.VK_UP:						// Up arrow pressed, camera moves forward 
  			dir = -1;
  			rads = Math.toRadians(myRotation); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translate((float) dx, (float) dz);
  			break;
  			
  		case KeyEvent.VK_DOWN:						// Down arrow pressed, camera moves backwards
  			dir = 1;
  			rads = Math.toRadians(myRotation);
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translate((float) dx, (float) dz);
  			break;
  			
  		case KeyEvent.VK_LEFT:
  			dir = 1;
  			rotShift = dir*1;
  			rotate(rotShift);
        	break;
        	
  		case KeyEvent.VK_RIGHT:
  			dir = -1;
  			rotShift = dir*1;
  			rotate(rotShift);
        	break;
        }
        
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
