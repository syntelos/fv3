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

    protected volatile Bounds.CircumSphere s;

    protected volatile double left, right, top = 1, bottom = -1, near = 1, far;


    public Ortho(Bounds.CircumSphere s){
        super();
        if (null != s){
            this.s = s;
            double d = s.diameter;
            double r = s.radius;
            this.left = s.midX-r;
            this.right = s.midX+r;
            this.top = s.midY+r;
            this.bottom = s.midY-r;
            this.far = (1 + d);
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

        double X = ( 2.0 / (right - left));
        double Y = ( 2.0 / (top - bottom));
        double Z = (-2.0 / (far - near));
        double Tx = ( (right + left) / (right - left));
        if (0.0 != Tx)
            Tx = -(Tx);
        double Ty = ( (top + bottom) / (top - bottom));
        if (0.0 != Ty)
            Ty = -(Ty);
        double Tz = ( (far + near) / (far - near));
        if (0.0 != Tz)
            Tz = -(Tz);

        m.m00(X);
        m.m11(Y);
        m.m22(X);
        m.m03(Tx);
        m.m13(Ty);
        m.m23(Tz);

        return m;
    }
    public Matrix view(Camera c){
//         Bounds.CircumSphere s = this.s;
//         if (null != s){
//             Matrix m = c.getView();

//             double x = s.midX;
//             double y = s.midY;
//             double z = s.midZ;

//             double t = -(s.diameter);

//             System.out.printf("Ortho (%g,%g,%g,%g)\n",x,y,z,s.diameter);

//             return m.translate(x,y,(z+t));
//         }
//         else
            return null;
    }
}