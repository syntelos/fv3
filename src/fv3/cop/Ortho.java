package fv3.cop;

import fv3.Bounds;
import fv3.Camera;
import fv3.math.Matrix;

/**
 * 
 */
public class Ortho
    extends Object
    implements Camera.Operator
{
    protected final static double _3 = (10.0/3.0);

    protected volatile Bounds.CircumSphere s;

    protected volatile double left, right, top = 1, bottom = -1, near = 1, far;


    public Ortho(Bounds.CircumSphere s){
        super();
        if (null != s){
            this.s = s;
            this.left = s.minX;
            this.right = s.maxX;
            this.bottom = s.minY;
            this.top = s.maxY;
            this.near = s.maxZ;
            this.far = s.minZ;
        }
        else
            throw new IllegalArgumentException();
    }
    public Ortho(double near, double far){
        super();
        if (0.0 < near){
            this.near = near;
            this.far = far;
        }
        else
            throw new IllegalArgumentException("Near must be positive");
    }
    public Ortho(double left, double right, double bottom, double top, double near, double far){
        if (0.0 < near){
            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            this.near = near;
            this.far = far;
        }
        else
            throw new IllegalArgumentException("Near must be positive");
    }


    public boolean hasCircumSphere(){
        return (null != this.s);
    }
    public Bounds.CircumSphere getCircumSphere(){
        return this.s;
    }
    protected void init(Camera c){

        double aspect = c.getAspect();

        if (0 == this.left && 0 == this.right){

            this.left = -(aspect);
            this.right = +(aspect);
        }
        else if ( aspect < 1.0 ) {
            this.bottom /= aspect;
            this.top /= aspect;
        }
        else {
            this.left *= aspect; 
            this.right *= aspect;
        }
    }
    public Matrix projection(Camera c){
        this.init(c);

        Matrix m = c.getProjection();

        double Sx = ( 2.0 / (right - left));
        double Sy = ( 2.0 / (top - bottom));
        double Sz = (-2.0 / (far - near));

        m.m00(Sx);
        m.m11(Sy);
        m.m22(Sx);

        double Tx = ( (right + left) / (right - left));
        if (0.0 != Tx)
            Tx = -(Tx);
        double Ty = ( (top + bottom) / (top - bottom));
        if (0.0 != Ty)
            Ty = -(Ty);
        double Tz = ( (far + near) / (far - near));
        if (0.0 != Tz)
            Tz = -(Tz);

        m.m03(Tx);
        m.m13(Ty);
        m.m23(Tz);

        return m;
    }
    public Matrix view(Camera c){
        /*
        Bounds.CircumSphere s = this.s;
        if (null != s){
            Matrix m = c.getView();

            double x = s.midX;
            double y = s.midY;
            double z = s.midZ;

            double t = (_3 * s.diameter);

            System.out.printf("Ortho (%g,%g,%g,%g)\n",x,y,z,s.diameter);

            return m.translate(x,y,(z+t));
        }
        else
        */
            return null;
    }
}
