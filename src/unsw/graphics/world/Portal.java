package unsw.graphics.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.LineStrip3D;
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
		//DUBUG FOR PORTAL
		p1 = new Point3D(0,0,0);
		p2 = new Point3D(0,0,0);
		p3 = new Point3D(0,0,0);
		p4 = new Point3D(0,0,0);
		
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
		Line3D line1 = new Line3D(p1,p2);
		Line3D line2 = new Line3D(p2,p3);
		Line3D line3 = new Line3D(p3,p4);
		Line3D line4 = new Line3D(p4,p1);
		line1.draw(gl);
		line2.draw(gl);
		line3.draw(gl);
		line4.draw(gl);
	}
	public boolean getPortal() {
		return isPortal;
	}
	public void setPortal(boolean t) {
		isPortal = t;
	}
	
	
	public int onPortal(Point3D p, float dx, float dz) {
		float yTresh = 2f;

		float x = getPosition().getX();
		float y = getPosition().getY();
		float z = getPosition().getZ();
		double rad = Math.toRadians(getRotY());
		float xTresh =  dx;//(float) (xTresh * Math.cos(rad) - zTresh * Math.sin(rad));
		float zTresh =  dz;//(float) (zTresh * Math.cos(rad) + xTresh * Math.sin(rad));
		// DEBUG
		
		//System.out.println("Rotation:" + getRotY() 
		//+ "\nOriginal pos(" + dx + ", " + y + ", " + dz + ")" );
		
		//System.out.println("After me rotChange: (" + x + ", " + y + ", " + z + ")");
		
		//System.out.println("P: (" + p.getX() + ", " + p.getY() + ", " + p.getZ() + ")");
		//CoordFrame3Dnew CoordFrame3D
		// DEBUG THE AREA OF THE PORTA
		float fullX = x + xTresh;
		float fullZ = z + zTresh;
		float lowX = x - xTresh;
		float lowZ = z - zTresh;
		p1 = new Point3D(fullX, y, lowZ);
		p2 = new Point3D(fullX, y, fullZ); 
		p3 = new Point3D(lowX, y, fullZ); 
		p4 = new Point3D(lowX, y, lowZ);
 /*
		double rads = Math.toRadians(this.getRotY());
		double radc = Math.toRadians(this.getRotY());
		
		/*
		float d  = (float) Math.sqrt(Math.pow(p.getX() - this.getPosition().getX(), 2)
				+ Math.pow(p.getZ() - getPosition().getZ(), 2));
			
		float pX; float pZ;
		if(p.getX() > this.getPosition().getX()) {
			pX = p.getX() - this.getPosition().getX();
		}
		else {
			pX = this.getPosition().getX() - p.getX();
		}
		if (p.getZ() > this.getPosition().getZ()) {
			pZ = p.getZ() - this.getPosition().getZ();
		}
		else {
			pZ = this.getPosition().getZ() - p.getZ();
		}
		
		p = new Point3D(getPosition().getX() + pX, p.getY(), getPosition().getZ() + pZ);
		
		 pX = (float) (getPosition().getX() + d*Math.sin(rads));
		 pZ = (float) (getPosition().getZ() + d*Math.cos(rads));
		p = new Point3D(p.getX() + pX, p.getY(), p.getZ() + pZ);
		*/
		//TEST 
		/*
		
		float d1  = (float) Math.sqrt(Math.pow(p1.getX() - this.getPosition().getX(), 2)
	 					+ Math.pow(p1.getZ() - getPosition().getZ(), 2));
	   	float p1X = (float) (getPosition().getX() + d1*Math.sin(rads));
	   	float p1Z = (float) (getPosition().getZ()+ d1*Math.cos(rads));
	   	p1 = new Point3D(p1X, y, p1Z);
	   	
	   	
		float d2  = (float) Math.sqrt(Math.pow(p2.getX() - this.getPosition().getX(), 2)
					+ Math.pow(p2.getZ() - getPosition().getZ(), 2));
		float p2X = (float) (getPosition().getX() + d2*Math.sin(rads));
		float p2Z = (float) (getPosition().getZ()+ d2*Math.cos(rads));
		p2 = new Point3D(p2X, y, p2Z);
	
		
		float d3  = (float) Math.sqrt(Math.pow(p3.getX() - this.getPosition().getX(), 2)
				+ Math.pow(p3.getZ() - getPosition().getZ(), 2));
		float p3X = (float) (getPosition().getX() + d3*Math.sin(rads));
		float p3Z = (float) (getPosition().getZ()+ d3*Math.cos(rads));
		p3 = new Point3D(p3X, y, p3Z);

		
		float d4  = (float) Math.sqrt(Math.pow(p4.getX() - this.getPosition().getX(), 2)
			+ Math.pow(p4.getZ() - getPosition().getZ(), 2));
		float p4X = (float) (getPosition().getX() + d4*Math.sin(rads));
		float p4Z = (float) (getPosition().getZ()+ d4*Math.cos(rads));
		p1 = new Point3D(p4X, y, p4Z);
	   	*/
		
		if ((fullX >= p.getX() && x <= p.getX()) 
				&& (y + yTresh >= p.getY() && y - yTresh < p.getY())
				&& (fullZ >= p.getZ() && z <= p.getZ())) {
				System.out.println("RETURN 1");
			return 1; //Represent portals as 2 cubes, this is first cube
		}
		else if ((x >= p.getX() && lowX <= p.getX()) 
				&& (y + yTresh >= p.getY() && y - yTresh < p.getY())
				&& (z >= p.getZ() && lowZ <= p.getZ())) {
			System.out.println("RETURN 2");
			return 2; //Second cube
		}

		return 0;
	}
}
