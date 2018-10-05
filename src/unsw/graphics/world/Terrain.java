package unsw.graphics.world;

import java.io.IOException;import java.nio.IntBuffer;import java.util.ArrayList;import java.util.Arrays;import java.util.List;import com.jogamp.opengl.GL3;import com.jogamp.opengl.util.GLBuffers;import unsw.graphics.CoordFrame2D;import unsw.graphics.CoordFrame3D;import unsw.graphics.Point2DBuffer;import unsw.graphics.Point3DBuffer;import unsw.graphics.Vector3;import unsw.graphics.geometry.LineStrip3D;import unsw.graphics.geometry.Point2D;import unsw.graphics.geometry.Point3D;import unsw.graphics.geometry.Triangle3D;import unsw.graphics.geometry.TriangleFan3D;import unsw.graphics.geometry.TriangleMesh;



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
    private Vector3 sunlight;        private LineStrip3D tri3D;    private TriangleMesh triMesh;    //private TriangleFan3D tri3D;         // buffers for drawing     private Point3DBuffer vertexBuffer;    private Point2DBuffer texCoordBuffer;    private IntBuffer indicesBuffer;

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
        this.sunlight = sunlight;    }    
    public void setTriangle(){        List<Point3D>  points = new ArrayList<Point3D>();        for(int i = 0; i < width; i++) {            for(int j = 0; j < depth; j++) {            	points.add(new Point3D((float) i, altitudes[i][j], (float) j));            	//System.out.println();            }        }                tri3D = new LineStrip3D(points);        //tri3D = new TriangleFan3D(points);        //tri3D = new TriangleMesh(points);    }
        
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
        /*         * if (x >= width || z >= width) {        	return 0;	        }        */
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
     * @param z     * @throws IOException 
     */
    public void addTree(float x, float z) throws IOException {
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
    }        /**     * Draw function, that draws the terrain as a TriangleFan3D     *      */        public void draw(GL3 gl, CoordFrame3D frame) {    	//tri3D = new TriangleFan3D(points);        tri3D.draw(gl, frame);    }                // need texture coords, verticies, indices     // normals generated on their own      // i.e. triangle mesh for 4x3 matrix     // vertices = x*z = 12    // 12 triangles     // normals are vertex normals = 12     // indices are num vertices/triangle * num triangles = 3*12 = 36            /**     * Function to set the vertices and texture coords in the Terrain      * Returns as a TriangleMesh     */    public TriangleMesh makeTerrain(GL3 gl) {        List<Point3D> vertices = new ArrayList<Point3D>();        List<Vector3> normals = new ArrayList<Vector3>();        List<Integer> indices = new ArrayList<Integer>();                        int count = 0;    	int size = width*depth;            	for (int i = 0; i < width; i++) {    		for (int k = 0; k < depth; k++) {    			float j = (float) getGridAltitude(i, k);    			vertices.add(new Point3D(i,j,k));    			// not sure if we need normals -> prof said unneeded    			normals.add(new Vector3(i,j,k));    		}      	}        	    	    	// one triangle has 3 indices, there are two triangles to form a square     	// for each square, there are 6 indices (2 * 3 indices)     	for (int i = 0; i < width -1; i++) {    		for (int k = 0; k < size; k++) { //depth-1; k++) {    			 // The index of the next slice                int j = (k + 1) % size;                                indices.add(2*k);                indices.add(2*j + 1);                indices.add(2*k + 1);                                indices.add(2*k);                indices.add(2*j + 1);                indices.add(2*j);    		}    	}    	            	triMesh = new TriangleMesh(vertices, normals, indices);        triMesh.init(gl);                // set buffers         setVertexBuffer(vertices);    	setIndicesBuffer(indices);        setTexCoordBuffer(vertices);                return triMesh;
    }            public Vector3 getSurfaceNormal(Point3D p0, Point3D p1, Point3D p2) {	//Triangle3D triangle) {    	Vector3 surfaceNormal = new Vector3(0,0,0);    	float x = 0;    	float y = 0;    	float z = 0;    	        List<Point3D> triPoints = new ArrayList<Point3D>();        triPoints.add(p0);        triPoints.add(p1);        triPoints.add(p2);     	int numVertices = triPoints.size();    	    	for (int i = 0 ; i < numVertices -1 ; i++) {    		x += (triPoints.get(i).getY() - triPoints.get(i+1).getY())*(triPoints.get(i).getZ() - triPoints.get(i+1).getZ());    	}    	for (int j = 0 ; j < numVertices -1 ; j++) {    		y += (triPoints.get(j).getZ() - triPoints.get(j+1).getZ())*(triPoints.get(j).getX() - triPoints.get(j+1).getX());    	}    	for (int k = 0 ; k < numVertices -1; k++) {    		z += (triPoints.get(k).getX() - triPoints.get(k+1).getX())*(triPoints.get(k).getY() - triPoints.get(k+1).getY());    	}    	    	surfaceNormal = new Vector3(x,y,z);    	    	return surfaceNormal;    }            /**     * Function to set the texture coords     */    public void setTexCoordBuffer(List<Point3D> vertices) { // should we also pass in the normals to see the direction / determine the texture coords?     	List<Point2D> texCoords = new ArrayList<Point2D>();    	    	// get max height    	float maxHeight = 0, alt = 0;     	for (int i = 0; i<width; i++) {    		for (int k = 0; k < depth; k++) {    			alt = altitudes[i][k];    			if (alt > maxHeight) { maxHeight = alt; }    		}    	}    	    	float j = 0;    	for (int i = 0; i<width; i++) {    		for (int k = 0; k < depth; k++) {    			j = altitudes[i][k];    			texCoords.add(new Point2D(i/width, j/maxHeight));    		}    	}    	    	texCoordBuffer = new Point2DBuffer(texCoords);    }        /**     * Function to get texture coordinates buffer      */    public Point2DBuffer getTexCoordBuffer() {    	return texCoordBuffer;    }        /**     * Function to set vertex buffer with list of Point3Ds     */     public void setVertexBuffer(List<Point3D> vertices) {        vertexBuffer = new Point3DBuffer(vertices);    }        /**     * Function to get vertex buffer     */    public Point3DBuffer getVertexBuffer() {    	return vertexBuffer;    }        /**     * Function to set indices buffer with an int array to represent the array list     */    public void setIndicesBuffer(List<Integer> indices) { // int[] indices) {    	int length = indices.size();    	int[] inds = new int[length];     	for (int i = 0; i < length; i++) {    		inds[i] = indices.get(i);    	}        indicesBuffer = GLBuffers.newDirectIntBuffer(inds);    }        /**     * Function to get indices buffer      */    public IntBuffer getIndicesBuffer() {    	return indicesBuffer;    }            
}
