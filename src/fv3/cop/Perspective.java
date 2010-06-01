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

    protected volatile double fovy;


    /**
     * @param fovy Field of view (degrees) in Y
     */
    public Perspective(double fovy){
        super();
        if (0.0 < fovy)
            this.fovy = fovy;
        else
            throw new IllegalArgumentException();
    }


    public Matrix projection(Camera c){
        Matrix m = c.getProjection();

        return m;
    }
    public Matrix view(Camera c){
        Matrix m = c.getView();

        return m;
    }
}
