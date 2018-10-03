package unsw.graphics.world;

import java.nio.IntBuffer;import java.util.ArrayList;import java.util.Arrays;import java.util.List;import com.jogamp.opengl.GL3;import com.jogamp.opengl.util.GLBuffers;import unsw.graphics.CoordFrame2D;import unsw.graphics.CoordFrame3D;import unsw.graphics.Point2DBuffer;import unsw.graphics.Point3DBuffer;import unsw.graphics.Vector3;import unsw.graphics.geometry.LineStrip3D;import unsw.graphics.geometry.Point2D;import unsw.graphics.geometry.Point3D;import unsw.graphics.geometry.TriangleFan3D;import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;       // private LineStrip3D tri3D;     private TriangleFan3D tri3D;         // buffers for drawing     private Point3DBuffer vertexBuffer;    private Point2DBuffer texCoordBuffer;    private IntBuffer indicesBuffer;    

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;        List<Point3D>  points = new ArrayList<Point3D>();        for(int i = 0; i < width; i++) {            for(int j = 0; j < depth; j++) {            	points.add(new Point3D((float) i, altitudes[i][j], (float) j));            }        }                //tri3D = new LineStrip3D(points);
        tri3D = new TriangleFan3D(points);    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
        float altitude = 0;

        // TODO: Implement this                // do we need to loop through the array to fins the closest x / y values to the point and then use         // the index and surrounding indexes  ?                
        /*         * if (x >= width || z >= width) {        	return 0;        }        */
        // check if both x and z are ints 
        if ((Math.round(x) == x) && (Math.round(z) == z)){
        	// dont need to linear interpolate, altitude already stored 
        	altitude = altitudes[(int) x][(int) z];
        } else {
        	// non int points 
        	// find closest int points in x and z 
            int xLow = (int) Math.floor(x), xHigh = (int) Math.ceil(x);
            int zLow = (int) Math.floor(z), zHigh = (int) Math.ceil(z);
            float depth0 = altitudes[xLow][zLow];
            float depth1 = altitudes[xLow][zHigh];
            float depth2 = altitudes[xHigh][zLow];
            float depth3 = altitudes[xHigh][zHigh];
            
            float R1 = bilinearInterp(z, zLow, zHigh, depth0, depth1);
            float R2 = bilinearInterp(z, zLow, zHigh, depth2, depth3);
            altitude = bilinearInterp(x, xLow, xHigh, R1, R2);
        }
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);        
    }
    
    /**
     * equation for one step of bilinear interpolation 
     * 
     * @param w
     * @param w0
     * @param w1
     * @param depth0
     * @param depth1
     * @return interpolation
     */
    public float bilinearInterp(float w, int w0, int w1, float depth0, float depth1) {
    	return (((w-w0)/(w1-w0))*depth1 + ((w1-w)/(w1-w0))*depth0);
    }        /**     * Draw function, that draws the terrain as a TriangleFan3D     *      */        public void draw(GL3 gl, CoordFrame3D frame) {    	//tri3D = new TriangleFan3D(points);        tri3D.draw(gl, frame);    }                // need texture coords, verticies, indices     // normals generated on their own      // i.e. triangle mesh for 4x3 matrix     // vertices = x*z = 12    // 12 triangles     // normals are vertex normals = 12     // indices are num vertices/triangle * num triangles = 3*12 = 36            /**     * Function to calculate the vertices in the Terrain      *      */        private TriangleMesh makeTerrain(GL3 gl) {        List<Point3D> vertices = new ArrayList<Point3D>();        List<Vector3> normals = new ArrayList<Vector3>();        List<Integer> indices = new ArrayList<Integer>();                int count = 0;    	int size = width*depth;            	for (int i = 0; i < width; i++) {    		for (int k = 0; k < depth; k++) {    			float j = (float) getGridAltitude(i, k);    			vertices.add(new Point3D(i,j,k));    			// not sure if we need normals -> prof said unneeded    			normals.add(new Vector3(i,j,k));    			    	        //Generate indices that effectively create a triangle fan.    	        indices.add(count);    	        if (count > 2) {    	            indices.add(0);    	            indices.add(count-1);    	        }    			count++;    		}      	}    	        TriangleMesh terrain = new TriangleMesh(vertices, normals, indices);        terrain.init(gl);        return terrain;    }    
}
