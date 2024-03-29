/**
 * 
 */
package unsw.graphics.geometry;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;

/**
 * A line strip in 3D space.
 * 
 * In a line strip, a series of points are joined with a line between each
 * adjacent pair.
 * 
 * This class is mutable, as new points can be added to the end of the strip.
 * 
 * 
 *
 */
public class LineStrip3D {
    private List<Point3D> points;

    public LineStrip3D() {
        points = new ArrayList<Point3D>();
    }
    
    /**
     * Construct a line strip with the given points.
     * @param points
     */
    public LineStrip3D(List<Point3D> points) {
        this.points = new ArrayList<Point3D>(points);
    }
    
    /**
     * Construct a line strip with the given values representing the points.
     * 
     * Argument 2*i and 2*i+1 form point i on the strip. e.g.
     * 
     * <code>new LineStrip3D(0,0,0, 1,0,0, 1,1,0);</code>
     * 
     * creates a line strip going from (0,0,0) to (1,0,0) to (1,1,0).
     * 
     * @param values
     */
    public LineStrip3D(float... values) {
        if (values.length % 3 != 0)
            throw new IllegalArgumentException("Odd number of arguments");
        List<Point3D> points = new ArrayList<Point3D>();
        for (int i = 0; i < values.length / 3; i++) {
            points.add(new Point3D(values[3*i], values[3*i + 1], values[3*i + 2]));
        }
        this.points = points;
    }

    /**
     * Draw the line strip in the given coordinate frame.
     * @param gl
     */
    public void draw(GL3 gl, CoordFrame3D frame) {
        Point3DBuffer buffer = new Point3DBuffer(points);

        int[] names = new int[1];
        gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, points.size() * 2 * Float.BYTES,
                buffer.getBuffer(), GL.GL_STATIC_DRAW);

        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_LINE_STRIP, 0, points.size());

        gl.glDeleteBuffers(1, names, 0);
    }
    
    /**
     * Draw the line on the canvas.
     * @param gl
     */
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }

    public void add(Point3D p) {
        points.add(p);
    }

    public Point3D getLast() {
        return points.get(points.size() - 1);
    }

    public List<Point3D> getPoints() {
        return points;
    }
}
