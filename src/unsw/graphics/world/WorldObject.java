package unsw.graphics.world;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.scene.MathUtil;

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
public class WorldObject {
    
    // the links in the scene tree
    private WorldObject myParent;
    private List<WorldObject> myChildren;

    // the local transformation
    private Point3D myTranslation;
    private float myRotX; //normalised to the range [-180..180)
    private float myRotY; //normalised to the range [-180..180)
    private float myRotZ; //normalised to the range [-180..180)

    private float myScale;
    
    // Is this part of the tree showing?
    private boolean amShowing;

    /**
     * Special constructor for creating the root node. Do not use otherwise.
     */
    public WorldObject() {
        myParent = null;
        myChildren = new ArrayList<WorldObject>();

        myRotX = 0;
        myRotY = 0;
        myRotZ = 0;
        
        myScale = 1;
        
        myTranslation = new Point3D(0,0,0);

        amShowing = true;
    }

    /**
     * Public constructor for creating SceneObjects, connected to a parent.
     *  
     * New objects are created at the same location, orientation and scale as the parent.
     *
     * @param parent
     */
    public WorldObject(WorldObject parent) {
        myParent = parent;
        myChildren = new ArrayList<WorldObject>();

        parent.myChildren.add(this);

        myRotX = 0;
        myRotY = 0;
        myRotZ = 0;
        
        myScale = 1;
        myTranslation = new Point3D(0,0,0);

        // initially showing
        amShowing = true;
    }

