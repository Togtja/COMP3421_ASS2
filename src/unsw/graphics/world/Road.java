package unsw.graphics.world;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.Matrix3;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road extends WorldObject {

    private List<Point2D> points;
    private float width;
        
    private TriangleMesh triMesh;
    float altitude;
    
    private int debug;
    
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
        this.debug = 0; 
    }
    
    public Road(float width, List<Point2D> spine, float altitude) {
        this.width = width;
        this.points = spine;
        this.altitude = altitude;
        this.debug = 0;
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
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        

        float x = b3(0, t) * p0.getX() + b3(1, t) * p1.getX() + b3(2, t) * p2.getX() + b3(3, t) * p3.getX();
        float y = b3(0, t) * p0.getY() + b3(1, t) * p1.getY() + b3(2, t) * p2.getY() + b3(3, t) * p3.getY();        
        
        return new Point2D(x, y);
    }
    
    /**
     * Calculate the Bezier coefficients when m = 3
     * 
     * @param i
     * @param t
     * @return
     */
    private float b3(int i, float t) {
        
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
     * Calculate the Bezier coefficients when m = 2
     * 
     * @param i
     * @param t
     * @return
     */
    private float b2(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t);

        case 1:
            return 2 * (1-t) * t;
            
        case 2:
            return t * t;
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
    public void makeRoad(GL3 gl) { //, int width, int depth) {    	
    	int size = 0, count = 0;
        float w  = this.width/2;
        float step = 0.01f;
        float alt = altitude + 0.001f;
        
        List<Point2D> curve = new ArrayList<Point2D>(); // use points on curve and width of road to get points on second curve 
        List<Vector3> normals = new ArrayList<Vector3>();
        getPointsOnCurve(curve, normals, step);        
        
        List<Point3D> curve1 = new ArrayList<Point3D>();
        List<Point3D> curve2 = new ArrayList<Point3D>();
        //setPoints(step, curve1, curve2);
        size = curve.size();
             	
        for (int i = 0; i < size; i++) {
        	Point2D pt = curve.get(i);
        	Vector3 normal = normals.get(i);
        	
        	curve1.add(new Point3D(pt.getX() - w * normal.getX(), alt, pt.getY() - w * normal.getY()));
        	curve2.add(new Point3D(pt.getX() + w * normal.getX(), alt, pt.getY() + w * normal.getY()));
        }
        
        //debug
        if (debug == 0) {
        	for (int i = 0; i < curve1.size(); i++) {
        		System.out.println("x= " + curve1.get(i).getX() + " y= " +curve1.get(i).getX());
        	}
        	System.out.println("Loop completed");
        }
    	debug = 1;

        List<Point3D> vertices = new ArrayList<Point3D>();
        List<Integer> indices = new ArrayList<Integer>();
        List<Point2D> texCoords = new ArrayList<Point2D>();
        
        drawTriangles(vertices, indices, texCoords, size, curve1, curve2);

        setVertexBuffer(vertices);
    	setIndicesBuffer(indices);
        setTexCoordBuffer(texCoords);
        
        triMesh = new TriangleMesh(vertices, indices, true, texCoords);	// initialize triangle mesh for terrain
    	//triMesh = new TriangleMesh(vertices, true, texCoords);
        triMesh.init(gl);
    }
    
    
    
    private void drawTriangles(List<Point3D> vertices, List<Integer> indices, List<Point2D> texCoords, int size, List<Point3D> curve1, List<Point3D> curve2) {
    	int count = 0;
        for (int i = 0; i < size -1 ; i++) {
			// generate vertices points 
			Point3D A = curve1.get(i); 		// new Point3D(i, altitudes[i][k], k);
			Point3D B = curve1.get(i+1); 	// new Point3D(i+1, altitudes[i+1][k], k);
			Point3D C = curve2.get(i); 		// new Point3D(i, altitudes[i][k+1], k+1);
			Point3D D = curve2.get(i+1); 	// new Point3D(i+1, altitudes[i+1][k+1], k+1);
			
			// add vertices list
			vertices.add(D); // D = 0
			vertices.add(C); // C = 1
			vertices.add(A); // A = 2
			vertices.add(B); // B = 3

			// add tex coords to list 
			texCoords.add(new Point2D(D.getX(),D.getZ()));
			texCoords.add(new Point2D(B.getX(),B.getZ()));
			texCoords.add(new Point2D(A.getX(),A.getZ()));
			texCoords.add(new Point2D(C.getX(),C.getZ()));
						
			// add indices to indices list 
			indices.add(count*4);			// 0
			indices.add(count*4 + 2);		// 2
			indices.add(count*4 + 1);		// 1
			
			indices.add(count*4);			// 0
 			indices.add(count*4 + 3);		// 3
			indices.add(count*4 + 2);		// 2

			count++;
        }
    }
    
    /**
     * Function to get points on curve and normal for each point on curve 
     */
    public void getPointsOnCurve(List<Point2D> curve, List<Vector3> normals, float step){
    	// use points calculate bezier curve 
    	// increment by a value to store a point from the bezier curve at t
    	/*int size = points.size();
    	
    	
    	int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;*/
    	
    	for (float t = 0; t <= 1; t+=step) {
    		curve.add(point(t));
    		Vector3 normal = normal(tangent(t)).normalize();
        	normals.add(normal);
    	}
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
   
   /**
    * Function to set triangle mesh 
    */
   public void setTriMesh(TriangleMesh mesh) {
 		triMesh = mesh;
   } 
   
   /**
    * Function to get triangle mesh 
    */
   public TriangleMesh getTriMesh() {
  		return triMesh;
   }
   
   
   /**
    * Function to get first control point  
    */
   public Point2D getFirstControlPoint() {
	   return points.get(0);
   }
   
   /**
    * Function to get set road altitude 
    */
   public void setAltitude(float altitude) {
	   this.altitude = altitude;
   }
   
   /**
    * Function to get road altitude 
    */
   public float getAltitude() {
	   return altitude;
   }
   
   /**
    * Function to get tangent of curve at t 
    * 
    * @param t 
    */
   public Vector3 tangent(float t) {	   	   
	   int i = (int)Math.floor(t);
       t = t - i;
       
       i *= 3;
	   
	   Point2D p0 = points.get(i++);
       Point2D p1 = points.get(i++);
       Point2D p2 = points.get(i++);
       Point2D p3 = points.get(i++);
       
       Point2D p10 = new Point2D(p1.getX() - p0.getX(), p1.getY() - p0.getY());
       Point2D p21 = new Point2D(p2.getX() - p1.getX(), p2.getY() - p1.getY());
       Point2D p32 = new Point2D(p3.getX() - p2.getX(), p3.getY() - p2.getY());
	   
	   
	   float x = 3*(b2(0, t) * p10.getX() + b2(1, t) * p21.getX() + b2(2, t) * p32.getX());   
       float y = 3*(b2(0, t) * p10.getY() + b2(1, t) * p21.getY() + b2(2, t) * p32.getY());      

       Vector3 k = new Vector3(x,y,0);
       
	   return k.normalize(); 
   }
   
   /**
    * Function to calculate normal of a vector k (tangent vector)
    * 
    * @param k 
    */
   public Vector3 normal(Vector3 k) {
	   return new Vector3(-1*k.getY(), k.getX(), 0); // i = (-k2, k1, 0)
   }
   
   
}
