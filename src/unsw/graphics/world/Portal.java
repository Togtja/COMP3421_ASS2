package unsw.graphics.world;

import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Portal extends WorldObject {
	private TriangleMesh model;
	private boolean isPortal;
	//private Point3D position;
	
	private Camera front;
	private Camera back;
	
	public Portal(WorldObject parent) throws IOException {
		super(parent);
		model = new TriangleMesh("res/models/thingi.ply");
		isPortal = false;
		front = new Camera(parent);
		back = new Camera(parent);
	}
	
    public void init(GL3 gl) {
    	model.init(gl);
    	front.setView(gl);
    	back.setView(gl);
    }
    
	public void drawPortal(GL3 gl) { 
		setScale(0.5f);
		CoordFrame3D view = CoordFrame3D.identity()
				.translate(getPosition())
				.scale(getScale(), getScale(), getScale());
		model.draw(gl, view);
	}
	public boolean getPortal() {
		return isPortal;
	}
	public void setPortal(boolean t) {
		isPortal = t;
	}
	
	
	public int onPortal(Point3D p, float xTresh, float zTresh) {
		float yTresh = 2f;
		float x = getPosition().getX();
		float y = getPosition().getY();
		float z = getPosition().getZ();
		
		if ((x + xTresh > p.getX() && x < p.getX()) 
				&& (y + yTresh > p.getY() && y - yTresh < p.getY())
				&& (z + zTresh > p.getZ() && z < p.getZ())) {
			System.out.println("Returned 1");
			return 1; //Represtet portals as 2 cubes, this is first cube
		}
		else if ((x > p.getX() && x - xTresh < p.getX()) 
				&& (y + yTresh > p.getY() && y - yTresh < p.getY())
				&& (z > p.getZ() && z - zTresh < p.getZ())) {
			System.out.println("Returned 2");
			return 2; //Second cube
		}

		return 0;
	}
}
