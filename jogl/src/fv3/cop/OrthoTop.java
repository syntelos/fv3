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
    protected Matrix view = new Matrix().rotateX(PI_D2);


    public OrthoTop(Bounds.CircumSphere s){
        super(s);
    }



    public Matrix view(Camera c){

        return this.view;
    }
}
