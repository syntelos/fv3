package fv3.cop;

import fv3.Bounds;
import fv3.Camera;
import fv3.math.Matrix;

/**
 * 
 */
public class OrthoLeft
    extends Ortho
{

    protected Matrix view = new Matrix().rotateY(-PI_D2);


    public OrthoLeft(Bounds.CircumSphere s){
        super(s);
    }


    public Matrix view(Camera c){

        return this.view;
    }
}
