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
	
	//DEBUGGING
	Point3D p1;
	Point3D p2;
	Point3D p3;
	Point3D p4;
	//
	
	public Portal(WorldObject parent) throws IOException {
		super(parent);
		model = new TriangleMesh("res/models/thingi.ply");
		isPortal = false;
		front = new Camera(parent);
		back = new Camera(parent);
		this.setRotY(0);
		/* DUBUG FOR PORTAL
		p1 = new Point3D(0,0,0);
		p2 = new Point3D(0,0,0);
		p3 = new Point3D(0,0,0);
		p4 = new Point3D(0,0,0);
		*/
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
				.scale(getScale(), getScale(), getScale()).rotateY(getRotY());
		model.draw(gl, view);
		p1.draw(gl,CoordFrame3D.identity());
		p2.draw(gl,CoordFrame3D.identity());
		p3.draw(gl,CoordFrame3D.identity());
		p4.draw(gl,CoordFrame3D.identity());
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
		double rad = Math.toRadians(getRotY());
		float dx =  xTresh;//(float) (xTresh * Math.cos(rad) - zTresh * Math.sin(rad));
		float dz =  zTresh;//(float) (zTresh * Math.cos(rad) + xTresh * Math.sin(rad));
		// DEBUG
		
		//System.out.println("Rotation:" + getRotY() 
		//+ "\nOriginal pos(" + dx + ", " + y + ", " + dz + ")" );
		
		//System.out.println("After me rotChange: (" + x + ", " + y + ", " + z + ")");
		
		//System.out.println("P: (" + p.getX() + ", " + p.getY() + ", " + p.getZ() + ")");
		//CoordFrame3Dnew CoordFrame3D
		// DEBUG THE AREA OF THE PORTA
		/*
		p1 = new Point3D(x+dx, y, z - dx);
		p2 = new Point3D(x-dx, y, z + dx); 
		p3 = new Point3D(x+dx, y, z + dx); 
		p4 = new Point3D(x-dx, y, z - dx); 
		*/
		if ((x + dx > p.getX() && x < p.getX()) 
				&& (y + yTresh > p.getY() && y - yTresh < p.getY())
				&& (z + dz > p.getZ() && z < p.getZ())) {
			System.out.println("Returned 1");
			return 1; //Represent portals as 2 cubes, this is first cube
		}
		else if ((x > p.getX() && x - dx < p.getX()) 
				&& (y + yTresh > p.getY() && y - yTresh < p.getY())
				&& (z > p.getZ() && z - dz < p.getZ())) {
			System.out.println("Returned 2");
			return 2; //Second cube
		}

		return 0;
	}
}
