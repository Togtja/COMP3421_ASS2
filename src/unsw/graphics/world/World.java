package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
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
import unsw.graphics.Vector4;
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
    private static WorldObject root;
    private static Terrain terrain;
    private static Person person;
    //private static Person person;
    //private Camera camera;
    //private Person person;
    private TriangleMesh terrainMesh;
    
    private Portal portal;
    private Portal portal2;
    private float portXTresh = 1.5f;
    private float portZTresh = 1f;
    
    private int verticesName;
    private int texCoordsName;
    private int indicesName;

    private Texture grass;
    private Texture trees;
    private Texture road; 
    
    private boolean day;    

    // Rain particles
    private int MAXPARTICLES = 1000;
    private Particle[] rainDrop = new Particle[MAXPARTICLES];
    
    public World(Terrain terrain) {
    	//super("Assignment 2", 2000, 2000);
    	super("Assignment 2", 1000, 1000);
        this.terrain = terrain;
        day = true;
        root = new WorldObject();
        //root.setTerrain();
        
        
        try {
        	person = new Person(root, terrain);
        	portal = new Portal(root);
			portal2 = new Portal(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Set default values to particles
        for(int i = 0; i < MAXPARTICLES; i++) {
        	rainDrop[i] = new Particle(new Point3D(0,6,0), 10);
        }

    }
   
    
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        //Terrain 
    	terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain); 
        
        // set parent for each object in world 
        terrain.setParent(root);
        for (int i = 0; i < terrain.trees().size(); i++)  // set each tree parent to root 
        	terrain.trees().get(i).setParent(root);
        for (int i = 0; i < terrain.roads().size(); i++) // set each road parent to root 
        	terrain.roads().get(i).setParent(root);
        
        //camera.getPerson().setTerrain(terrain);
       // person = new Person(root);
        //person = new Person(camera);
        //camera.addChild(person);
        world.start();
    }

	
	@Override
	public void init(GL3 gl) {
		super.init(gl);
		getWindow().addKeyListener(person.getCamera());
		getWindow().addKeyListener(person);

		//getWindow().addKeyListener(person.getCam());
		getWindow().addKeyListener(this);
		getWindow().addMouseListener(this);
        
		// initialize classes
		person.init(gl);
		

		portal.init(gl);
		portal2.init(gl);
        terrain.makeTerrain(gl); // gets vertex, indices, and tex coord buffers for terrain
		terrainMesh = terrain.getTriMesh();
        terrainMesh.init(gl);
        //person.setTerrain(terrain);
	    
        // generate buffers 
        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);
        verticesName = names[0];
        texCoordsName = names[1];
        indicesName = names[2];
        
        // initialize and set the shader and its lighting properties
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/sunlight.glsl"); 
	    shader.use(gl);
	}
	
	@Override
    public void display(GL3 gl)  {
        super.display(gl);
        
		person.setView(gl); // set view matrix, draw camera, draw children of camera 
		
		
        // for day / night mode 
        setLighting(gl);
        
        Shader.setInt(gl,"tex", 0);
        grass = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        // to draw terrain 
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, grass.getId());
        Shader.setPenColor(gl, Color.GREEN);
        gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, GL3.GL_FILL); // GL3.GL_LINE); // DEBUG: shows as lines vs. filled in ground 
        terrainMesh.draw(gl);

        grass.destroy(gl);
        
        // to draw trees 
        trees = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);
        gl.glActiveTexture(GL.GL_TEXTURE0);// + 1);
        gl.glBindTexture(GL.GL_TEXTURE_2D, trees.getId());
        terrain.drawTrees(gl);
        trees.destroy(gl);
        
        // to draw roads 
        road = new Texture(gl, "res/textures/road1.jpg", "jpg", true);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, road.getId());
        Shader.setPenColor(gl, Color.WHITE);
        terrain.drawRoads(gl);
        road.destroy(gl);
        
        
        Texture texture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);            
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        //Shader.setPenColor(gl, Color.WHITE);
    	person.drawPerson(gl);
    	texture.destroy(gl);

    	


