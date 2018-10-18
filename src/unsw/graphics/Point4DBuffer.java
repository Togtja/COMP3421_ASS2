/**
 * 
 */
package unsw.graphics;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;

import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.Point4D;

/**
 * A buffer of points. Can be passed to OpenGL commands that expect buffers by
 * using the getBuffer() method.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Point4DBuffer {

    private FloatBuffer floatBuffer;

    private int capacity;

    /**
     * Construct a new buffer with the given capacity.
     * 
     * @param capacity
     */
    public Point4DBuffer(int capacity) {
        // Buffer stores pairs of floats
        this.capacity = capacity;
        floatBuffer = GLBuffers.newDirectFloatBuffer(capacity * 4);
    }

    public Point4DBuffer(List<Point4D> points) {
        this(points.size());
        for (int i = 0; i < capacity; i++) {
            put(i, points.get(i));
        }
    }

    /**
     * Add a {@link Point3D} to the buffer at the given index.
     * 
     * @param index
     * @param p
     */
    public void put(int index, Point4D p) {
        put(index, p.getX(), p.getY(), p.getZ(), p.getW());
    }

    /**
     * Add a point (given as an x-y-z coordinate) to the buffer at the given
     * index.
     * 
     * @param index
     * @param x
     * @param y
     */
    public void put(int index, float x, float y, float z, float w) {
        if (index >= 0 && index < capacity) {
            floatBuffer.put(index * 4, x);
            floatBuffer.put(index * 4 + 1, y);
            floatBuffer.put(index * 4 + 2, z);
            floatBuffer.put(index * 4 + 3, w);
        } else {
            throw new IndexOutOfBoundsException(
                    "index: " + index + ", capacity: " + capacity);
        }
    }

    public Buffer getBuffer() {
        return floatBuffer;
    }

    public int capacity() {
        return capacity;
    }

    public Point4D get(int i) {
        return new Point4D(floatBuffer.get(i*4), floatBuffer.get(i*4 + 1), floatBuffer.get(i*4 + 2), floatBuffer.get(i*4 + 3));
    }

}
