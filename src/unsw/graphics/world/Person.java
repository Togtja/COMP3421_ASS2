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

public class Person {
    private Point3D position;
    private CoordFrame3D frame;
    
    // from model viewer 
    private static final boolean USE_LIGHTING = true;
    private static final boolean USE_TEXTURE = true;
    private static final boolean USE_CUBEMAP = false;
    private TriangleMesh model;
    private Texture texture;
    
    public Person() throws IOException {
    	position = new Point3D(0f, 0f, 0f);
        model = new TriangleMesh("res/models/bunny.ply");      
    }
    
    public void init(GL3 gl) {
    	model.init(gl);
    }
    
    public void TrdPerson(GL3 gl, CoordFrame3D frame) {
    	model.init(gl); 
    	/*texture = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        
        Shader.setInt(gl,"tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
		*/
        Shader.setPenColor(gl, Color.YELLOW);
    	frame = frame.translate(position).scale(5f, 5f, 5f);
    	model.draw(gl, frame);
    }
}
