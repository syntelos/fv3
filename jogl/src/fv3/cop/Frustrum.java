package fv3.cop;

import fv3.Bounds;
import fv3.Camera;
import fv3.math.Matrix;

/**
 * 
 */
public class Frustrum
    extends Object
    implements Camera.Operator
{

    protected volatile Bounds.CircumSphere s;

    protected volatile double near, far, left, right, top = 1, bottom = -1;

    protected volatile boolean init = true;


    public Frustrum(Bounds.CircumSphere s){
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
    public Frustrum(double near, double far){
        super();
        if (0.0 < near){
            this.near = near;
            this.far = far;
        }
        else
            throw new IllegalArgumentException("Near must be positive");
    }
    public Frustrum(double left, double right, double bottom, double top, double near, double far){
        if (0.0 < near){

            this.left = left;
            this.right = right;
            this.bottom = bottom;
            this.top = top;
            this.near = near;
            this.far = far;
        }
        else
            throw new IllegalArgumentException("Near must be positive.");
    }


    public boolean hasCircumSphere(){
        return false;
    }
    public Bounds.CircumSphere getCircumSphere(){
        return null;
    }
    protected void init(Camera c){
        if (this.init){
            this.init = false;

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
    }
    public Matrix projection(Camera c){
        this.init(c);

        Matrix m = c.getProjection();

        double N2 = (2*near);
        m.m00(N2 / (right - left));
        m.m11(N2 / (top - bottom));
        m.m02( (right + left) / (right - left));
        m.m12( (top + bottom) / (top - bottom));
        m.m22( (far + near) / (far - near));
        m.m23( (N2 * far) / (far - near));
        m.m32(-1);
        m.m33(0);

        return m;
    }
    public Matrix view(Camera c){

        return null;
    }
}