    /**
     * Remove an object and all its children from the scene tree.
     */
    public void destroy() {
	    List<WorldObject> childrenList = new ArrayList<WorldObject>(myChildren);
        for (WorldObject child : childrenList) {
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
    public WorldObject getParent() {
        return myParent;
    }

    /**
     * Get the children of this object
     * 
     * @return
     */
    public List<WorldObject> getChildren() {
        return myChildren;
    }
    
    /**
     * Set the children of this object
     * 
     * @return
     */
    public void setChildren(ArrayList<WorldObject> children) {
    	myChildren = children;
    }
    
    /**
     * Add the child to this object
     * 
     * @return
     */
    public void addChild(WorldObject child) {
    	myChildren.add(child);
    }
    
    /**
     * Set the local X rotation (in degrees)
     * 
     * @return
     */
    public void setRotX(float rotation) {
        myRotX = MathUtil.normaliseAngle(rotation);
    }
    /**
     * Get the local X rotation (in degrees)
     * 
     * @return
     */
    public float getRotX() {
        return myRotX;
    }
    
    /**
     * Set the local Y rotation (in degrees)
     * 
     * @return
     */
    public void setRotY(float rotation) {
        myRotY = MathUtil.normaliseAngle(rotation);
    }
    
    /**
     * Get the local Y rotation (in degrees)
     * 
     * @return
     */
    public float getRotY() {
        return myRotY;
    }
    
    /**
     * Set the local z rotation (in degrees)
     * 
     * @return
     */
    public void setRotZ(float rotation) {
        myRotZ = MathUtil.normaliseAngle(rotation);
    }
    /**
     * Get the local Z rotation (in degrees)
     * 
     * @return
     */
    public float getRotZ() {
        return myRotZ;
    }
    




    /**
     * Rotate the object around an axis by the given angle (in degrees)
     * 
     * @param angle
     */
    /**
     * around X axis 
     */
    public void rotateX(float angle) {
        myRotX += angle;
        myRotX = MathUtil.normaliseAngle(myRotX);
    }
    /**
     * around Y axis 
     */
    public void rotateY(float angle) {
        myRotY += angle;
        myRotY = MathUtil.normaliseAngle(myRotY);
    }
    /**
     * around Z axis 
     */
    public void rotateZ(float angle) {
        myRotX += angle;
        myRotX = MathUtil.normaliseAngle(myRotZ);
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
    public Point3D getPosition() {
        return myTranslation;
    }

    /**
     * Set the local position of the object
     * 
     * @param x
     * @param y
     */
    public void setPosition(float x, float y, float z) {
        setPosition(new Point3D(x,y,z));
    }

    /**
     * Set the local position of the object
     * 
     * @param x
     * @param y
     */
    public void setPosition(Point3D p) {
        myTranslation = p;
    }

    /**
     * Move the object by the specified offset in local coordinates
     * 
     * @param dx
     * @param dy
     */
    public void translate(float dx, float dy, float dz) {
        myTranslation = myTranslation.translate(dx, dy, dz);
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
        List<WorldObject> children = new ArrayList<WorldObject>(myChildren);
        for (WorldObject so : children) {
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
    public void drawSelf(GL3 gl, CoordFrame3D objectFrame) {
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
    public void draw(GL3 gl, CoordFrame3D frame) {
        
        // don't draw if it is not showing
        if (!amShowing) {
            return;
        }

        // TODO: Compute the coordinate frame for this object
        // draw the object (Call drawSelf() to draw the object itself) 
        // and all its children recursively

        
        // Compute the coordinate frame for the person
        CoordFrame3D objectFrame = frame
                .translate(myTranslation.getX(), myTranslation.getY(), myTranslation.getZ())
                .rotateY(myRotY).rotateX(myRotX).rotateZ(myRotZ)
                .scale(myScale, myScale, myScale);

        // draw self 
        drawSelf(gl, objectFrame);

        // recursively call draw function for every child of object 
        List<WorldObject> children = new ArrayList<WorldObject>(myChildren);
        for (WorldObject so : children) {	// recursively draw tree 
            so.draw(gl, objectFrame);         		
        }

    }

    /**
     * Compute the object's position in world coordinates
     * 
     * @return a point in world coordinates
     */

    
    public Point3D getGlobalPosition() {
        // TODO: Complete this    	
    	
    	// check if there is a parent to the node 
    	if(getParent() == null) {
    		// if parent is null, this is the root 
    		return myTranslation; // return root translation
    	}
    	else {    		
    		// calculate parent translation matrix 
    		CoordFrame3D parentGlobalFrame = CoordFrame3D.identity()
    				.translate(myParent.getGlobalPosition().getX(), myParent.getGlobalPosition().getY(), myParent.getGlobalPosition().getZ())
    				.rotateY(myParent.getGlobalRotY()).rotateX(myParent.getGlobalRotX()).rotateZ(myParent.getGlobalRotZ())
    				.scale(myParent.getGlobalScale(), myParent.getGlobalScale(), myParent.getGlobalScale());
    		Matrix4 parentGlobalMatrix = parentGlobalFrame.getMatrix();
    		    		
    		// get local position point in vector form 
    		Vector4 localPosition = new Vector4(myTranslation.getX(), myTranslation.getY(), myTranslation.getZ(), 1); 
    		
    		// get global position of point by multiplying it by the parent global matrix translation 
    		Vector4 globalPosition = parentGlobalMatrix.multiply(localPosition);
    		
    		// convert vector to point and return global position as a point 
    		return globalPosition.asPoint3D();
    	}
    }


    /**
     * Compute the object's rotation in the global coordinate frame
     * 
     * @return the global rotation of the object (in degrees) and 
     * normalized to the range (-180, 180) degrees. 
     */
    /**
     * around X axis 
     */
    public float getGlobalRotX() {
        // TODO: Complete this
    	WorldObject parent = getParent(); // create new object that has parent info 
    	
    	if (getParent()!=null) {
    		return MathUtil.normaliseAngle(parent.getGlobalRotX() + getRotX());
    	}else { // parent is null, root object, get rotation of root
    		return getRotX(); 
    	}
    }
    /**
     * around Y axis 
     */
    public float getGlobalRotY() {
        // TODO: Complete this
    	WorldObject parent = getParent(); // create new object that has parent info 
    	
    	if (getParent()!=null) {
    		return MathUtil.normaliseAngle(parent.getGlobalRotY() + getRotY());
    	}else { // parent is null, root object, get rotation of root
    		return getRotY(); 
    	}
    }
    /**
     * around Z axis 
     */
    public float getGlobalRotZ() {
        // TODO: Complete this
    	WorldObject parent = getParent(); // create new object that has parent info 
    	
    	if (getParent()!=null) {
    		return MathUtil.normaliseAngle(parent.getGlobalRotZ() + getRotZ());
    	}else { // parent is null, root object, get rotation of root
    		return getRotZ(); 
    	}
    }

    /**
     * Compute the object's scale in global terms
     * 
     * @return the global scale of the object 
     */
    public float getGlobalScale() {
        // TODO: Complete this
    	WorldObject parent = getParent(); // create new object that has parent info
    	
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
    public void setParent(WorldObject parent) {
        // TODO: add code so that the object does not change its global position, rotation or scale
        // when it is reparented. You may need to add code before and/or after 
        // the fragment of code that has been provided - depending on your approach
        
    	// save global translation of child 
    	Point3D globalPosition = this.getGlobalPosition();
    	float globalRotX = this.getGlobalRotX();
    	float globalRotY = this.getGlobalRotY();
    	float globalRotZ = this.getGlobalRotZ();

    	float globalScale = this.getGlobalScale();
    	
        // save parent global translation 
    	Point3D parentGlobalPosition = parent.getGlobalPosition();
    	float parentGlobalRotX = parent.getGlobalRotX();
    	float parentGlobalRotY = parent.getGlobalRotY();
    	float parentGlobalRotZ = parent.getGlobalRotZ();
    	float parentGlobalScale = parent.getGlobalScale();

    	// calculate rotation from new parent to object
    	this.myRotX = MathUtil.normaliseAngle(globalRotX - parentGlobalRotX);
    	this.myRotY = MathUtil.normaliseAngle(globalRotY - parentGlobalRotY);
    	this.myRotZ = MathUtil.normaliseAngle(globalRotZ - parentGlobalRotZ);
    	
    	// calculate scale from new parent to object 
    	this.myScale = globalScale / parentGlobalScale;
    	    	
    	// make inverse matrix for parent global to local 
    	CoordFrame3D parentInverseFrame = CoordFrame3D.identity()
    			.scale(1/parentGlobalScale, 1/parentGlobalScale, 1/parentGlobalScale)
    			.rotateY(-1*parentGlobalRotY).rotateX(-1*parentGlobalRotX).rotateZ(-1*parentGlobalRotZ)
    			.translate(-1*parentGlobalPosition.getX(), -1*parentGlobalPosition.getY(), -1*parentGlobalPosition.getZ());
    	Matrix4 parentInverseMatrix = parentInverseFrame.getMatrix();
    	
    	// get local translation with new parent 
    	Vector4 globalPositionPoint = new Vector4(globalPosition.getX(), globalPosition.getY(), globalPosition.getZ(), 1);
    	Vector4 localPosition = parentInverseMatrix.multiply(globalPositionPoint);
    	
    	// set position of point 
    	this.myTranslation = localPosition.asPoint3D();
    	
    	// update new parent 
    	if (myParent != null) {
    		myParent.myChildren.remove(this);
    	}
        myParent = parent;
        myParent.myChildren.add(this);
    }
    

}