package unsw.graphics.scene;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Line2D;

public class LineSceneObject extends SceneObject {

	private Line2D myLine;
	private Color myLineColor;
	private float myX0;
	private float myX1;
	private float myY0;
	private float myY1;

	/**
	 * Create a line scene object and add it to the scene tree
	 * 
	 * @param parent
	 *            The parent in the scene tree
	 * @param lineColor
	 *            The color of the line 
	 */
	
	// Create a LineSceneObject from (0,0) to (1,0)
	public LineSceneObject(SceneObject parent, Color lineColor) {
		super(parent);
		myLineColor = lineColor;
		// (x0, y0) == (0,0)
		myX0 = 0;
		myY0 = 0; 
		// (x1, y1) == (1,0)
		myX1 = 1; 
		myY1 = 0; 
		myLine = new Line2D(myX0, myY0, myX1, myY1);
	}

	/**
	 * Create a line scene object and add it to the scene tree
	 * 
	 * @param x0
	 *            X coordinate of starting point defining the line
	 * @param y0
	 *            Y coordinate of starting point defining the line    
	 * @param x1
	 *            X coordinate of end point defining the line  
	 * @param y1
	 *            Y coordinate of end point defining the line    
	 */
	
	// Create a LineSceneObject from (x1,y1) to (x2,y2)
	public LineSceneObject(SceneObject parent, float x0, float y0, float x1, float y1, Color lineColor) {
		super(parent);
		myX0 = x0;
		myY0 = y0;
		myX1 = x1;
		myY1 = y1;
		myLineColor = lineColor;
		myLine = new Line2D(myX0, myY0, myX1, myY1);
	}


	/**
	 * Get the line color.
	 * 
	 * @return
	 */
	public Color getLineColor() {
		return myLineColor;
	}

	/**
	 * Set the line color.
	 * 
	 * Setting the color to null means the outline should not be drawn
	 * 
	 * @param lineColor
	 */
	public void setLineColor(Color lineColor) {
		myLineColor = lineColor;
	}

	/**
	 * Draw the line
	 */
	@Override
	public void drawSelf(GL3 gl, CoordFrame2D frame) {
		// check if line color is null 
		if(getLineColor() == null) {
			return; // if line color null, exit function
		}
		
		Shader.setPenColor(gl, getLineColor());
		
        // Draw the line
		myLine.draw(gl,frame);		 	
	}

}
