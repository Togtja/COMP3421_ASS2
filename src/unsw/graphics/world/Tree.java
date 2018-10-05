package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private Point3D position;
   //private CoordFrame3D frame = CoordFrame3D.identity();
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh tree;
    private Texture texture;

    public Tree(float x, float y, float z) throws IOException {
        position = new Point3D(x, y, z);
        tree = new TriangleMesh("res/models/tree.ply");
       // frame.translate(position);
        
    }
    
    public void init(GL3 gl) {
    	// super.init(gl); if Tree extends application3D
    	// set coord frame for tree
    	tree.init(gl);
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    
	// set coord frame for tree based on position 
 
    
    
    // use this function? 
    public void drawTree(GL3 gl, CoordFrame3D frame) {
    	tree.init(gl); 
    	Shader.setPenColor(gl, Color.GREEN);
    	frame = frame.translate(position).scale(0.1f, 0.1f, 0.1f);
    	tree.draw(gl, frame);
    }
    

}
