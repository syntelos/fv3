package fv3.cop;

import fv3.Bounds;
import fv3.Camera;
import fv3.math.Matrix;

/**
 * 
 */
public class Perspective
    extends Object
    implements Camera.Operator
{

    protected final static double DEG2RAD = (Math.PI / 180);


    protected volatile Bounds.CircumSphere s;

    protected volatile double near = 1, far;

    /**
     * Field of view in Y (radians)
     */
    protected volatile double fovy;


    /**
     * @param fovy Field of view (degrees) in Y
     */
    public Perspective(double near, double far, double fovy){
        super();
        if (0.0 < near && 0.0 < fovy){
            this.near = near;
            this.far = far;
            this.fovy = (fovy * DEG2RAD);
        }
        else
            throw new IllegalArgumentException();
    }
    /**
     * @param fovy Field of view (degrees) in Y
     */
    public Perspective(Bounds.CircumSphere s, double fovy){
        super();
        if (null != s && 0.0 < fovy){
            this.s = s;
            this.far = (2* s.diameter);
            this.fovy = (fovy * DEG2RAD);
        }
        else
            throw new IllegalArgumentException();
    }


    public boolean hasCircumSphere(){
        return (null != this.s);
    }
    public Bounds.CircumSphere getCircumSphere(){
        return this.s;
    }
    public Matrix projection(Camera c){

        double a = c.getAspect();

        Matrix m = c.getProjection();

        double f = (1.0 / Math.tan(fovy/2.0));

        m.m00( f / a);
        m.m11(f);
        m.m22( (far + near) / (near - far));
        m.m23( (2 * far * near) / (near - far));
        m.m32(-1);
        m.m33(0);

        return m;
    }
    public Matrix view(Camera c){
        Matrix m = c.getView();

        return m;
    }
}
