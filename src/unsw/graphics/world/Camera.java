package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip3D;
import unsw.graphics.geometry.Point3D;




public class Camera implements  KeyListener {

    private Point3D myPos; //Should maybe point 3D
    private float myAngleX;
    private float myAngleY;
    private float myAngleZ; 
    private float myScale; 

    public Camera() {
    	//Some default Values
    	myPos = new Point3D(0,0.5f,4.5f);
    	myAngleX = 0; myAngleY = 0;
    	myAngleZ = 0; 
        myScale = 1;
    }
    /**
     * Set the view transform
     * 
     * Note: this is the inverse of the model transform above
     * 
     * @param gl
     */
    public void setView(GL3 gl) {
        CoordFrame3D viewFrame = CoordFrame3D.identity()
                .scale(1/myScale, 1/myScale, 1/myScale)
                .rotateZ(-myAngleZ)                
                .rotateX(-myAngleX)
                .rotateY(-myAngleY)
                .translate(-myPos.getX(), -myPos.getY(), -myPos.getZ());
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
        //return viewFrame;
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
                myAngleY += 5;
            else
                myPos = new Point3D(myPos.getX() - 0.1f, myPos.getY(), myPos.getZ());  
            break;
            
        case KeyEvent.VK_D:
            if (e.isShiftDown()) 
                myAngleY -= 5;
            else
                myPos = new Point3D(myPos.getX() + 0.1f, myPos.getY(), myPos.getZ());                
            break;

        case KeyEvent.VK_S:
            if (e.isShiftDown())
            	myAngleX -= 5;
            else
                myPos = new Point3D(myPos.getX(), myPos.getY() , myPos.getZ() + 0.1f); //Might want to change z instead
            break;

        case KeyEvent.VK_W:
            if (e.isShiftDown())
            	myAngleX += 5;
            else
                myPos = new Point3D(myPos.getX(), myPos.getY() , myPos.getZ() - 0.1f);
            break;
        case KeyEvent.VK_Q:
        	 if (e.isShiftDown())
        		 myScale *= 1.1;
             else
            	 myAngleZ -=5;
        	 break;
      	case KeyEvent.VK_E:
      		if (e.isShiftDown())
      			myScale /= 1.1;
      		else
      			myAngleZ +=5;
      		break;
        
        //Delivery keys
  		case KeyEvent.VK_UP:
  			myPos = new Point3D(myPos.getX(), myPos.getY() , myPos.getZ() - 0.1f);
  			break;
  		case KeyEvent.VK_DOWN:
  			myPos = new Point3D(myPos.getX(), myPos.getY() , myPos.getZ() + 0.1f);
  			break;
  		case KeyEvent.VK_LEFT:
  			myAngleY +=5;
  			break;
  		case KeyEvent.VK_RIGHT:
  			myAngleY -=5;
  			break;
       }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

}
