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
/*
    private Point3D myPos; //Should maybe point 3D
    private float myAngleX;
    private float myAngleY;
    private float myAngleZ; 
    private float myScale; 
    */
    private CoordFrame3D viewFrame;
    private CoordFrame3D fps;
    private TriangleMesh terrain;
    private CoordFrame3D tps;
    private boolean fpsMode = true;
    
    public Camera() {
    	//Some default Values
    	//myPos = new Point3D(0,0.5f,4.5f);
    	//myAngleX = 0; myAngleY = 0;
    	//myAngleZ = 0; 
        //myScale = 1;
        viewFrame = CoordFrame3D.identity();
    }
    
    public Camera(TriangleMesh terrainMesh) {
    	//Some default Values
    	/*myPos = new Point3D(0,0.5f,4.5f);
    	myAngleX = 0; myAngleY = 0;
    	myAngleZ = 0; 
        myScale = 1;*/
        viewFrame = CoordFrame3D.identity();
        terrain = terrainMesh;
    }
    
    
    
    /**
     * Set the view transform
     * 
     * Note: this is the inverse of the model transform above
     * 
     * @param gl
     */
    public void setView(GL3 gl) { //Take in Point3D as argument so we default is decided in World
        viewFrame = CoordFrame3D.identity()
                .translate(new Point3D(0,-0.5f,-9f));       // .translate(0, -0.5f, -4.5f);
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }
    
    public CoordFrame3D getView() {
        return viewFrame;
    }
    public void firstPerson(CoordFrame3D frame) {
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
            	fps = fps.translate(0.1f, 0, 0);
            break;
            
        case KeyEvent.VK_D:
            if (e.isShiftDown()) 
            	fps = fps.rotateY(5);
            else
            	fps = fps.translate(-0.1f, 0, 0);               
            break;

        case KeyEvent.VK_S:
            if (e.isShiftDown())
            	fps = fps.rotateX(-5);
            else
            	fps = fps.translate(0, 0, -0.1f);           
            break;

        case KeyEvent.VK_W:
            if (e.isShiftDown())
            	fps = fps.rotateX(5);
            else
            	fps = fps.translate(0, 0, 0.1f);
            break;
        case KeyEvent.VK_Q:
        	 if (e.isShiftDown())
        		 fps = fps.scale(1/1.1f, 1/1.1f, 1/1.1f);
             else
            	 fps = fps.rotateZ(-5);
        	 break;
      	case KeyEvent.VK_E:
      		if (e.isShiftDown())
      			fps = fps.scale(1.1f, 1.1f, 1.1f);//myScale /= 1.1;
      		else
      			fps = fps.rotateZ(5);
      		break;
        
        //Delivery keys
  		case KeyEvent.VK_UP:
  			//Point3D p = fps.transform(new Point3D(0f, 0f, -0.1f));
  			//fps = fps.translate(p);
  			fps = fps.translate(0, 0, 0.1f);
  			break;
  		case KeyEvent.VK_DOWN:
  			fps = fps.translate(0, 0, -0.1f);
  			break;
  		case KeyEvent.VK_LEFT:
  			fps = fps.rotateY(-5);
  			break;
  		case KeyEvent.VK_RIGHT:
  			fps = fps.rotateY(5);
  			break;
       }

    }

    
    public void setTerrain(TriangleMesh terrain) {
    	this.terrain = terrain;
    }
    
    public TriangleMesh getTerrain() {
    	return terrain;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    	switch(e.getKeyCode()) {
    	case KeyEvent.VK_R:
    		if(fpsMode) {
    			fpsMode = false;
    			
    		}
    		else {
    			fpsMode = true;
    		}
    	}
    }
}
