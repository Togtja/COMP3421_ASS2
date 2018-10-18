package unsw.graphics.world;

import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Portal {
	private TriangleMesh model;
	private boolean isPortal;
	private Point3D position;
	
	private Camera front;
	private Camera back;
	
	public Portal() throws IOException {
		model = new TriangleMesh("res/models/thingi.ply");
		isPortal = false;
		front = new Camera();
		back = new Camera();
	}
    public void init(GL3 gl) {
    	model.init(gl);
    	front.setView(gl);
    	back.setView(gl);
    }
	public void drawPortal(GL3 gl, CoordFrame3D view) {
		model.draw(gl, view.translate(position).scale(0.5f, 0.5f, 0.5f));
	}
	public boolean getPortal() {
		return isPortal;
	}
	public void setPortal(boolean t) {
		isPortal = t;
	}
	public void placePortal(float x, float y, float z) {
		position = new Point3D(x,y,z);
	}
	public void placePortal(Point3D p) {
		position = p;
	}
	public Point3D getPosition() {
		return position;
	}
	public int onPortal(Point3D p, float xTresh, float zTresh) {
		float yTresh = 2f;
		if (position.getX() + xTresh > p.getX() && position.getX() < p.getX() 
			&& position.getY() + yTresh > p.getY() && position.getY() - yTresh < p.getY()
			&& position.getZ() + zTresh > p.getZ() && position.getZ() < p.getZ()) {
			System.out.println("Returned 1");
			return 1; //Represtet portals as 2 cubes, this is first cube
			
		}
		else if (position.getX() > p.getX() && position.getX() - xTresh < p.getX() 
				&& position.getY() + yTresh > p.getY() && position.getY() - yTresh < p.getY()
				&& position.getZ() > p.getZ() && position.getZ() - zTresh < p.getZ()) {
			System.out.println("Returned 2");
				return 2; //Second cube
		}
		return 0;
	}
}
