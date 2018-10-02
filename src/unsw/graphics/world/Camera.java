package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip3D;
import unsw.graphics.geometry.Point3D;




public class Camera implements  KeyListener {

    private Point3D myPos; //Should maybe point 3D
    private float myAngle; //
    private float myScale; //

    public Camera() {
    	//Some default Values
    	myPos = new Point3D(0,0,2);
        myAngle = 0;
        myScale = 2;
    }

    public void draw(GL3 gl, CoordFrame3D frame) {
    	CoordFrame3D cameraFrame = frame.translate(myPos)
                .rotateZ(myAngle)
                .scale(myScale, myScale, myScale);
    	LineStrip3D camera = new LineStrip3D(1,1,1, -1,1,1, -1,-1,1, 1,-1,1, 1,1,1,
    										 1,1,-1, -1,1,-1, -1,-1,-1, 1,-1,-1, 1,1,-1);
        camera.draw(gl, cameraFrame);
    }

    /**
     * Set the view transform
     * 
     * Note: this is the inverse of the model transform above
     * 
     * @param gl
     */
    public CoordFrame3D setView(GL3 gl) {
        CoordFrame3D viewFrame = CoordFrame3D.identity()
                .scale(1/myScale, 1/myScale, 1/myScale)
                .rotateZ(-myAngle)
                .translate(-myPos.getX(), -myPos.getY(), -myPos.getZ());
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
        return viewFrame;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            if (e.isShiftDown())
                myAngle += 5;
            else
                myPos = new Point3D(myPos.getX() - 0.1f, myPos.getY(), myPos.getZ());  
            break;
            
        case KeyEvent.VK_RIGHT:
            if (e.isShiftDown())
                myAngle -= 5;
            else
                myPos = new Point3D(myPos.getX() + 0.1f, myPos.getY(), myPos.getZ());                
            break;

        case KeyEvent.VK_DOWN:
            if (e.isShiftDown())
                myScale *= 1.1;
            else
                myPos = new Point3D(myPos.getX(), myPos.getY() - 0.1f, myPos.getZ()); //Might want to change z instead
            break;

        case KeyEvent.VK_UP:
            if (e.isShiftDown())
                myScale /= 1.1;
            else
                myPos = new Point3D(myPos.getX(), myPos.getY() + 0.1f, myPos.getZ());
            break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

}
