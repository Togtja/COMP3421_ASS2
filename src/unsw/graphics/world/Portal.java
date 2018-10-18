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
	public Portal() throws IOException {
		model = new TriangleMesh("res/models/cube.ply");
		isPortal = false;
	}
    public void init(GL3 gl) {
    	model.init(gl);
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
	public boolean onPortal(Point3D p) {
		float tresh = 1.5f;
		if (position.getX() + tresh > p.getX() && position.getX() - tresh < p.getX() 
			&& position.getY() + tresh > p.getY() && position.getY() - tresh < p.getY()
			&& position.getZ() + tresh > p.getZ() && position.getZ() - tresh < p.getZ()) {
			
			return true;
		}
		return false;
	}
}
