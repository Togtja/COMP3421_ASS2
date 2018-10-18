package unsw.graphics.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;

/**
 * A cool scene object
 *
 */
public class MyCoolSceneObject extends SceneObject {

	CircularSceneObject myHead;
	
    public MyCoolSceneObject(SceneObject parent) {
        super(parent);
        // TODO: Write your scene object - a smiley face 
        myHead = new CircularSceneObject(parent, 0.25f, Color.YELLOW, Color.BLACK);   		
    }
    
    @Override 
    public void drawSelf(GL3 gl, CoordFrame2D frame) {
    	// set children of head and their translations
    	setShapeTranslations(myHead);
    	// draw head and children 
    	myHead.draw(gl, frame);
    }
    
    
    private static void setShapeTranslations(SceneObject parent) {    	
    	// set local coordinate frame translations for left eye
        CircularSceneObject leftEye = new CircularSceneObject(parent, 0.05f, Color.BLACK, Color.BLACK);
		leftEye.translate(-0.07f,0.05f);
    	
    	// set local coordinate frame translations for right eye
        CircularSceneObject rightEye = new CircularSceneObject(parent, 0.05f, Color.BLACK, Color.BLACK);
		rightEye.translate(0.07f,0.05f);
    	
    	// set local coordinate frame translations for smile
    	SemiCircleSceneObject smile = new SemiCircleSceneObject(parent, 0.1f, Color.BLACK, Color.BLACK);
		smile.translate(0f,-0.05f);
    }
}

