package unsw.graphics.world;

import java.io.IOException;import java.nio.IntBuffer;import java.util.ArrayList;import java.util.Arrays;import java.util.List;import com.jogamp.opengl.GL3;import com.jogamp.opengl.util.GLBuffers;import unsw.graphics.CoordFrame2D;import unsw.graphics.CoordFrame3D;import unsw.graphics.Point2DBuffer;import unsw.graphics.Point3DBuffer;import unsw.graphics.Vector3;import unsw.graphics.geometry.LineStrip3D;import unsw.graphics.geometry.Point2D;import unsw.graphics.geometry.Point3D;import unsw.graphics.geometry.Triangle3D;import unsw.graphics.geometry.TriangleFan3D;import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain extends WorldObject {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;        private TriangleMesh triMesh;        // buffers for drawing     private Point3DBuffer vertexBuffer;    private Point2DBuffer texCoordBuffer;    private IntBuffer indicesBuffer;

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

        this.sunlight = sunlight;    }        public void drawTrees(GL3 gl) {//, CoordFrame3D frame) {    	for	(int i = 0; i < trees.size(); i++) {    		trees.get(i).init(gl);    		trees.get(i).drawTree(gl);//, frame);    	}    }        public int getWidth() {    	return width;    }    public int getDepth() {    	return depth;    }
     
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
    public double getGridAltitude(int x, int z) {    	if (x < width && x >= 0 && z < depth && z >= 0) {
    		return altitudes[x][z];    	}    	return 0;    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {    	if (x < width && x >= 0 && z < depth && z >= 0) {            altitudes[x][z] = h;    	} 
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {        // TODO: Implement this    	int xLow = (int) Math.floor(x);        int xHigh = xLow + 1;        int zLow = (int) Math.floor(z);        int zHigh = zLow + 1;                // initialize altitude 
        float altitude = 0;                // check if altitudes within range of array to avoid out of range error         if ( x < 0 || xHigh >= width || z < 0 || zHigh >= depth) {        	//System.out.println("esc");        	return altitude;        }       
        // check if both x and z are ints - if they are, dont need to linear interpolate, altitude already stored 
        if ((Math.round(x) == x) && (Math.round(z) == z)){
        	altitude = altitudes[(int) x][(int) z];        	        // x and or z are non int points - need to find closest int points in x and z and linear interpolate to get altitudes 
        } else if (xLow >= 0 && xHigh < width && zLow >= 0 && zHigh < depth) {        	float depth0 = altitudes[xLow][zLow];        	float depth1 = altitudes[xLow][zHigh];        	float depth2 = altitudes[xHigh][zLow];        	float depth3 = altitudes[xHigh][zHigh];        	float R1 = bilinearInterp(z, zLow, zHigh, depth0, depth1);        	float R2 = bilinearInterp(z, zLow, zHigh, depth2, depth3);        	altitude = bilinearInterp(x, xLow, xHigh, R1, R2);        }
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z     * @throws IOException 
     */
/*    public void addTree(float x, float z) throws IOException {
        float y = altitude(x, z) + 0.5f;
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }*/    public void addTree(float x, float z) throws IOException {        float y = altitude(x, z) + 0.5f;        WorldObject parent = this.getParent();        //Tree tree = new Tree(parent);        //tree.setPosition(x, y, z);        Tree tree = new Tree(x,y,z);        trees.add(tree);    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);      }
    
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
    public float bilinearInterp(float w, float w0, float w1, float depth0, float depth1) {
    	if (w == w0)         	           	return ((w1-w)/(w1-w0))*depth0;    	return (((w-w0)/(w1-w0))*depth1 + ((w1-w)/(w1-w0))*depth0);
    }        /**     * Draw function, that draws the terrain as a TriangleFan3D     *      */        public void draw(GL3 gl, CoordFrame3D frame) {    	//tri3D = new TriangleFan3D(points);        triMesh.draw(gl, frame);    }                // need texture coords, verticies, indices     // normals generated on their own      // i.e. triangle mesh for 4x3 matrix     // vertices = x*z = 12    // 12 triangles     // normals are vertex normals = 12     // indices are num vertices/triangle * num triangles = 3*12 = 36            /**     * Function to set the vertices and texture coords in the Terrain      * Returns as a TriangleMesh     */   // public TriangleMesh makeTerrain(GL3 gl) {    public void makeTerrain(GL3 gl) {        List<Point3D> vertices = new ArrayList<Point3D>();        List<Integer> indices = new ArrayList<Integer>();        List<Point2D> texCoords = new ArrayList<Point2D>();                drawTriangles(vertices, indices, texCoords); // calculate vertices, indices, and tex coords         // set buffers         setVertexBuffer(vertices);    	setIndicesBuffer(indices);        setTexCoordBuffer(texCoords);            	triMesh = new TriangleMesh(vertices, indices, true, texCoords);	// initialize triangle mesh for terrain    	triMesh.init(gl);    }        /**     * Function to set the texture coord buffer      */    public void setTexCoordBuffer(List<Point2D> texCoords) {     	texCoordBuffer = new Point2DBuffer(texCoords);    }            /**     * Function to get texture coordinates buffer      */    public Point2DBuffer getTexCoordBuffer() {    	return texCoordBuffer;    }        /**     * Function to set vertex buffer with list of Point3Ds     */     public void setVertexBuffer(List<Point3D> vertices) {        vertexBuffer = new Point3DBuffer(vertices);    }        /**     * Function to get vertex buffer     */    public Point3DBuffer getVertexBuffer() {    	return vertexBuffer;    }        /**     * Function to set indices buffer with an int array to represent the array list     */    public void setIndicesBuffer(List<Integer> indices) { // int[] indices) {    	int length = indices.size();    	int[] inds = new int[length];     	    	// convert list of indices to int array    	for (int i = 0; i < length; i++) {    		inds[i] = indices.get(i);    	}        indicesBuffer = GLBuffers.newDirectIntBuffer(inds);    }        /**     * Function to get indices buffer      */    public IntBuffer getIndicesBuffer() {    	return indicesBuffer;    }        /**     * Function to draw triangles     *      * 			A---B     * 			|\RT|     *			| \ |     *			|LT\|     *			C---D     *     * LT = ACD     * RT = DBA     *     */    private void drawTriangles(List<Point3D> vertices, List<Integer> indices, List<Point2D> texCoords) {    	double j;    	int count = 0;    	// numTriangles = 0; // to keep track of the number of triangles in the mesh     	for (int i = 0; i < width - 1; i++) {    		for (int k = 0; k < depth - 1; k++) {    			// generate vertices points     			Point3D A = new Point3D(i, altitudes[i][k], k);    			Point3D B = new Point3D(i+1, altitudes[i+1][k], k);    			Point3D C = new Point3D(i, altitudes[i][k+1], k+1);    			Point3D D = new Point3D(i+1, altitudes[i+1][k+1], k+1);    			    			// add vertices points to vertices list     			vertices.add(A);        		vertices.add(C);        		vertices.add(D);        		vertices.add(B);     				    			// add indices to indices list       			indices.add(count*4);			// add A     			indices.add(count*4 + 1);		// add C    			indices.add(count*4 + 2);		// add D    			indices.add(count*4);			// add A     			indices.add(count*4 + 2);		// add B    				indices.add(count*4 + 3);		// add C    			// add tex coords to tex coords list     			texCoords.add(new Point2D(0,0));    			texCoords.add(new Point2D(0,1));    			texCoords.add(new Point2D(1,1));    			texCoords.add(new Point2D(1,0));    			//numTriangles = numTriangles + 2;     			count++;    		}		}    }        /**     * Function to set TriangleMesh for terrain     * @param mesh     *      */    // unused function     public void setTriMesh(TriangleMesh mesh) {    	triMesh = mesh;    }        /**     * Function to get TriangleMesh of the terrain     */    public TriangleMesh getTriMesh() {    	return triMesh;    }	public void drawRoads(GL3 gl) {//, CoordFrame3D frame) {		// TODO Auto-generated method stub		for	(int i = 0; i < roads.size(); i++) {			// set the road altitude based on the terrain altitude			Point2D p = roads.get(i).getFirstControlPoint();			float alt = altitude(p.getX(), p.getY());			roads.get(i).setAltitude(alt);						// make and draw the triangle mesh for the road 			roads.get(i).makeRoad(gl);//, width, depth);			roads.get(i).getTriMesh().draw(gl);//, frame);    	}	}			
}
