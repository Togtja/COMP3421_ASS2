package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip3D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;




public class Camera implements  KeyListener {

    private Point3D myPos; //Should maybe point 3D
    private float myPosX;
    private float myPosY;
    private float myPosZ;
    private float myRotX;
    private float myRotY;
    private float myRotZ; 
    private float myScale; 
    private float speed = 0.3f;   
    
    private CoordFrame3D local;
    private CoordFrame3D viewFrame;
    //private CoordFrame3D fps;
    private TriangleMesh terrain;
    private CoordFrame3D tps;
    private boolean fpsMode = true;
    
    private int stepsX; 
    private int stepsZ;
    
    
    public Camera() {
    	//Some default Values
    	myPosX = 0; myPosY = -0.5f; myPosZ = -9f;		// initialize position
    	setMyPos();
    	myRotX = 0; myRotY = 0; myRotZ = 0; 	// initialize rotation
        myScale = 1;								// initialize scale 
        viewFrame = CoordFrame3D.identity();	// initialize view frame 
        //fps = CoordFrame3D.identity().translate(myPos);	
    }
    
    public Camera(TriangleMesh terrainMesh) {
    	//Some default Values
    	/*myPos = new Point3D(0,0.5f,4.5f);
    	myAngleX = 0; myAngleY = 0;
    	myAngleZ = 0; 
        myScale = 1;*/
        viewFrame = CoordFrame3D.identity();
        terrain = terrainMesh;
        stepsX = 0; 
        stepsZ = 0;
    }
    
    
    
    /**
     * Set the view transform
     * 
     * Note: this is the inverse of the model transform above
     * 
     * @param gl
     */
    public void setView(GL3 gl) { 
        viewFrame = CoordFrame3D.identity()
                .translate(myPosX,myPosY,myPosZ);       // .translate(0, -0.5f, -4.5f);
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }
    public void setView(CoordFrame3D frame) { 
        viewFrame = frame;       // .translate(0, -0.5f, -4.5f);
        //Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }
    
    public CoordFrame3D getView() {
        return viewFrame;
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
    public void keyPressed(KeyEvent e) {
        double rad;
        float xtrans, ytrans, ztrans;
        double xrotrad, zrotrad; 
        
        switch(e.getKeyCode()) {
        
        // A key pressed 
        case KeyEvent.VK_A:      					
            if (e.isShiftDown()) { // rotate in negative y direction 
            	viewFrame = viewFrame.rotateY(-2);

            	// keep track of rotation
            	myRotZ -= 2;
            	if (myRotZ < -360) myRotZ += 360;	// normalise angle 
            } else { // translate left 

            	rad = (myRotZ * Math.PI / 180);
            	xtrans = (float) (-1 * Math.sin(rad) * speed);
            	ztrans = (float) (-1 * Math.cos(rad) * speed); 
            	viewFrame = viewFrame.translate(xtrans, 0f, ztrans);
            }
            break;
        
        // D key pressed
        case KeyEvent.VK_D:							 
            if (e.isShiftDown()) { // rotate in positive y direction 
            	viewFrame = viewFrame.rotateY(5);
            	// keep track of angle 
            	myRotZ += 1;
            	if (myRotZ > 360) myRotZ -= 360;	// normalise angle 
            } else {	// translate right       

            	rad = (myRotZ / 180 * Math.PI);
            	xtrans = (float) (1 * Math.sin(rad) * speed);
            	ztrans = (float) (1 * Math.cos(rad) * speed); 
            	viewFrame = viewFrame.translate(xtrans, 0, ztrans); 
            }
            break;

        // S key pressed 
        case KeyEvent.VK_S:							
            if (e.isShiftDown()) { // rotate in negative x direction 
            	viewFrame = viewFrame.rotateX(-5);
            	// keep track of angle 
                myRotX -= 1;
                if (myRotX < -360) myRotX += 360; // normalise angle 

            } 
            else { 	// move backwards 
            	rad = (myRotZ * Math.PI / 180);
      			xtrans = (float) (1 * Math.sin(rad) * speed);
            	ztrans = (float) (-1 * Math.cos(rad) * speed);       
            	viewFrame = viewFrame.translate(xtrans, 0f, ztrans);  
            }
            break;
            
        // W key pressed
        case KeyEvent.VK_W:							 
            if (e.isShiftDown()) { // rotate in positive x direction 
            	viewFrame = viewFrame.rotateX(5);
            	// keep track of angle 
            	myRotX += 1;
                if (myRotX > 360) myRotX -= 360; // normalise angle 

            } 
            else {	// move forwards 
            	rad = (myRotZ * Math.PI / 180);
      			xtrans = (float) (-1 * Math.sin(rad) * speed);
            	ztrans = (float) (1 * Math.cos(rad) * speed); 
            	viewFrame = viewFrame.translate(xtrans, 0f, ztrans); 
            }
            break;
            
        // Q key pressed 
        case KeyEvent.VK_Q:							
        	 if (e.isShiftDown()) { // decrease scale 
        		 viewFrame = viewFrame.scale(1/1.1f, 1/1.1f, 1/1.1f);
        	 }
             else { // rotate in negative z direction 
            	 viewFrame = viewFrame.rotateZ(-2);
            	 // keep track of rotation
            	 myRotY -= 2; 
            	 if (myRotY < -360) myRotY += 360;
             }
        	 break;
        	 
        // E key pressed 
      	case KeyEvent.VK_E:	
      		if (e.isShiftDown()) { // increase scale 
      			viewFrame = viewFrame.scale(1.1f, 1.1f, 1.1f); //myScale /= 1.1;
      		} 
      		else { // rotate in positive z direction 
      			viewFrame = viewFrame.rotateZ(2); 
      			// keep track of rotation
      			myRotY += 2;
      			if (myRotY > 360) myRotY -= 360;
      		}
      		break;

        //Delivery keys
  		case KeyEvent.VK_UP:						// Up arrow pressed, camera moves forward 

  			rad = (myRotZ * Math.PI / 180);
  			xtrans = (float) (-1 * Math.sin(rad) * speed);
        	ztrans = (float) (1 * Math.cos(rad) * speed); 
        	viewFrame = viewFrame.translate(xtrans, 0f, ztrans);   
  			break;
  		case KeyEvent.VK_DOWN:						// Down arrow pressed, camera moves backwards
  			rad = (myRotZ * Math.PI / 180);
  			xtrans = (float) (1 * Math.sin(rad) * speed);
        	ztrans = (float) (-1 * Math.cos(rad) * speed);       
        	viewFrame = viewFrame.translate(xtrans, 0f, ztrans); 
  			break;
  		case KeyEvent.VK_LEFT:						// Left arrow pressed, camera moves left 
  			viewFrame = viewFrame.rotateY(-2);
  			// keep track of rotation 
  			myRotZ -= 2; 
  			if (myRotZ < -360) myRotZ += 360;
  			break;
  		case KeyEvent.VK_RIGHT:						// Right arrow pressed, camera moves right 
  			viewFrame = viewFrame.rotateY(2);
  			// keep track of rotation
  			myRotZ += 2; 
  			if (myRotZ > 360) myRotZ -= 360;
  			break;
       }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	
    }
    
    public void setTerrain(TriangleMesh terrain) {
    	this.terrain = terrain;
    }
    
    public TriangleMesh getTerrain() {
    	return terrain;
    }
    

    
    private void setMyPos() {
    	myPos = new Point3D(myPosX, myPosY, myPosZ);
    }
}
