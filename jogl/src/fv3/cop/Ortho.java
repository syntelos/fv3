package fv3.cop;

import fv3.Bounds;
import fv3.Camera;
import fv3.math.Matrix;

/**
 * Base class for orthographic cameras
 * @see OrthoFront
 * @see OrthoBack
 * @see OrthoLeft
 * @see OrthoRight
 * @see OrthoTop
 * @see OrthoBottom
 */
public abstract class Ortho
    extends Object
    implements Camera.Operator
{
    protected final static double MZ = -0.0;


    protected volatile Bounds.CircumSphere s;

    protected volatile double aspect;


    public Ortho(Bounds.CircumSphere s){
        super();
        if (null != s)
            this.s = s;
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

        this.aspect = c.getAspect();

        Matrix m = c.getProjection();

        if (EPSILON < this.s.diameter){

            double ss = (2.0 / this.s.diameter);

            double Sx, Sy, Sz;

            if (this.s.hasDx())
                Sx = (ss);
            else
                Sx = 1.0;

            if (this.s.hasDy())
                Sy = (ss);
            else
                Sy = 1.0;

            if (this.s.hasDz())
                Sz = -(ss);
            else
                Sz = 1.0;

            if (1.0 != this.aspect){

                double a = (this.aspect / 2.0);

                if ( this.aspect < 1.0 )
                    Sy /= a;
                else 
                    Sx *= a;
            }

            m.m00(Sx);
            m.m11(Sy);
            m.m22(Sz);
        }
        m.m03(s.tX());
        m.m13(s.tY());
        m.m23(s.tZ());

        return m;
    }
    public Matrix view(Camera c){

        return null;
    }
}
