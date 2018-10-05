package unsw.graphics.world;

import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree{

    private Point3D position;
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh tree;
    private Texture texture;

    public Tree(float x, float y, float z) throws IOException {
        position = new Point3D(x, y, z);
        tree = new TriangleMesh("res/models/tree.ply");
    }
    
    public void init(GL3 gl) {
    	// super.init(gl); if Tree extends application3D
    	// set coord frame for tree
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    
	// set coord frame for tree based on position 

    
    
    // use this function? 
    public void drawTree() {
    	
    }
    

}
