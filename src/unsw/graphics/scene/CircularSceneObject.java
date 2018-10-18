package unsw.graphics.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;

public class CircularSceneObject extends PolygonalSceneObject {

	private float myRadius;
	private Point2D myCenter;

	//Create a CircularSceneObject with centre (0,0) and radius 1
	public CircularSceneObject(SceneObject parent, Color fillColor, Color lineColor) {
		// call constructor of parent class, input the given values to constructor, and create a "null" polygon
		super(parent, new Polygon2D(new ArrayList<Point2D>()), fillColor, lineColor);
		
		myCenter = new Point2D(0,0); 	// set centre to (0,0)
		myRadius = 1;					// set radius to 1 
	}

	//Create a CircularSceneObject with centre (0,0) and a given radius
	public CircularSceneObject(SceneObject parent, float radius, Color fillColor, Color lineColor) {
		// call constructor of parent class, input the given values to constructor, and create a "null" polygon
		super(parent, new Polygon2D(new ArrayList<Point2D>()), fillColor, lineColor);
		
		myCenter = new Point2D(0,0);	// set centre to (0,0)
		myRadius = radius;				// set radius to input 
	}

	// set points for circle for Polygon2D
	public List<Point2D> setPolygonPoints(Point2D center, float radius) {
		// initialize array of points 
        List<Point2D> points = new ArrayList<Point2D>();
        
        double theta, x = 0, y = 0;
        
        for (int i = 0; i < 32; i++) {
        	theta = (2*Math.PI)/32 * i; // get angle 
        	x = center.getX() + radius * Math.cos(theta); 
        	y = center.getY() + radius * Math.sin(theta); 
        	Point2D point = new Point2D((float)x, (float)y);	// set point
            points.add(point); 									// add point to list 
        }
        
        return points;
	}
	
	public void setCircle(List<Point2D> points) {
		setPolygon(new Polygon2D(points));
	}
	
	@Override 
    public void drawSelf(GL3 gl, CoordFrame2D frame) {
        // TODO: Write this method
		
		// set circle points in polygon with center and radius 
        setCircle(setPolygonPoints(myCenter, myRadius));
		
    	if(getLineColor() != null) {
    		// fill color is non-null, draw polygon with fill color 
    		Shader.setPenColor(gl, getFillColor());
            
            // draw circle 
    		getPolygon().draw(gl, frame);
    	}
    	if (getLineColor() != null) {
    		// line color is non-null, draw polygon outline with color
    		// set color 
    		Shader.setPenColor(gl, getLineColor());
    		
    		// draw outline of circle 
    		getPolygon().drawOutline(gl, frame);
    	}
    }
	
}
