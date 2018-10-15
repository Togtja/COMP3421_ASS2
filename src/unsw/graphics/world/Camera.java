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
    private float myRotX;
    private float myRotY; 
    private float myRotZ;
    
    private float myScale;
    private float myAspectRatio;
    private float xPos;
    private float zPos;
    private float yPos;
    

    public Camera() {
    	viewFrame = CoordFrame3D.identity();
    	myRotX = 0;
    	myRotY = 180;
    	myRotZ = 0;
        myScale = 1;
        myTranslation = new Point3D(0,0,0);
        myAspectRatio = 1;
        xPos = 0; yPos = 0; zPos = 0;
    }
    
    public Camera(Terrain t) {
        viewFrame = CoordFrame3D.identity();
        myRotX = 0;
    	myRotY = 180;
    	myRotZ = 0;
        myScale = 1;
        xPos = 0; yPos = 0; zPos = 0; 
        myTranslation = new Point3D(xPos,yPos,zPos);
        myAspectRatio = 1;
        terrain = t;
        
    }

    public void setRotX(float rotation) {
        myRotX = MathUtil.normaliseAngle(rotation);
    }
    public float getRotX() {
        return myRotX;
    }
    public void setRotY(float rotation) {
        myRotY = MathUtil.normaliseAngle(rotation);
    }
    public float getRotY() {
        return myRotY;
    }
    public void setRotZ(float rotation) {
        myRotZ = MathUtil.normaliseAngle(rotation);
    }
    public float getRotZ() {
        return myRotZ;
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
    public void setHeight(float offSet) {
    	float y = terrain.altitude(myTranslation.getX(), myTranslation.getZ());
        setPosition(new Point3D(myTranslation.getX(),y + offSet,myTranslation.getZ()));
        computeView();
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
    
    public void translateXZ(float dx, float dz) {
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
    
    public void translateY(float dy) {
    	yPos += dy;
    	myTranslation = myTranslation.translate(0, dy, 0);
    }
    
    public void scale(float factor) {
        myScale *= factor;
    }
    
    public void rotateX(float angle) {
        myRotX += angle;
        myRotX = MathUtil.normaliseAngle(myRotX);
    }
    public void rotateY(float angle) {
        myRotY += angle;
        myRotY = MathUtil.normaliseAngle(myRotY);
    }
    public void rotateZ(float angle) {
        myRotZ += angle;
        myRotZ = MathUtil.normaliseAngle(myRotZ);
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
        	dir = 1;
  			rads = Math.toRadians(myRotY); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = 1*0.3f; // dir*Math.sin(rads)*0.3f;
  			translateXZ((float) dx, (float) dz);
        	break;
        
        case KeyEvent.VK_A:							// A key pressed, step left 
        	dir = -1;
  			rads = Math.toRadians(myRotY); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*0.3f; // dir*Math.sin(rads)*0.3f;
  			translateXZ((float) dx, (float) dz);
        	break;
        	
        case KeyEvent.VK_W: // rotate camera up around x/z axis 
        	/*dir = 1;
  			rotShift = dir*1;
  			rotateZ(rotShift);
  			rotateX(rotShift);*/
        	
        	dir = 1;
  			rads = Math.toRadians(myRotZ); 
  			dy = dir*Math.cos(rads)*speed;
  			//dx = dir*Math.sin(rads)*speed;
  			translateY((float) dy);
        	break;
        	
        case KeyEvent.VK_S: // rotate camera down around x/z axis 
        	/*dir = -1;
  			rotShift = dir*1;
  			rotateZ(rotShift);
  			rotateX(rotShift);
  			*/
  			dir = -1;
  			rads = Math.toRadians(myRotZ); 
  			dy = dir*Math.cos(rads)*speed;
  			//dx = dir*Math.sin(rads)*speed;
  			translateY((float) dy);
        	break;
        
        
        //Delivery keys
  		case KeyEvent.VK_UP:						// Up arrow pressed, camera moves forward 
  			dir = -1;
  			rads = Math.toRadians(myRotY); 
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translateXZ((float) dx, (float) dz);
  			break;
  			
  		case KeyEvent.VK_DOWN:						// Down arrow pressed, camera moves backwards
  			dir = 1;
  			rads = Math.toRadians(myRotY);
  			dz = dir*Math.cos(rads)*speed;
  			dx = dir*Math.sin(rads)*speed;
  			translateXZ((float) dx, (float) dz);
  			break;
  			
  		case KeyEvent.VK_LEFT:
  			dir = 1;
  			rotShift = dir*1;
  			rotateY(rotShift);
        	break;
        	
  		case KeyEvent.VK_RIGHT:
  			dir = -1;
  			rotShift = dir*1;
  			rotateY(rotShift);
        	break;
        }
        computeView();
        float offSet = 1;
    	if(this.getPosition().getX() > 0 
    			&& this.getPosition().getX() < terrain.getWidth()) {
      	  if (this.getPosition().getZ() > 0
      			  && this.getPosition().getZ() < terrain.getDepth()) {
      		//Some of-setting needs to be done
      		  this.setHeight(offSet);
      		  
      	  }
    	}
    	System.out.println("Camera pos (" + this.getPosition().getX() + ", "
				  + this.getPosition().getY() + ", " + this.getPosition().getZ() + ")\n");
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
