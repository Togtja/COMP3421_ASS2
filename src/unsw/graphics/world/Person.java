package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Person extends WorldObject implements KeyListener {
    //private Point3D position; //The model position
    private boolean fpsMode = true;
    private boolean tp = false; //if person just teleporten
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh model;
   // private Terrain terrain;
    
    public Person(WorldObject parent) throws IOException {
    	super(parent);
    	setPosition(new Point3D(2.3f, -1f, 2f)); 
    	setScale(5f);
        setRotY(50);
        model = new TriangleMesh("res/models/bunny.ply");      
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
    }
    
    public void drawPerson(GL3 gl) {
        Shader.setPenColor(gl, Color.BLUE);

        // get parent global position 
        Point3D parentPosition = getParent().getGlobalPosition();
        float parentScale = getParent().getScale();
        float parentRotY = getParent().getRotY();
        
        // get person global position 
        Point3D pos = getGlobalPosition();
        float scale = getGlobalScale();
        float rotY = getGlobalRotY();
   
        // get vector info from camera to person 
		Point3D diff = new Point3D(pos.getX() - parentPosition.getX(), 
				pos.getY() - parentPosition.getY(), 
				pos.getZ() - parentPosition.getZ());
		float magnitude = (float) Math.sqrt(diff.getX()*diff.getX() + diff.getY()*diff.getY() + diff.getZ()*diff.getZ());
		double rads = Math.toRadians(rotY);
		float dx = (float) (Math.sin(rads)*-magnitude);
		float dz = (float) (Math.cos(rads)*-magnitude);
		
        
        
        CoordFrame3D frame = CoordFrame3D.identity()
        		.translate(new Point3D(parentPosition.getX() + getPosition().getX(),
        				parentPosition.getY() + getPosition().getY(),
        				parentPosition.getZ() + getPosition().getZ() ))
        		.scale(getScale(), getScale(), getScale())
        		.rotateY(getRotY());
/*        
        CoordFrame3D frame = CoordFrame3D.identity()
        		.translate(new Point3D(getGlobalPosition().getX(),
        				getGlobalPosition().getY(),
        				getGlobalPosition().getZ()))
        		.scale(getGlobalScale(), getGlobalScale(), getGlobalScale())
        		.rotateY(getGlobalRotY());*/
        
      /*  CoordFrame3D frame = CoordFrame3D.identity()
        		.translate(new Point3D(getParent().getGlobalPosition().getX(),
        				getParent().getGlobalPosition().getY(),
        				getParent().getGlobalPosition().getZ()))
        		.scale(getGlobalScale(), getGlobalScale(), getGlobalScale())
        		.rotateY(getGlobalRotY());
        */
        model.draw(gl, frame); 
    }
    
/*    public CoordFrame3D getfps() {
    	//camera.setView(gl);
    	if(fpsMode) { 
    		//trdPerson = camera.getView();
    		return camera.getView();
    	}
    	
    	//Else
    	float dir = -5f;
    	float speed = 0.3f;
		double rads = Math.toRadians(camera.getRotY());
		double dz = dir*Math.cos(rads)*speed;
		double dx = dir*Math.sin(rads)*speed;
		double dzR = dir*Math.cos(rads);
		double dxR = dir*Math.sin(rads);
    	return camera.getView().translate((float)dx, -1f, (float)dz);//.rotateX((float)dzR).rotateZ((float)dxR);
    }*/

    @Override
    public void keyPressed(KeyEvent e) {
    	if(fpsMode) {
    		setPosition(new Point3D(2.3f, -1f, 2f));
    	} else {
    		setPosition(new Point3D(3, 0 , 3)); 
    	}
    	
        /*float upSet = 1f; float offSet = 1f; //float avatarPlacement;
//    	if(camera.getPosition().getX() + offSet > 0 
//    			&& camera.getPosition().getX() < terrain.getWidth()  + offSet) {
//      	  if (camera.getPosition().getZ()  + offSet > 0
//      			  && camera.getPosition().getZ() < terrain.getDepth()  + offSet) {
        
        if(getPosition().getX() + offSet > 0 
    			&& getPosition().getX() < terrain.getWidth()  + offSet) {
      	  if (getPosition().getZ()  + offSet > 0
      			  && getPosition().getZ() < terrain.getDepth()  + offSet) {
        
      		//Some (up)off-setting needs to be done so that we have the correct height

      		 float x = getGlobalPosition().getX();
      		 float z = getGlobalPosition().getZ();
           	if(!fpsMode) {
       			float dir = -1; //float speed = 0.3f; 
       			double rads = Math.toRadians(camera.getRotY()); 
       			z += dir*Math.cos(rads);
       			x += dir*Math.sin(rads);
         	}
           	
      		camera.setHeight(x, upSet, z);// !fpsMode);
      		//Point3D p = getGlobalPosition();
      	  }
    	}*/
        
        
        /*setScale(5f);
        setRotY(50);
        CoordFrame3D frame = CoordFrame3D.identity();
        
        Point3D camPos = camera.getGlobalPosition();
        setPosition(camPos);
        double camRot = getRotY();
        double rads = Math.toRadians(camRot);
        int dir;
        if (camRot > 180) { dir = -1; }
        else { dir = 1; }
        
		double dz = dir*Math.cos(rads)*3;
		double dx = dir*Math.sin(rads)*3;
		float dy = -1f;//-2.5f;
		translate((float) dx, dy, (float) dz);
    	*/
    }
    //public void key 
	@Override
	public void keyReleased(KeyEvent e) {
		//camera.keyReleased(e);
		switch(e.getKeyCode()) {
    	case KeyEvent.VK_R:
    		if(fpsMode && !e.isAutoRepeat()) {
    			fpsMode = false;
    			//camera.setRotX(10);
    		}
    		else if(!e.isAutoRepeat()) {
        		//camera.firstPerson(camera.getView());
    			fpsMode = true;
    			//camera.setRotX(-10);

    		}
    	}
		
	}
	
/*    public void setTerrain(Terrain terrain) {
    	this.terrain = terrain;
    }
    
    public Terrain getTerrain() {
    	return terrain;
    }*/
    public boolean isFps() {
    	return fpsMode;
    }
 
    public boolean getTeleportet() {
    	return tp;
    }
    public void setTeleportet(boolean tf) {
    	tp =tf;
    }
/*    public Camera getCam() {
    	return camera;
    }
    */
    
}