/*
        if(!camera.getPerson().isFps()) {
            Texture texture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", true);            
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
            
            Shader.setPenColor(gl, Color.WHITE);
            
            
    		camera.drawAvatar(gl);
    		trees.destroy(gl);
        }
        */
        if(portal.getPortal()) {
        	Shader.setPenColor(gl, Color.BLUE);
        	portal.drawPortal(gl);
        }
        if(portal2.getPortal()) {
        	Shader.setPenColor(gl, Color.ORANGE);
        	portal2.drawPortal(gl);
        }
        Shader.setPenColor(gl, Color.BLUE);
        for (int i = 0; i < MAXPARTICLES; i++) {
        	//rainDrop[i].draw(gl, person.getCam().getRotY()-180);
        	rainDrop[i].draw(gl, person.getCamera().getRotY()-180);
        	if(rainDrop[i].getPosition().getY() < 0) {
        		rainDrop[i].destroy();
        		rainDrop[i] = new Particle(new Point3D(0,6,0), 10);
        	}
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
		switch(e.getKeyCode()) {  
        case KeyEvent.VK_N:							// N key pressed, switch to night mode 
        	if (day == true) { day = false; } 
        	else { day = true; }
        	break;
		}

		if(portal.getPortal() && portal2.getPortal()) {
			Point3D tpFromPos;
			if (person.isFps()) {
				tpFromPos = person.getCamera().getPosition();
			}
			else  {
				tpFromPos = person.getPosition();
			}
			//If hit hits the first cube of the portal
			if (portal.onPortal(person.getCamera().getPosition(), portXTresh, portZTresh) == 1) {
	 				
				person.getCamera().setPosition(portal2.getPosition().translate(-1.5f, 0, -1.5f));
	 				//person.setTeleportet(true);
	 		}
	 		else if (portal.onPortal(person.getCamera().getPosition(), portXTresh, portZTresh) == 2) {
	 			person.getCamera().setPosition(portal2.getPosition().translate(1.5f, 0, 1.5f));
	 		}
	 		if (portal2.onPortal(person.getCamera().getPosition(), portXTresh, portZTresh) == 1) {
	 				
	 			person.getCamera().setPosition(portal.getPosition().translate(-1.5f, 0, -1.5f));
	 				//person.setTeleportet(true);
	 		}
	 		else if (portal2.onPortal(person.getCamera().getPosition(), portXTresh, portZTresh) == 2) {
	 				
	 			person.getCamera().setPosition(portal.getPosition().translate(1.5f, 0, 1.5f));
	 				//person.setTeleportet(true);
	 		}
			
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
	public void setLighting(GL3 gl) {
		 // Set the lighting properties
		
		// get sunlight vector coords
		float xSun = terrain.getSunlight().getX();
		float ySun = terrain.getSunlight().getY();
		float zSun = terrain.getSunlight().getZ();
		
/*		// check fps
		Point3D torchPos;
		if (camera.getPerson().isFps()) {
			torchPos = new Point3D(camera.getGlobalPosition().getX(), camera.getGlobalPosition().getY(), camera.getGlobalPosition().getZ());
*/
		Point3D torchPos;
		// check fps
		if (person.isFps()) {
			torchPos = new Point3D(person.getCamera().getGlobalPosition().getX(),
					person.getCamera().getGlobalPosition().getY(),
					person.getCamera().getGlobalPosition().getZ());
		} else { 
			torchPos = new Point3D(person.getGlobalPosition().getX(),
					person.getGlobalPosition().getY() + 0.2f, 
					person.getGlobalPosition().getZ());
		}
		
        Shader.setPoint3D(gl, "sunlight", new Point3D(xSun,ySun,zSun)); // set sunlight vector to passed in vector 
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setFloat(gl, "phongExp", 16f);
        
        // for torch light
        Shader.setFloat(gl, "cutoff", 90f); // in degrees
        Shader.setPoint3D(gl, "posTorch", torchPos);
        Shader.setFloat(gl, "attenuationFactor", 40f); 
        Shader.setColor(gl, "lightIntensityTorch", Color.YELLOW); 
        
        if (day) {
        	// light properties for day
            Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
        	Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
            Shader.setPoint3D(gl, "dirTorch", new Point3D(0,0,0)); // set torchDir vector 
        } else { 
        	// light properties for night 
            Shader.setColor(gl, "specularCoeff", new Color(0.5f, 0.5f, 0.5f));
        	Shader.setColor(gl, "ambientIntensity", new Color(0.01f, 0.01f, 0.01f));
            Shader.setPoint3D(gl, "dirTorch", new Point3D(0,0,1)); // set torchDir vector 
        }
	}


/**/ 
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON1 && !portal.getPortal()) {
			float dir = -2.5f;
  			double rads = Math.toRadians(person.getCamera().getRotY()); 
			//double rads = Math.toRadians(camera.getRotY()); 
  			float dz = (float) (dir*Math.cos(rads));
  			float dx = (float) (dir*Math.sin(rads));
  			/*
			portal.setPosition(camera.getPosition().getX() + dx,
					camera.getPosition().getY(),
					camera.getPosition().getZ() + dz);*/
			portal.setPosition(person.getPosition().getX() + dx,
					person.getTerrainY(
							(person.getPosition().getX() + dx),
							person.getPosition().getY() + person.getYOff(),
							person.getPosition().getZ() + dz),
					person.getPosition().getZ() + dz);
			//portal.setRotY(person.getCamera().getRotY() + 90);
			//portal.setRotY(camera.getRotY() + 90);
			portal.setPortal(true);
		}
		else if (arg0.getButton() == MouseEvent.BUTTON1) {
			portal.setPortal(false);
		}
		
		if(arg0.getButton() == MouseEvent.BUTTON3 && !portal2.getPortal()) {
			float dir = -2.5f;
			double rads = Math.toRadians(person.getCamera().getRotY()); 
  			//double rads = Math.toRadians(camera.getRotY()); 
  			float dz = (float) (dir*Math.cos(rads));
  			float dx = (float) (dir*Math.sin(rads));
/*			portal2.setPosition(camera.getPosition().getX() + dx,
					camera.getPosition().getY(),
					camera.getPosition().getZ() + dz);*/
			portal2.setPosition(person.getPosition().getX() + dx,
					person.getPosition().getY() + person.getYOff(),
					person.getPosition().getZ() + dz);
			//portal2.setRotY(person.getCamera().getRotY() + 90);
			//portal2.setRotY(camera.getRotY() + 90);
			portal2.setPortal(true);
		} else if (arg0.getButton() == MouseEvent.BUTTON3) {
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
	public Terrain getTerrain() {
		return terrain;
	}

}