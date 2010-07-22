/*
 * fv3
 * Copyright (C) 2010, John Pritchard, all rights reserved.
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fv3.csg.u;

import fv3.csg.Solid;
import fv3.math.Vector;

/**
 * CSG algorithm developed following
 * 
 * Laidlaw, Trumbore and Hughes, 1986, "Constructive Solid Geometry
 * for Polyhedral Objects"</a>, and
 * 
 * Philip M. Hubbard, 1990, "Constructive Solid Geometry for
 * Triangulated Polyhedra" (an improvement on Laidlaw, Trumbore and
 * Hughes for triangles), and
 * 
 * with adaptations suggested by that paper, and with the study and
 * application of the work of Danilo Balby Silva Castanheira in
 * UnBBoolean/J3DBool.
 *
 * <h3>Introduction</h3>
 * 
 * Certainly the most complex aspect of the code in these classes is
 * the symmetry of an intersection segment between two faces from each
 * of two solids.  
 * 
 * The intersection of two faces has two points, with many cases for
 * their representation.  Combined with symmetry, the reasonable
 * performance programming model is fairly intensive.
 * 
 * Each intersection segment is a member of two faces from each of the
 * two solids under operation.  The programming notation within the
 * Segment class is defined in terms of the CSG operand solids ("A"
 * and "B"), but this convenience is misleading.  The segment is
 * symmetric, and employed in the triangulation of each face ("A" and
 * "B") independently throughout the triangulation code.
 * 
 * In simple triangulation, TriangulateV or TriangulateE, this
 * symmetry is described by the grouping of equivalent "A" and "B"
 * cases.  For example, endpoint vertex kind "AA" and "BA" are grouped
 * into one case, and endpoint edge kind "AAB" and "BAB" are grouped
 * into one case.
 * 
 * For multi-segment triangulation in the TriangulateM family, the
 * Segment and Endpoint classes define methods that require a face
 * argument to relate perspective.
 * 
 * <h3>Design</h3>
 * 
 * This class is intended to develop one solution set for intersection
 * and triangulation.
 * 
 * <h3>Path Order</h3>
 * 
 * Path order is used for sorting vertices, faces and segments.  It is
 * defined in terms of the comparison function, which has the range
 * {-1,0,+1} for less than, equal to, or greater than (two objects in
 * the same class: vertex, face or segment).
 * 
 * Equivalence is tested in the epsilon equivalence (equivalent within
 * radius epsilon) of coordinate values.
 * 
 * Difference is defined as a normalized vector angular separation for
 * positive and negative angles.
 * 
 * <h3>Implementation</h3>
 * 
 * The code in these classes is intended to perform error checking and
 * to throw runtime exceptions from the outer layers, and to throw
 * cast or array or pointer exceptions from the inner (self
 * referential) layers.  This policy is good for performance and
 * produces a good indication of severity.
 * 
 * The Intersection class constructor catches Illegal Argument
 * exceptions from the Segment class constructor in order to ignore an
 * empty intersection segment.  Therefore exceptions created to
 * indicate error conditions -- in code called from the Segment
 * constructor -- will throw Illegal State exceptions.  
 *
 * @see http://docs.google.com/document/edit?id=1uIZzKy_P6XTZMJ0-ciZeToiMkqUNzK5WejArjX0-BgI
 * @see A
 * @see Segment
 * @see Triangulate
 * 
 * @author John Pritchard
 */
public final class AH
    extends A
{

    /**
     * Perform operation.  Subsequently requires call to method
     * "destroy".
     */
    public AH(Solid.Construct op, Solid a, Solid b){
        super(op,a,b);
        /*
         * Performs triangulation and classification
         */
        for (Face f : this.inA){

            f.triangulate(a);
        }
        for (Face f : this.inB){

            f.triangulate(b);
        }
        /*
         */
        switch (op){

        case Union:

            for (Face aFace: a){

                if (aFace.is(State.Outside)){

                    r.addC(aFace);
                }
            }

            for (Face bFace: b){

                if (bFace.is(State.Outside)){

                    r.addC(bFace);
                }
            }
            return ;

        case Intersection:

            for (Face aFace: a){

                if (aFace.is(State.Inside)){

                    r.addC(aFace);
                }
            }

            for (Face bFace: b){

                if (bFace.is(State.Inside)){

                    r.addC(bFace);
                }
            }
            return ;

        case Difference:

            InvertInsideFaces(b);

            for (Face aFace: a){

                if (aFace.is(State.Outside)){

                    r.addC(aFace);
                }
            }

            for (Face bFace: b){

                if (bFace.is(State.Inside)){

                    r.addC(bFace);
                }
            }
            return ;
        default:
            throw new IllegalStateException();
        }
    }
}
