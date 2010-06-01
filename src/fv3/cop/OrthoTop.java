package fv3.cop;

import fv3.Bounds;
import fv3.Camera;
import fv3.math.Matrix;

/**
 * 
 */
public class OrthoTop
    extends Ortho
{


    public OrthoTop(Bounds.CircumSphere s){
        super(s);
    }



    public Matrix view(Camera c){

        Matrix m = c.getView();

        return m.rotateX(PI2);
    }
}
