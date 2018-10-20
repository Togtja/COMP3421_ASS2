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
    private boolean fpsMode;
    private boolean tp = false; //if person just teleporten
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh model;
    
    private Terrain terrain; 
    private float yOff = 0.01f;
    
    public Person(WorldObject parent) throws IOException {
    	super(parent);
    	translate(-2f, yOff, -2f); 
    	setScale(5f);
        setRotY(50);
        model = new TriangleMesh("res/models/bunny.ply");     
        
        fpsMode = false; //true; 
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
    }
    
    public void drawPerson(GL3 gl) {
        Shader.setPenColor(gl, Color.BLUE);
        CoordFrame3D frame = CoordFrame3D.identity()
    			.translate(getPosition())
    			.scale(getScale(), getScale(), getScale());
        model.draw(gl, frame); 
    }

    

    @Override
    public void keyPressed(KeyEvent e) {
    	Point3D currPos = getGlobalPosition();
    	setHeight(currPos.getX(), currPos.getZ(), isFps());
    }
      	  

    public void setHeight(float x, float z, boolean avatar) {
    	int w = terrain.getWidth();
    	int d = terrain.getDepth();
    	float y = 0;

    	if (x >= w || x < 0 || z >= d || z < 0) { } // index out of bounds 
    	else { y = terrain.altitude(x, z); }

    	setPosition(getPosition().getX(), y+yOff, getPosition().getZ());
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
}
