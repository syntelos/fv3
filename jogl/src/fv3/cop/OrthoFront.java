package fv3.cop;

import fv3.Bounds;
import fv3.Camera;
import fv3.math.Matrix;

/**
 * Ortho is a synonym for this ortho front view.
 */
public class OrthoFront
    extends Ortho
{


    public OrthoFront(Bounds.CircumSphere s){
        super(s);
    }
}
