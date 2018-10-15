package unsw.graphics.world;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Point2D> points;
    private float width;
    
    private TriangleMesh triMesh;
    float altitude;
    
    // buffers for drawing 
    private Point3DBuffer vertexBuffer;
    private Point2DBuffer texCoordBuffer;
    private IntBuffer indicesBuffer;

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine) {
        this.width = width;
        this.points = spine;
        this.altitude = 0;
    }
    
    public Road(float width, List<Point2D> spine, float altitude) {
        this.width = width;
        this.points = spine;
        this.altitude = altitude;
    }


    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return width;
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return points.size() / 3;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return points.get(i);
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public Point2D point(float t) {
        /*int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;*/
        int i = 0;
        
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        

        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();        
        
        return new Point2D(x, y);
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    
    /**
     * Calculate the second Bezier curve for the road
     * and draw triangles for triangle mesh 
     * 
     * 			A---B
     * 			|\RT|
     *			| \ |
     *			|LT\|
     *			C---D
     *
     * LT = ACD
     * RT = DBA
     * 
     * @param initPoints
     */
    public void makeRoad(GL3 gl, int width, int depth) {    	
    	int size = 0, count = 0;
    	
    	// to get number of points in curve 
    	List<Point2D> curve = getPointsOnCurve(); // use function points(t) to get point (x,y) on curve 
    	size = curve.size(); // get size of curve1
    	
    	// to get points in both curves 
    	List<Point2D> curve1 = new ArrayList<Point2D>();
    	List<Point2D> curve2 = new ArrayList<Point2D>(); // use points on curve and width of road to get points on second curve 
    	for (int i = 0; i < size; i++) {
    		curve1.add(new Point2D(getPointsOnCurve().get(i).getX() - width/2, getPointsOnCurve().get(i).getY()));
    	    curve2.add(new Point2D(getPointsOnCurve().get(i).getX() + width/2, getPointsOnCurve().get(i).getY()));
    	}
    	
    	// check points with array bounds of terrain
    	curve1 = checkPoints(width, depth, curve1);
    	curve2 = checkPoints(width, depth, curve2);
    	
    	// Make triangle mesh
    	List<Point3D> vertices = new ArrayList<Point3D>();
        List<Integer> indices = new ArrayList<Integer>();
        List<Point2D> texCoords = new ArrayList<Point2D>();
    	for (int i = 0; i < size - 1; i++) {
 		   	// generate vertices points 
    		Point2D A = curve1.get(i);
 		   	Point2D B = curve2.get(i);
 		   	Point2D C = curve1.get(i+1);
 		   	Point2D D = curve2.get(i+1);
 		
 		   	// add vertices points to vertices list 
 			vertices.add(new Point3D(A.getX(), altitude+0.01f, A.getY())); //need to do altitude not 0
 			vertices.add(new Point3D(C.getX(), altitude+0.01f, C.getY()));
 			vertices.add(new Point3D(D.getX(), altitude+0.01f, D.getY()));
 			vertices.add(new Point3D(B.getX(), altitude+0.01f, B.getY())); 
 			
 			// add indices to indices list 
 			indices.add(count*4);			// add A 
 			indices.add(count*4 + 1);		// add C
 			indices.add(count*4 + 2);		// add D
 			indices.add(count*4);			// add A 
 			indices.add(count*4 + 2);		// add B 
 			indices.add(count*4 + 3);		// add C

 			// add tex coords to tex coords list 
 			texCoords.add(new Point2D(0,0));
 			texCoords.add(new Point2D(0,1));
 			texCoords.add(new Point2D(1,1));
 			texCoords.add(new Point2D(1,0));

 			count++;
    	}
    	
    	setVertexBuffer(vertices);
    	setIndicesBuffer(indices);
        setTexCoordBuffer(texCoords);
        
        triMesh = new TriangleMesh(vertices, indices, true, texCoords);	// initialize triangle mesh for terrain
    	triMesh.init(gl);
    }
    
   public List<Point2D> getPointsOnCurve(){
	   // use points calculate bezier curve 
	   List<Point2D> ptsOnCurve = new ArrayList<Point2D>();
	   // increment by a value to store a point from the bezier curve at t
    	for (float t = 0; t < 1.05; t+=0.05) {
    		ptsOnCurve.add(point(t));
    	}
    	return ptsOnCurve;
   }
   
   /**
    * Function to set the texture coord buffer 
    */
   public void setTexCoordBuffer(List<Point2D> texCoords) { 
   	texCoordBuffer = new Point2DBuffer(texCoords);
   }
   
   
   /**
    * Function to get texture coordinates buffer 
    */
   public Point2DBuffer getTexCoordBuffer() {
   	return texCoordBuffer;
   }
   
   /**
    * Function to set vertex buffer with list of Point3Ds
    */ 
   public void setVertexBuffer(List<Point3D> vertices) {
       vertexBuffer = new Point3DBuffer(vertices);
   }
   
   /**
    * Function to get vertex buffer
    */
   public Point3DBuffer getVertexBuffer() {
   	return vertexBuffer;
   }
   
   /**
    * Function to set indices buffer with an int array to represent the array list

    */
   public void setIndicesBuffer(List<Integer> indices) { // int[] indices) {
   	int length = indices.size();
   	int[] inds = new int[length]; 
   	
   	// convert list of indices to int array
   	for (int i = 0; i < length; i++) {
   		inds[i] = indices.get(i);
   	}
       indicesBuffer = GLBuffers.newDirectIntBuffer(inds);
   }
   
   /**
    * Function to get indices buffer 
    */
   public IntBuffer getIndicesBuffer() {
   		return indicesBuffer;
   }
   
   public void setTriMesh(TriangleMesh mesh) {
 		triMesh = mesh;
   } 
   
   public TriangleMesh getTriMesh() {
  		return triMesh;
   }
   
   public List<Point2D> checkPoints(int width, int depth, List<Point2D> pts) {
	   int size = pts.size();
	   Point2D p;
	   float x, y; 
	   List<Point2D> pts2 = new ArrayList<Point2D>(); 
	   // check points in list based on max and min for array bounds 
	   for (int i = 0; i < size; i++) {
		   x = pts.get(i).getX();
		   y = pts.get(i).getY();
		   if (x >= width) { x = width - 1; } // check x is greater than array width
		   else if (x < 0) { x = 0; } // check x is less than 0
		   if (y >= depth) { y = depth - 1; } // check y is greater than array depth
		   else if (y < 0) { y = 0; } // check y is less than 0
		   pts2.add(new Point2D(x,y)); // add updated point 
	   }
	   return pts2;
   }
   
   public Point2D getFirstControlPoint() {
	   return points.get(0);
   }
   
   public void setAltitude(float altitude) {
	   this.altitude = altitude;
   }
   
   public float getAltitude() {
	   return altitude;
   }
}
