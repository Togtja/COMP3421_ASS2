package unsw.graphics.scene;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Matrix3;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.Point2D;

/**
 * A SceneObject is an object that can move around in the world.
 * 
 * SceneObjects form a scene tree.
 * 
 * Each SceneObject is offset from its parent by a translation, a rotation and a scale factor. 
 *
 * TODO: The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class SceneObject {
    
    // the links in the scene tree
    private SceneObject myParent;
    private List<SceneObject> myChildren;

    // the local transformation
    private Point2D myTranslation;
    private float myRotation; //normalised to the range [-180..180)
    private float myScale;
    
    // Is this part of the tree showing?
    private boolean amShowing;

    /**
     * Special constructor for creating the root node. Do not use otherwise.
     */
    public SceneObject() {
        myParent = null;
        myChildren = new ArrayList<SceneObject>();

        myRotation = 0;
        myScale = 1;
        myTranslation = new Point2D(0,0);

        amShowing = true;
    }

    /**
     * Public constructor for creating SceneObjects, connected to a parent.
     *  
     * New objects are created at the same location, orientation and scale as the parent.
     *
     * @param parent
     */
    public SceneObject(SceneObject parent) {
        myParent = parent;
        myChildren = new ArrayList<SceneObject>();

        parent.myChildren.add(this);

        myRotation = 0;
        myScale = 1;
        myTranslation = new Point2D(0,0);

        // initially showing
        amShowing = true;
    }

    /**
     * Remove an object and all its children from the scene tree.
     */
    public void destroy() {
	    List<SceneObject> childrenList = new ArrayList<SceneObject>(myChildren);
        for (SceneObject child : childrenList) {
            child.destroy();
        }
        if(myParent != null)
                myParent.myChildren.remove(this);
    }

    /**
     * Get the parent of this scene object
     * 
     * @return
     */
    public SceneObject getParent() {
        return myParent;
    }

    /**
     * Get the children of this object
     * 
     * @return
     */
    public List<SceneObject> getChildren() {
        return myChildren;
    }
    
    /**
     * Set the children of this object
     * 
     * @return
     */
    public void setChildren(ArrayList<SceneObject> children) {
    	myChildren = children;
    }
    
    /**
     * Add the child to this object
     * 
     * @return
     */
    public void addChild(SceneObject child) {
    	myChildren.add(child);
    }

    /**
     * Get the local rotation (in degrees)
     * 
     * @return
     */
    public float getRotation() {
        return myRotation;
    }

    /**
     * Set the local rotation (in degrees)
     * 
     * @return
     */
    public void setRotation(float rotation) {
        myRotation = MathUtil.normaliseAngle(rotation);
    }

    /**
     * Rotate the object by the given angle (in degrees)
     * 
     * @param angle
     */
    public void rotate(float angle) {
        myRotation += angle;
        myRotation = MathUtil.normaliseAngle(myRotation);
    }

    /**
     * Get the local scale
     * 
     * @return
     */
    public float getScale() {
        return myScale;
    }

    /**
     * Set the local scale
     * 
     * @param scale
     */
    public void setScale(float scale) {
        myScale = scale;
    }

    /**
     * Multiply the scale of the object by the given factor
     * 
     * @param factor
     */
    public void scale(float factor) {
        myScale *= factor;
    }

    /**
     * Get the local position of the object 
     * 
     * @return
     */
    public Point2D getPosition() {
        return myTranslation;
    }

    /**
     * Set the local position of the object
     * 
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        setPosition(new Point2D(x,y));
    }

    /**
     * Set the local position of the object
     * 
     * @param x
     * @param y
     */
    public void setPosition(Point2D p) {
        myTranslation = p;
    }

    /**
     * Move the object by the specified offset in local coordinates
     * 
     * @param dx
     * @param dy
     */
    public void translate(float dx, float dy) {
        myTranslation = myTranslation.translate(dx, dy);
    }

    /**
     * Test if the object is visible
     * 
     * @return
     */
    public boolean isShowing() {
        return amShowing;
    }

    /**
     * Set the showing flag to make the object visible (true) or invisible (false).
     * This flag should also apply to all descendents of this object.
     * 
     * @param showing
     */
    public void show(boolean showing) {
        amShowing = showing;
    }

    /**
     * Update the object and all it's children. This method is called once per frame. 
     * 
     * @param dt The amount of time since the last update (in seconds)
     */
    public void update(float dt) {
        updateSelf(dt);
        
        // Make a copy of all the children to avoid concurrently modification issues if new objects
        // are added to the scene during the update.
        List<SceneObject> children = new ArrayList<SceneObject>(myChildren);
        for (SceneObject so : children) {
            so.update(dt);
        }
    }

    /** 
     * Update the object itself. Does nothing in the default case. Subclasses can override this
     * for animation or interactivity.
     * 
     * @param dt
     */
    public void updateSelf(float dt) {
        // Do nothing by default
    }

    /**
     * Draw the object (but not any descendants)
     * 
     * This does nothing in the base SceneObject class. Override this in subclasses.
     * 
     * @param gl
     */
    public void drawSelf(GL3 gl, CoordFrame2D frame) {
        // Do nothing by default
    }

    
    // ===========================================
    // COMPLETE THE METHODS BELOW
    // ===========================================
    
    /**
     * Draw the object and all of its descendants recursively.
     * 
     * TODO: Complete this method
     * 
     * @param gl
     */
    public void draw(GL3 gl, CoordFrame2D frame) {
        
        // don't draw if it is not showing
        if (!amShowing) {
            return;
        }

        // TODO: Compute the coordinate frame for this object
        // draw the object (Call drawSelf() to draw the object itself) 
        // and all its children recursively

        
        // Compute the coordinate frame for the person
        CoordFrame2D objectFrame = frame
                .translate(myTranslation.getX(), myTranslation.getY())
                .rotate(myRotation)
                .scale(myScale, myScale);

        // draw self 
        drawSelf(gl, objectFrame);

        // recursively call draw function for every child of object 
        List<SceneObject> children = new ArrayList<SceneObject>(myChildren);
        for (SceneObject so : children) {	// recursively draw tree 
            so.draw(gl,objectFrame);         		
        }

    }

    /**
     * Compute the object's position in world coordinates
     * 
     * @return a point in world coordinates
     */

    
    public Point2D getGlobalPosition() {
        // TODO: Complete this    	
    	
    	// check if there is a parent to the node 
    	if(getParent() == null) {
    		// if parent is null, this is the root 
    		return myTranslation; // return root translation
    	}
    	else {    		
    		// calculate parent translation matrix 
    		CoordFrame2D parentGlobalFrame = CoordFrame2D.identity()
    				.translate(myParent.getGlobalPosition().getX(), myParent.getGlobalPosition().getY())
    				.rotate(myParent.getGlobalRotation())
    				.scale(myParent.getGlobalScale(), myParent.getGlobalScale());
    		Matrix3 parentGlobalMatrix = parentGlobalFrame.getMatrix();
    		    		
    		// get local position point in vector form 
    		Vector3 localPosition = new Vector3(myTranslation.getX(), myTranslation.getY(), 1); 
    		
    		// get global position of point by multiplying it by the parent global matrix translation 
    		Vector3 globalPosition = parentGlobalMatrix.multiply(localPosition);
    		
    		// convert vector to point and return global position as a point 
    		return globalPosition.asPoint2D();
    	}
    }


    /**
     * Compute the object's rotation in the global coordinate frame
     * 
     * @return the global rotation of the object (in degrees) and 
     * normalized to the range (-180, 180) degrees. 
     */
    public float getGlobalRotation() {
        // TODO: Complete this
    	SceneObject parent = getParent(); // create new object that has parent info 
    	
    	if (getParent()!=null) {
    		return MathUtil.normaliseAngle(parent.getGlobalRotation() + getRotation());
    	}else { // parent is null, root object, get rotation of root
    		return getRotation(); 
    	}
    }

    /**
     * Compute the object's scale in global terms
     * 
     * @return the global scale of the object 
     */
    public float getGlobalScale() {
        // TODO: Complete this
    	SceneObject parent = getParent(); // create new object that has parent info
    	
    	if(getParent()!=null) { // parent exists, get global scale 
    		return parent.getGlobalScale() * getScale();
    	}else { // parent is null, root object, get scale of root
    		return getScale(); 
    	}
    }

    /**
     * Change the parent of a scene object.
     * 
     * @param parent
     */
    public void setParent(SceneObject parent) {
        // TODO: add code so that the object does not change its global position, rotation or scale
        // when it is reparented. You may need to add code before and/or after 
        // the fragment of code that has been provided - depending on your approach
        
    	// save global translation of child 
    	Point2D globalPosition = this.getGlobalPosition();
    	float globalRotation = this.getGlobalRotation();
    	float globalScale = this.getGlobalScale();
    	
        // save parent global translation 
        Point2D parentGlobalPosition = parent.getGlobalPosition();
    	float parentGlobalRotation = parent.getGlobalRotation();
    	float parentGlobalScale = parent.getGlobalScale();
    	
    	// calculate rotation from new parent to object
    	this.myRotation = MathUtil.normaliseAngle(globalRotation - parentGlobalRotation);
    	
    	// calculate scale from new parent to object 
    	this.myScale = globalScale / parentGlobalScale;
    	    	
    	// make inverse matrix for parent global to local 
    	CoordFrame2D parentInverseFrame = CoordFrame2D.identity()
    			.scale(1/parentGlobalScale, 1/parentGlobalScale)
    			.rotate(-1*parentGlobalRotation)
    			.translate(-1*parentGlobalPosition.getX(), -1*parentGlobalPosition.getY());
    	Matrix3 parentInverseMatrix = parentInverseFrame.getMatrix();
    	
    	// get local translation with new parent 
    	Vector3 globalPositionPoint = new Vector3(globalPosition.getX(), globalPosition.getY(), 1);
    	Vector3 localPosition = parentInverseMatrix.multiply(globalPositionPoint);
    	
    	// set position of point 
    	this.myTranslation = localPosition.asPoint2D();
    	
    	// update new parent 
        myParent.myChildren.remove(this);
        myParent = parent;
        myParent.myChildren.add(this);
    }
    

}