package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleFan3D;

public class Particle {
	
	private TriangleFan3D drop;
	private Color color;
	private List<Point3D> points;
	private float rand(float max, float min) {
		//int fiftFifty = (int) Math.random() * 2 + 1;
		double random = Math.random() * (max-1) + min;

		return (float ) random;
		
	}
	private float size;
	
	public float life;
	public Point3D pos;
	public Vector3 speed;
	
	
	public Particle(Point3D area, float dist){
		points = new ArrayList<Point3D>();
		points.add(new Point3D(0.1f, -0.70f, 0.0f)); // A
		points.add(new Point3D(-0.1f, -0.70f, 0.0f)); //C
		points.add(new Point3D(-0.1f,  0.70f, 0.0f)); //D
		points.add(new Point3D(0.1f,  0.70f, 0.0f)); //B
		drop = new TriangleFan3D(points);
		color = new Color(0,0,1, 0.7f);
		speed = new Vector3(0,-0.05f,0);
		pos = new Point3D(
				area.getX() + rand(dist, 1),
				area.getY() + rand(dist, 1),
				area.getZ() + rand(dist, 1));
		size = rand(2.5f, 0)/10;
	}
	public void draw(GL3 gl, CoordFrame3D frame) {
		Shader.setPenColor(gl, color);
		drop.draw(gl, frame.translate(pos).scale(size, size, size));
		pos = new Point3D(pos.getX()+speed.getX(),
						  pos.getY()+speed.getY(),
						  pos.getZ()+speed.getZ());
		
		//System.out.println("Our Friends: (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
	}
	
	public Point3D getPosition() {
		return pos;
	}
		
}
