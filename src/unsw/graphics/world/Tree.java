package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
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
public class Tree extends WorldObject {

    private Point3D position;
    //private CoordFrame3D frame;
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh tree;
    private Texture texture;

    public Tree(float x, float y, float z) throws IOException {
    	setPosition(new Point3D(x,y,z)); 
        tree = new TriangleMesh("res/models/tree.ply");
        setScale(0.1f);
    }
    
    public Tree(WorldObject parent) throws IOException {
    	super(parent);
        tree = new TriangleMesh("res/models/tree.ply");
    }
    
    public void init(GL3 gl) {
    	tree.init(gl);
    }

    public void drawTree(GL3 gl) {//, CoordFrame3D frame) {
    	//tree.init(gl); 
    	Shader.setPenColor(gl, Color.BLUE);
    	CoordFrame3D frame = CoordFrame3D.identity()
    			.translate(getPosition())
    			.scale(getScale(), getScale(), getScale());
    	tree.draw(gl, frame);
    }
}
