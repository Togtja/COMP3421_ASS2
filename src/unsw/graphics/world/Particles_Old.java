package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Point4DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.Point4D;

public class Particles_Old {
	private int[] billboard_vertex_buffer;
	private int[] particles_position_buffer;
	private int[] particles_color_buffer;
	private int MAXPARTICLES;
	
	public Point3D[] pos;
	public Vector3[] speed;
	public Color color; 
	public float[] size; 
	public float[] life;
	private List<Point3D> points;
	public static List<Point4D> g_particule_position_size_data;
	public static List<Point4D> g_particule_color_data;
	public Particles_Old(GL3 gl, int MAX) {
		MAXPARTICLES = MAX;
		pos = new Point3D[MAX];
		speed = new Vector3[MAX];
		color = new Color(0,0,1,0.7f);
		size = new float[MAX];
		life = new float[MAX];
		g_particule_position_size_data = new ArrayList<Point4D>();
		g_particule_color_data = new ArrayList<Point4D>();
		points = new ArrayList<Point3D>();
		for(int i = 0; i < MAX; i++) {
			size[i] = 20f;
			life[i] = 3f;
			pos[i] = new Point3D(-3,5,2);
			speed[i] = new Vector3(0,1,0);
		}
		
		// The VBO containing the 4 vertices of the particles.
		// Thanks to instancing, they will be shared by all particles.
		points.add(new Point3D(-0.5f, -0.5f, 0.0f));
		points.add(new Point3D(0.5f, -0.5f, 0.0f));
		points.add(new Point3D(-0.5f,  0.5f, 0.0f));
		points.add(new Point3D(0.5f,  0.5f, 0.0f));
		Point3DBuffer buffer = new Point3DBuffer(points);
		billboard_vertex_buffer = new int[1];
		//GLuint billboard_vertex_buffer;
		gl.glGenBuffers(1, billboard_vertex_buffer, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, billboard_vertex_buffer[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, 3 * points.size() * Float.BYTES, buffer.getBuffer(), GL.GL_STATIC_DRAW);

		// The VBO containing the positions and sizes of the particles
		//GLuint particles_position_buffer;
		particles_position_buffer = new int[1];
		gl.glGenBuffers(1, particles_position_buffer,0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, particles_position_buffer[0]);
		// Initialize with empty (NULL) buffer : it will be updated later, each frame.
		gl.glBufferData(GL.GL_ARRAY_BUFFER, MAXPARTICLES * 4 * Float.BYTES, null, GL.GL_STATIC_DRAW);

		// The VBO containing the colors of the particles
		particles_color_buffer = new int[1];
		gl.glGenBuffers(1, particles_color_buffer,0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, particles_color_buffer[0]);
		// Initialize with empty (NULL) buffer : it will be updated later, each frame.
		gl.glBufferData(GL.GL_ARRAY_BUFFER, MAXPARTICLES * 4 * 1, null, GL.GL_STATIC_DRAW);
		
        
		
		
		
	}
	public void init(GL3 gl) {

	}
	public void  draw(GL3 gl, int ParticlesCount, CoordFrame3D frame) {
		
		
		// 1rst attribute buffer : vertices
		gl.glEnableVertexAttribArray(0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, billboard_vertex_buffer[0]);
		gl.glVertexAttribPointer(
		 0, // attribute. No particular reason for 0, but must match the layout in the shader.
		 3, // size
		 GL.GL_FLOAT, // type
		 false, // normalized?
		 0, // stride
		 (long) 0 // array buffer offset
		);

		// 2nd attribute buffer : positions of particles' centers
		gl.glEnableVertexAttribArray(1);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, particles_position_buffer[0]);
		gl.glVertexAttribPointer(
		 1, // attribute. No particular reason for 1, but must match the layout in the shader.
		 4, // size : x + y + z + size => 4
		 GL.GL_FLOAT, // type
		 false, // normalized?
		 0, // stride
		 (long)0 // array buffer offset
		);

		// 3rd attribute buffer : particles' colors
		gl.glEnableVertexAttribArray(2);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, particles_color_buffer[0]);
		gl.glVertexAttribPointer(
		 2, // attribute. No particular reason for 1, but must match the layout in the shader.
		 4, // size : r + g + b + a => 4
		 GL.GL_UNSIGNED_BYTE, // type
		 true, // normalized? *** YES, this means that the unsigned char[4] will be accessible with a vec4 (floats) in the shader ***
		 0, // stride
		 (long)0 // array buffer offset
		);
		gl.glVertexAttribDivisor(0, 0); // particles vertices : always reuse the same 4 vertices -> 0
		gl.glVertexAttribDivisor(1, 1); // positions : one per quad (its center)                 -> 1
		gl.glVertexAttribDivisor(2, 1);
		Point4DBuffer buffer_pos = new Point4DBuffer(g_particule_position_size_data);
		Point4DBuffer buffer_col = new Point4DBuffer(g_particule_color_data);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, particles_position_buffer[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, MAXPARTICLES * 4 * Float.BYTES,null , GL.GL_STATIC_DRAW); // Buffer orphaning, a common way to improve streaming perf. See above link for details.
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, ParticlesCount * Float.BYTES * 4, buffer_pos.getBuffer());

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, particles_color_buffer[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, MAXPARTICLES * 4 * 1, buffer_col.getBuffer(), GL.GL_STATIC_DRAW); // Buffer orphaning, a common way to improve streaming perf. See above link for details.
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, ParticlesCount * Float.BYTES * 4, buffer_col.getBuffer());
		
		Shader.setModelMatrix(gl, frame.getMatrix());
		gl.glDrawArraysInstanced(GL.GL_TRIANGLE_STRIP, 0, 4, ParticlesCount);

		gl.glDisableVertexAttribArray(0);
		gl.glDisableVertexAttribArray(1);
		gl.glDisableVertexAttribArray(2);
		
		gl.glDeleteBuffers(1, particles_color_buffer,0);
		gl.glDeleteBuffers(1, particles_position_buffer,0);
		gl.glDeleteBuffers(1, billboard_vertex_buffer,0);
	}
}
/* IN THE WORLD.JAVA
private int lastUsedParticle = 0;
private Particles ParticlesContainer;
private int findUnusedParticle() {
	Particles p = ParticlesContainer;
	for	(int i = lastUsedParticle; i < MAXPARTICLES; i++) {
		if (p.life[i] < 0) {
			lastUsedParticle = i;
			return i;
		}
	}
	for (int i = 0; i < lastUsedParticle; i++) {
		if (p.life[i] < 0) {
			lastUsedParticle = i;
			return i;
		}
	}
	return 0;
}
private void simulateParticles(GL3 gl, float delta, CoordFrame3D frame) {
	int newparticles = (int)(delta*10000.0);
	if (newparticles > (int)(0.016f*10000.0))
	    newparticles = (int)(0.016f*10000.0);
	Particles p = ParticlesContainer; // shortcut
	for(int i = 0; i < newparticles; i++) {
		int particleIndex = findUnusedParticle();
		p.life[particleIndex] = 30f;
		p.pos[particleIndex] = new Point3D(0,10,0);
		float spread = 1.5f;
		Vector3 mainDir = new Vector3(0, 10, 10);
		p.speed[particleIndex] = mainDir;
		
	}
	
	int ParticlesCount = 0;
	for(int i = 0; i < MAXPARTICLES; i++){
		
	    
	    if(p.life[i] > 0.0f){
	    	
	        // Decrease life
	    	
	        //p.life[i] -= delta;
	        if (p.life[i] > 0.0f){
	            // Simulate simple physics : gravity only, no collisions
	            p.speed[i] = new Vector3(0.0f,-9.81f * delta * 0.5f, 0.0f) ;
	            //p.pos[i] = new Point3D(0f, p.pos[i].getY() + p.speed[i].getY()* (float)delta, 0f);// * (float)delta;
	            Particles.g_particule_position_size_data.add(new Point4D(
	            		 p.pos[i].getX(), 
	            		 p.pos[i].getY(),
	            		 p.pos[i].getZ(),
	            		 p.size[i]));
	            Particles.g_particule_color_data.add(new Point4D(
	            		p.color.getBlue(),
	            		p.color.getGreen(),
	            		p.color.getRed(),
	            		p.color.getAlpha()));
	            p.draw(gl, ParticlesCount, frame);
	        }
	        else {
	            
	        }

	        ParticlesCount++;

	    }
	}
}

INIT IN WORDL
ParticlesContainer = new Particles(gl, MAXPARTICLES);
ParticlesContainer.init(gl);

DISPLAY IN WORLD
simulateParticles(gl, deltaTime, person.getfps());
*/