package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.examples.sailing.objects.Mouse;
import unsw.graphics.world.Person;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.Point4D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.world.Camera;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements KeyListener, MouseListener {
    private Terrain terrain;
    //private Camera camera;
    private Person person;
    private TriangleMesh terrainMesh;
    
    private Portal portal;
    private Portal portal2;
    private float portXTresh = 2f;
    private float portZTresh = 0.5f;
    
    private int verticesName;
    private int texCoordsName;
    private int indicesName;

    private Texture grass;
    private Texture trees;
    private Texture road; 
    
    private boolean daylight;
    
    private CoordFrame3D drawFrame;
    //deltaTime
    long lastTime =  System.nanoTime() * 1000000;
    
    
    // Rain particles
    private int MAXPARTICLES = 1000;
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
    
    public World(Terrain terrain) {
    	//super("Assignment 2", 2000, 2000);
    	super("Assignment 2", 600, 600);
        this.terrain = terrain;
        daylight = true;
        try {
			person = new Person(terrain);
			portal = new Portal();
			portal2 = new Portal();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Set default values to particles
        
 

    }
   
    
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain); 
        world.start();
    }

	
	@Override
	public void init(GL3 gl) {
		super.init(gl);
		getWindow().addKeyListener(person);
		getWindow().addKeyListener(person.getCam());
		getWindow().addKeyListener(this);
		getWindow().addMouseListener(this);
        
		person.init(gl);
		
		portal.init(gl);
		portal2.init(gl);
		
        terrain.makeTerrain(gl); // gets vertex, indices, and tex coord buffers for terrain
		terrainMesh = terrain.getTriMesh();
        terrainMesh.init(gl);
        
        person.setTerrain(terrain);
       
        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);

        verticesName = names[0];
        texCoordsName = names[1];
        indicesName = names[2];
        

        
        //Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/sunlight.glsl"); //"shaders/fragment_tex_phong.glsl");
         Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl"); //"shaders/fragment_tex_phong.glsl");

        shader.use(gl);
        
        grass = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        trees = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);
        //road = new Texture(gl, "res/textures/kittens.jpg", "jpg", true);
        road = new Texture(gl, "res/textures/road1.jpg", "jpg", true);
        
        setDayLighting(gl); // set the lighting properties for the shader 
        
        //The particle system
        ParticlesContainer = new Particles(gl, MAXPARTICLES);
        ParticlesContainer.init(gl);

	}
	
	@Override
    public void display(GL3 gl)  {
        super.display(gl);
        long time = System.nanoTime();
        float deltaTime = (float)((time - lastTime) / 1000000);
        lastTime = time;
        //simulateParticles(gl, deltaTime, person.getfps());
        // for day / night mode 
        if (daylight == true) {
        	setDayLighting(gl);
        } else {
        	setNightLighting(gl);
        }
        
        CoordFrame3D global = CoordFrame3D.identity();
        
       // set camera view frame 
        person.getCam().computeView();
        
        // shifted drawing coord frame based on width and depth of terrain 
        
        Shader.setInt(gl,"tex", 0);
        
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, grass.getId());

        Shader.setPenColor(gl, Color.GREEN);
                
        //setDayLighting(gl); // set the lighting properties for the shader 

        gl.glPolygonMode(GL3.GL_FRONT_AND_BACK,GL3.GL_FILL); // GL3.GL_LINE); // DEBUG: shows as lines vs. filled in ground 
        
        terrainMesh.draw(gl, person.getfps());
        
        gl.glActiveTexture(GL.GL_TEXTURE0 + 1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, trees.getId());
        
        terrain.drawTrees(gl, person.getfps());
        
        //gl.glActiveTexture(GL.GL_TEXTURE0 + 1);
        //gl.glBindTexture(GL.GL_TEXTURE_2D, trees.getId());
        Shader.setPenColor(gl, Color.WHITE);


        //System.out.println(terrain.altitude( 1.5f, 3f));
        terrain.drawRoads(gl, person.getfps());
        // person.TrdPerson(gl);
        if(!person.isFps()) {
        	person.drawPerson(gl); //global); // person.getfps());
        }
        if(portal.getPortal()) {
        	portal.drawPortal(gl, person.getfps());
        }
        if(portal2.getPortal()) {
        	portal2.drawPortal(gl, person.getfps());
        }
        
        
        
    }

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		terrainMesh.destroy(gl);
        //gl.glDeleteBuffers(2, new int[] { indicesName, verticesName }, 0);
        //terrain.destroy(gl);
        //grass.destroy(gl);
        //trees.destroy(gl);
	}


	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}

	// implement methods to implement KeyListener interface
	@Override
	public void keyPressed(KeyEvent e) {
		/*if (person.getTeleportet() &&
				(portal.onPortal(person.getPosition()) != 0
				&& portal2.onPortal(person.getPosition()) != 0)) {
		//person.keyPressed(e); // do what camera would do 
		switch(e.getKeyCode()) {  
        case KeyEvent.VK_N: // switch to/from night time mode 
        	if (daylight == true) { // switch to night mode
        		daylight = false;
        		//setDayLighting(gl);
        	} else { // switch to day mode 
        		daylight = true; 
        	}
        	break;
		}
		if (person.getTeleportet() &&
				(!portal.onPortal(person.getPosition())
				&& !portal2.onPortal(person.getPosition()))) {
			person.setTeleportet(false);
		}*/
		//If hit hits the first cube of the portal
		if (portal.getPortal() && portal2.getPortal()
				//&& !person.getTeleportet()
				&& portal.onPortal(person.getPosition(), portXTresh, portZTresh) == 1) {
				
				person.setPosition(portal2.getPosition().translate(-1.5f, 0, -1.5f));
				//person.setTeleportet(true);
		}
		else if (portal.getPortal() && portal2.getPortal()
				//&& !person.getTeleportet()
				&& portal.onPortal(person.getPosition(), portXTresh, portZTresh) == 2) {
			person.setPosition(portal2.getPosition().translate(1.5f, 0, 1.5f));
		}
		if (portal.getPortal() && portal2.getPortal()
				//&& !person.getTeleportet()
				&& portal2.onPortal(person.getPosition(), portXTresh, portZTresh) == 1) {
				
				person.setPosition(portal.getPosition().translate(-1.5f, 0, -1.5f));
				//person.setTeleportet(true);
		}
		else if (portal.getPortal() && portal2.getPortal()
				//&& !person.getTeleportet()
				&& portal2.onPortal(person.getPosition(), portXTresh, portZTresh) == 2) {
				
				person.setPosition(portal.getPosition().translate(1.5f, 0, 1.5f));
				//person.setTeleportet(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//person.keyReleased(e); // do what camera would do 

	}
	
    /**
     * Set lighting and material properties 
     * 
     * @param gl
     * @param shader
     */
	public void setDayLighting(GL3 gl) {
		 // Set the lighting properties
        Shader.setViewMatrix(gl, person.getfps().getMatrix());
        //Shader.setPoint3D(gl, "sunlight", new Point3D(0, 0, 5));
        Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
  		Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setFloat(gl, "phongExp", 16f);
	}
	
	public void setNightLighting(GL3 gl) {
		 // Set the lighting properties
	    Shader.setViewMatrix(gl, person.getfps().getMatrix());
        //Shader.setPoint3D(gl, "sunlight", new Point3D(0, 0, 5));

       Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
 		Shader.setColor(gl, "lightIntensity", Color.WHITE);
       Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
       // Set the material properties
       Shader.setColor(gl, "ambientCoeff", Color.WHITE);
       Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
       Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
       Shader.setFloat(gl, "phongExp", 16f);
	}


	public CoordFrame3D getDrawFrame() {
		return drawFrame;
	}


/**/ 
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON1 && !portal.getPortal()) {
			float dir = -5;
  			double rads = Math.toRadians(person.getRot()); 
  			float dz = (float) (dir*Math.cos(rads));
  			float dx = (float) (dir*Math.sin(rads));
			portal.placePortal(person.getPosition().getX() + dx,
					person.getPosition().getY(),
					person.getPosition().getZ() + dz);
			portal.setPortal(true);
			System.out.println("Mouse 1 pressed");
		}
		else if (arg0.getButton() == MouseEvent.BUTTON1) {
			portal.setPortal(false);
		}
		if(arg0.getButton() == MouseEvent.BUTTON3 && !portal2.getPortal()) {
			float dir = -5;
  			double rads = Math.toRadians(person.getRot()); 
  			float dz = (float) (dir*Math.cos(rads));
  			float dx = (float) (dir*Math.sin(rads));
			portal2.placePortal(person.getPosition().getX() + dx,
					person.getPosition().getY(),
					person.getPosition().getZ() + dz);
			portal2.setPortal(true);
			System.out.println("Mouse 3 pressed");
		}
		else if (arg0.getButton() == MouseEvent.BUTTON3) {
			portal2.setPortal(false);
		}

	}



	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}



	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseMoved(MouseEvent arg0) {
		//
		
	}



	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseWheelMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	void code(GL3 gl, int MAX){

	}

}