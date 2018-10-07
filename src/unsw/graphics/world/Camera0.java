// going to screw around with the camera, this is a copy of a old working camera 


package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip3D;
import unsw.graphics.geometry.Point3D;




public class Camera0 implements  KeyListener {

    private Point3D myPos; //Should maybe point 3D
    private float myAngleX;
    private float myAngleY;
    private float myAngleZ; 
    private float myScale; 
    private CoordFrame3D viewFrame;
    private CoordFrame3D fps;

    public Camera0() {
    	//Some default Values
    	myPos = new Point3D(0,0.5f,4.5f);
    	myAngleX = 0; myAngleY = 0;
    	myAngleZ = 0; 
        myScale = 1;
        viewFrame = CoordFrame3D.identity();
        
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
                .translate(new Point3D(0,-0.5f,-9f));       // .translate(0, -0.5f, -4.5f);
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }
    
    public CoordFrame3D getView() {
        return viewFrame;
    }
    public void firstPerson(GL3 gl, CoordFrame3D frame) {
    	fps = frame;
        //Shader.setViewMatrix(gl, frame.getMatrix());
    }
    public CoordFrame3D getfps() {
        return fps;
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
        switch(e.getKeyCode()) {
        case KeyEvent.VK_A:
            if (e.isShiftDown())
            	fps = fps.rotateY(-5);
            else
            	fps = fps.translate(0.1f,0,0);
            break;
            
        case KeyEvent.VK_D:
            if (e.isShiftDown()) 
            	fps = fps.rotateY(5);
            else
            	fps = fps.translate(-0.1f,0,0);               
            break;

        case KeyEvent.VK_S:
            if (e.isShiftDown())
            	fps = fps.rotateX(-5);
            else
            	fps = fps.translate(0,0,-0.1f);           
            break;

        case KeyEvent.VK_W:
            if (e.isShiftDown())
            	fps = fps.rotateX(5);
            else
            	fps = fps.translate(0,0,0.1f);
            break;
        case KeyEvent.VK_Q:
        	 if (e.isShiftDown())
        		 myScale *= 1.1;
             else
            	 fps = fps.rotateZ(-5);
        	 break;
      	case KeyEvent.VK_E:
      		if (e.isShiftDown())
      			myScale /= 1.1;
      		else
      			fps = fps.rotateZ(5);
      		break;
        
        //Delivery keys
  		case KeyEvent.VK_UP:
  			fps = fps.translate(0,0,0.1f);
  			break;
  		case KeyEvent.VK_DOWN:
  			fps = fps.translate(0,0,-0.1f);
  			break;
  		case KeyEvent.VK_LEFT:
  			fps = fps.rotateY(-5);
  			break;
  		case KeyEvent.VK_RIGHT:
  			fps = fps.rotateY(5);
  			break;
       }

    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyPressed0(KeyEvent e) {
    	double xpos = 0, ypos = 0, zpos = 0, xrot = 0, yrot = 0, zrot = 0, angle = 0.0 ;
        double xrotrad, zrotrad;
        
        switch(e.getKeyCode()) {
        
        // A key pressed 
        case KeyEvent.VK_A:      					
            if (e.isShiftDown()) { // rotate in negative y fps = fps.rotateY(-5);
            	myRotY -= 1;
            	if (myRotY < -360) myRotY += 360;	// normalise angle 
            } else { // translate left fps = fps.translate(0.1f, 0, 0);
            	zrotrad = (myRotZ / 180 * Math.PI);  
            	myPosX += Math.cos(zrotrad) * 0.2;
            	myPosX += Math.sin(zrotrad) * 0.2;
            }
            break;
        
        // D key pressed
        case KeyEvent.VK_D:							 
            if (e.isShiftDown()) { // rotate in positive y direction fps = fps.rotateY(5);
            	myRotY += 1;
            	if (myRotY > 360) myRotY -= 360;	// normalise angle 
            } else {	// translate right fps = fps.translate(-0.1f, 0, 0);       
            	zrotrad = (myRotZ / 180 * Math.PI);
            	myPosX -= Math.cos(zrotrad) * 0.2;
            	myPosZ -= Math.sin(zrotrad) * 0.2;
            }
            break;

        // S key pressed 
        case KeyEvent.VK_S:							
            if (e.isShiftDown()) { // rotate in negative x direction fps = fps.rotateX(-5);
                myRotX -= 1;
                if (myRotX < -360) myRotX += 360; // normalise angle 
            } else { // translate backwards fps = fps.translate(0, 0, -0.1f);   
            	zrotrad = (myRotZ / 180*Math.PI);
            	xrotrad = (myRotX / 180*Math.PI);
        	
            	myPosX -= Math.sin(zrotrad);
            	// myPosY += Math.cos(zrotrad);
            	myPosZ += Math.sin(xrotrad);
            }
            break;
            
        // W key pressed
        case KeyEvent.VK_W:							 
            if (e.isShiftDown()) { // rotate in positive x direction fps = fps.rotateX(5);
            	myRotX += 1;
                if (myRotX > 360) myRotX -= 360; // normalise angle 
            }
            else {// shift in positive z direction fps = fps.translate(0, 0, 0.1f);
            	zrotrad = (myRotZ / 180*Math.PI);
            	xrotrad = (myRotX / 180*Math.PI);
            	
            	myPosX += Math.sin(zrotrad);
            	//myPosY -= Math.cos(zrotrad);
            	myPosZ -= Math.sin(xrotrad);
            }
            break;
            
        // Q key pressed 
        case KeyEvent.VK_Q:							
        	 if (e.isShiftDown()) { // decrease scale fps = fps.scale(1/1.1f, 1/1.1f, 1/1.1f);
        		 myScale = myScale/1.1f;
        	 }
             else { // rotate in -z direction fps = fps.rotateZ(-5);
            	 myRotZ -= 5; 
            	 if (myRotZ < -360) myRotZ += 360;
             }
        	 break;
        	 
        // E key pressed 
      	case KeyEvent.VK_E:	
      		if (e.isShiftDown()) // increase scale fps = fps.scale(1.1f, 1.1f, 1.1f);//myScale /= 1.1;
      			myScale = myScale*1.1f;
      		else // rotate in +z direction fps = fps.rotateZ(5);
      			myRotZ += 5;
      			if (myRotZ > 360) myRotZ -= 360;
      		break;
        
        //Delivery keys
  		case KeyEvent.VK_UP:						// Up arrow pressed, camera moves forward 
  			//Point3D p = fps.transform(new Point3D(0f, 0f, -0.1f));
  			//fps = fps.translate(p);
  			fps = fps.translate(0f, 0f, -0.1f);
  			break;
  		case KeyEvent.VK_DOWN:						// Down arrow pressed, camera moves backwards 
  			fps = fps.translate(0, 0, -0.1f);
  			break;
  		case KeyEvent.VK_LEFT:						// Left arrow pressed, camera moves left 
  			fps = fps.rotateY(-5);
  			break;
  		case KeyEvent.VK_RIGHT:						// Right arrow pressed, camera moves right 
  			fps = fps.rotateY(5);
  			break;
       }
        
       fps = CoordFrame3D.identity().translate(myPosX, myPosY, myPosZ)
    		   .rotateX(myRotX).rotateY(myRotY).rotateZ(myRotZ)
    		   .scale(myScale, myScale, myScale);
    }


}
