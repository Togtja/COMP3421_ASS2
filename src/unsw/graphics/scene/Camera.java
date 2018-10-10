package unsw.graphics.scene;

import java.awt.Toolkit;
import java.awt.geom.Dimension2D;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.glu.GLU;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Matrix3;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;

/**
 * The camera is a SceneObject that can be moved, rotated and scaled like any other, as well as
 * attached to any parent in the scene tree.
 * 
 * TODO: You need to implement the setView() method.
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Camera extends SceneObject {
    
    /**
     * The aspect ratio is the ratio of the width of the window to the height.
     */
    private float myAspectRatio;

    public Camera(SceneObject parent) {
        super(parent);
    }

    public void setView(GL3 gl) {
        // TODO compute a view transform to account for the cameras aspect ratio
        
    	// compute view transform for dimensions of screen;
    	CoordFrame2D viewFrame = CoordFrame2D.identity()
    			.scale(1/getAspectRatio(), 1)
    	// TODO apply further transformations to account for the camera's global position, 
    	// rotation and scale
                .scale(1/getScale(), 1/getScale())
                .rotate(-1*getRotation())
                .translate(-1*getPosition().getX(), -1*getPosition().getY());
    	
        // TODO set the view matrix to the computed transform
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }

    public void reshape(int width, int height) {
        myAspectRatio = (1f * width) / height;            
    }

    public void reshape2(int width, int height, GL3 gl) {
        myAspectRatio = (1f * width) / height;            
    }
    
    /**
     * Transforms a point from camera coordinates to world coordinates. Useful for things like mouse
     * interaction
     * 
     * @param x
     * @param y
     * @return
     */
    public Point2D fromView(float x, float y) {
        Matrix3 mat = Matrix3.translation(getGlobalPosition())
                .multiply(Matrix3.rotation(getGlobalRotation()))
                .multiply(Matrix3.scale(getGlobalScale(), getGlobalScale()))
                .multiply(Matrix3.scale(myAspectRatio, 1));
        return mat.multiply(new Vector3(x,y,1)).asPoint2D();
    }

    public float getAspectRatio() {
        return myAspectRatio;
    }
}
