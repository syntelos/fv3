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
import fv3.csg.u.Segment.Endpoint.Kind.Edge.*;
import fv3.csg.u.Segment.Endpoint.Kind.Vertex.*;
import fv3.csg.u.Segment.Path.Kind.*;
import fv3.math.Vector;

/**
 * The functions defined here perform face splitting.  They are called
 * from the {@link Face#triangulate Face triangulate} method.
 * 
 * The {@link Segment} class defines and manages segment endpoints.
 * That code with the code defined here handles both simple and
 * complex face segmentation cases.
 * 
 * The code in these classes has cases that appear to be loose or
 * incorrect, but are intended to be correct under geometric
 * reachability.  That is, geometrically valid input is unable to
 * violate the correct operation of the code.  Or in other words,
 * geometrically valid input is unable to reach incorrect paths of
 * execution in the code.  
 * 
 * The code is designed for readability and correctness over a large
 * number of geometric use cases.  For example, point equivalences due
 * to the symmetry of intersection (A1 == B1), and the equivalence of
 * points of intersection with vertices (B1 == Face.A), are multiplied
 * by the uses of a point of intersection.
 *
 * The simple "V" and "E" cases have one intersection line segment
 * that splits the face.  These cases depend on both endpoints being
 * defined in terms of the face to be split.  The correct operation of
 * this code has been designed in terms of cases geometrically
 * reachable.
 * 
 * The complex "M" cases are generalized.  Rotating the face triangle
 * vertices for the segmentation case, these functions do not employ
 * the endpoint classifiers which would have value "M".
 * 
 * @see AH
 * @author John Pritchard
 */
public final class Triangulate
    extends java.lang.Object
{

    /**
     * Principal triangulation segment case identity.
     * 
     * Segment kind: member of a multiple segment path,
     * vertex-edge, edge-vertex, or edge-edge.  These are
     * semantically equivalent to "vertex to edge" or "edge to
     * edge" (as exist for a non empty, one segment triangle
     * bisection), but symbolically differentiated for the
     * compiler.
     */
    public enum Kind {

        M, VE, EV, EE;

        /**
         * @return Triangulation segment case for face.
         */
        public final static Kind For(Face f, Segment.Endpoint e1, Segment.Endpoint e2){

            if (null == e1 || null == e2)
                throw new IllegalArgumentException();

            else if (e1.isVertexOn(f)){

                if (e2.isEdgeOn(f))

                    return VE;
                else
                    return M;
            }
            else if (e2.isVertexOn(f)){

                if (e1.isEdgeOn(f))

                    return EV;
                else
                    return M;
            }
            else if (e1.isEdgeOn(f) && e2.isEdgeOn(f))
                return EE;
            else
                return M;
        }
    }

    /**
     * Singular intersection including a vertex of the argument face.
     */
    protected final static Face[] V(Face f, Solid s, Segment.Endpoint e1, Segment.Endpoint e2)
    {
        if (f.alive()){

            assert e1.sameKind(e2);

            final Vertex a = f.a;
            final Vertex b = f.b;
            final Vertex c = f.c;

            switch ((Segment.Endpoint.Kind.Vertex)e1.kind){
            case AA:
            case BA:
                switch((Segment.Endpoint.Kind.Edge)e2.kind){
                case AAB:
                case BAB:
                    throw new IllegalStateException("Triangulate.V(A,AB)");

                case ABC:
                case BBC:{

                    Vertex bc = e2.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy("Triangulate.V(A,BC)"), a, b,bc).classify(b),
                        new Face(s,f.name.copy("Triangulate.V(A,BC)"), a,bc, c).classify(c)
                    };
                }
                case ACA:
                case BCA:
                    throw new IllegalStateException("Triangulate.V(A,CA)");

                default:
                    throw new IllegalStateException();
                }

            case AB:
            case BB:
                switch((Segment.Endpoint.Kind.Edge)e2.kind){
                case AAB:
                case BAB:
                    throw new IllegalStateException("Triangulate.V(A,AB)");

                case ABC:
                case BBC:
                    throw new IllegalStateException("Triangulate.V(A,CA)");

                case ACA:
                case BCA:{

                    Vertex ca = e2.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy("Triangulate.V(B,CA)"), b, c,ca).classify(c),
                        new Face(s,f.name.copy("Triangulate.V(B,CA)"), b,ca, a).classify(a)
                    };
                }
                default:
                    throw new IllegalStateException();
                }

            case AC:
            case BC:
                switch((Segment.Endpoint.Kind.Edge)e2.kind){
                case AAB:
                case BAB:{

                    Vertex ab = e2.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy("Triangulate.V(C,AB)"), c, a,ab).classify(a),
                        new Face(s,f.name.copy("Triangulate.V(C,AB)"), c,ab, b).classify(b)
                    };
                }
                case ABC:
                case BBC:
                    throw new IllegalStateException("Triangulate.V(C,BC)");

                case ACA:
                case BCA:
                    throw new IllegalStateException("Triangulate.V(C,CA)");

                default:
                    throw new IllegalStateException();
                }

            default:
                throw new IllegalStateException();
            }
        }
        return null;
    }
    /**
     * Singular intersection across edges of the argument face.
     */
    protected final static Face[] E(Face f, Solid s, Segment.Endpoint e1, Segment.Endpoint e2)
    {
        if (f.alive()){

            assert e1.sameKind(e2);

            final Vertex a = f.a;
            final Vertex b = f.b;
            final Vertex c = f.c;

            Vertex ab, bc, ca;

            String nn;

            switch((Segment.Endpoint.Kind.Edge)e1.kind){
            case AAB:
            case BAB:
                switch((Segment.Endpoint.Kind.Edge)e2.kind){
                case AAB:
                case BAB:
                    throw new IllegalStateException("Triangulate.E(AB,AB)");

                case ABC:
                case BBC:
                    /*
                     * Case "B"
                     */
                    nn = "Triangulate.E(AB,BC)";
                    ab = e1.vertex;
                    bc = e2.vertex;
                    ca = a.midpoint(c);

                    return new Face[]{
                        new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                        new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                        new Face(s,f.name.copy(nn),ca,ab,bc).classify(a),
                        new Face(s,f.name.copy(nn), c,ca,bc).classify(a)
                    };
                case ACA:
                case BCA:
                    /*
                     * Case "A"
                     */
                    nn = "Triangulate.E(AB,CA)";
                    ab = e1.vertex;
                    bc = b.midpoint(c);
                    ca = e2.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                        new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                        new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                        new Face(s,f.name.copy(nn), c,ca,bc).classify(b)
                    };
                default:
                    throw new IllegalStateException();
                }

            case ABC:
            case BBC:
                switch((Segment.Endpoint.Kind.Edge)e2.kind){
                case AAB:
                case BAB:
                    /*
                     * Case "B"
                     */
                    nn = "Triangulate.E(BC,AB)";
                    ab = e2.vertex;
                    ca = a.midpoint(c);
                    bc = e1.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                        new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                        new Face(s,f.name.copy(nn),ca,ab,bc).classify(a),
                        new Face(s,f.name.copy(nn), c,ca,bc).classify(a)
                    };
                case ABC:
                case BBC:
                    throw new IllegalStateException("Triangulate.E(BC,BC)");

                case ACA:
                case BCA:
                    /*
                     * Case "C"
                     */
                    nn = "Triangulate.E(BC,CA)";
                    ab = a.midpoint(b);
                    bc = e1.vertex;
                    ca = e2.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy(nn), a,ab,ca).classify(b),
                        new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                        new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                        new Face(s,f.name.copy(nn), c,ca,bc).classify(c)
                    };
                default:
                    throw new IllegalStateException();
                }

            case ACA:
            case BCA:
                switch((Segment.Endpoint.Kind.Edge)e2.kind){
                case AAB:
                case BAB:
                    /*
                     * Case "A"
                     */
                    nn = "Triangulate.E(CA,AB)";
                    ab = e2.vertex;
                    bc = b.midpoint(c);
                    ca = e1.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy(nn), a,ab,ca).classify(a),
                        new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                        new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                        new Face(s,f.name.copy(nn), c,ca,bc).classify(b)
                    };
                case ABC:
                case BBC:
                    /*
                     * Case "C"
                     */
                    nn = "Triangulate.E(CA,BC)";
                    ab = a.midpoint(b);
                    bc = e2.vertex;
                    ca = e1.vertex;

                    return new Face[]{
                        new Face(s,f.name.copy(nn), a,ab,ca).classify(b),
                        new Face(s,f.name.copy(nn),ab, b,bc).classify(b),
                        new Face(s,f.name.copy(nn),ca,ab,bc).classify(b),
                        new Face(s,f.name.copy(nn), c,ca,bc).classify(c)
                    };
                case ACA:
                case BCA:
                    throw new IllegalStateException("Triangulate.E(CA,CA)");

                default:
                    throw new IllegalStateException();
                }

            default:
                throw new IllegalStateException();
            }
        }
        return null;
    }
    protected final static Face[] M(Face f, Solid s){
        if (f.alive()){

            Segment.Path p;
            try {
                p = new Segment.Path(f);
            }
            catch (IllegalStateException exc){

                throw new 
                    fv3.model.Debugger( f.getName().toString(), f.debugger());
            }

            switch (p.kind){
            case VIE:
                return Triangulate.M_VIE(f, s, p);
            case VTE:
                return Triangulate.M_VTE(f, s, p);
            case VTV:
                return Triangulate.M_VTV(f, s, p);
            case EIV:
                return Triangulate.M_EIV(f, s, p);
            case ETV:
                return Triangulate.M_ETV(f, s, p);
            case EIE:
                return Triangulate.M_EIE(f, s, p);
            case ETE:
                return Triangulate.M_ETE(f, s, p);

            default:
                throw new IllegalStateException();
            }
        }
        return null;
    }
    private final static Face[] M_VIE(Face f, Solid s, Segment.Path p){
        Face[] replacements = null;

        final int count = f.countMembership();

        final boolean outbound = p.start.isOutbound(p.end);

        final Vertex a = p.start.vertex;
        final Vertex b = f.next(a);
        final Vertex c = f.next(b);
        final Vertex e = p.end.vertex;

        final Vertex m1 = a.midpoint(e);
        {
            m1.status = p.sclass(f,m1);
        }

        if (2 == count){

            final Vertex o = p.startEndpoint2(f);

            final Vertex m = b.midpoint(c).classify(c);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/O)"),a,e,o).classify(m1),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/O)"),a,o,c).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/O)"),c,o,m).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/O)"),e,m,o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/O)"),e,b,m).classify(b)
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/I)"),a,o,e).classify(m1),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/I)"),a,c,o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/I)"),c,m,o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/I)"),e,o,m).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(2/I)"),e,m,b).classify(b)
                };
            }
        }
        else {
            final int term = (count-1);

            final Vertex m2 = b.midpoint(c).classify(c);

            Vertex o;

            o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VIE(N/O)"), a, o, c).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(N/O)"), a,m1, o).classify(c),
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VIE(N/I)"), a, c, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VIE(N/I)"), a, o,m1).classify(c),
                };
            }

            for (int sc = 1; sc < term; sc++){
                Segment se = p.list[sc];
                Vertex o1 = se.endpoint1(f).vertex;
                Vertex o2 = se.endpoint2(f).vertex;

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_VIE(N/O)"),o1,m1,o2).classify(m1),
                            new Face(s,f.name.copy("Triangulate.M_VIE(N/O)"),o1,o2, c).classify(c),
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_VIE(N/I)"),o1,o2,m1).classify(m1),
                            new Face(s,f.name.copy("Triangulate.M_VIE(N/I)"),o1, c,o2).classify(c),
                        });
                }
            }

            o = p.endEndpoint1(f);

            if (outbound){
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_VIE(N/O)"),m1, e, o).classify(m1),
                        new Face(s,f.name.copy("Triangulate.M_VIE(N/O)"), o, e,m2).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_VIE(N/O)"),m2, e, b).classify(b)
                    });
            }
            else {
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_VIE(N/I)"),m1, o, e).classify(m1),
                        new Face(s,f.name.copy("Triangulate.M_VIE(N/I)"), o,m2, e).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_VIE(N/I)"),m2, b, e).classify(b)
                    });
            }
        }

        return replacements;
    }
    private final static Face[] M_EIV(Face f, Solid s, Segment.Path p){
        ////////////////////////////////
        ////////////////////////////////
        ////////////////////////////////
        return null;
    }
    private final static Face[] M_VTE(Face f, Solid s, Segment.Path p){
        Face[] replacements = null;

        final int count = f.countMembership();

        final boolean outbound = p.start.isOutbound(p.end);

        final Vertex a = p.start.vertex;
        final Vertex b = f.next(a);
        final Vertex c = f.next(b);
        final Vertex e = p.end.vertex;

        final Vertex m1 = a.midpoint(b).classify(b);
        final Vertex m2 = c.midpoint(a).classify(c);

        if (2 == count){

            final Vertex o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"), a, o,m2).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"), a,m1, o).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"), o, e,m2).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"), o,m1, e).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"),m2, e, c).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"),m1, e, b).classify(b)
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"), a,m2, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"), a, o,m1).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"), o,m2, e).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"), o, e,m1).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"),m2, c, e).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"),m1, b, e).classify(b)
                };
            }
        }
        else {
            final int term = (count-1);

            Vertex o;

            o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"), a, o,m2).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/O)"), a,m1, o).classify(b)
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"), a,m2, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_VTE(2/I)"), a, o,m1).classify(b)
                };
            }

            for (int sc = 1; sc < term; sc++){
                Segment se = p.list[sc];
                Vertex o1 = se.endpoint1(f).vertex;
                Vertex o2 = se.endpoint2(f).vertex;

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_VTE(N/O)"),o1,m1,o2).classify(b),
                            new Face(s,f.name.copy("Triangulate.M_VTE(N/O)"),o1,o2,m2).classify(c),
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_VTE(N/I)"),o1,o2,m1).classify(b),
                            new Face(s,f.name.copy("Triangulate.M_VTE(N/I)"),o1,m2,o2).classify(c),
                        });
                }
            }

            o = p.endEndpoint1(f);

            if (outbound){
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/O)"),m1, o, b).classify(b),
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/O)"), o, b, e).classify(b),
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/O)"),m2, o, e).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/O)"),m2, e, c).classify(c)
                    });
            }
            else {
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/I)"),m1, b, o).classify(b),
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/I)"), o, e, b).classify(b),
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/I)"),m2, e, o).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_VTE(N/I)"),m2, c, e).classify(c)
                    });
            }
        }
        return replacements;
    }
    private final static Face[] M_ETV(Face f, Solid s, Segment.Path p){
        ////////////////////////////////
        ////////////////////////////////
        ////////////////////////////////
        return null;
    }
    private final static Face[] M_EIE(Face f, Solid s, Segment.Path p){
        Face[] replacements = null;

        final int count = f.countMembership();

        final Vertex e1 = p.start.vertex;
        final Vertex e2 = p.end.vertex;
        final Vertex a = p.startFaceVertexForEdgeIn1(f);
        final Vertex b = f.next(a);
        final Vertex c = f.next(b);

        final Vertex m1 = c.midpoint(a).classify(c);
        final Vertex m2 = b.midpoint(c).classify(c);
        final Vertex m3 = e1.midpoint(e2);
        {
            m3.status = p.sclass(f,m3);
        }
        final boolean outbound = p.startIsOutboundFromFaceVertexForEdgeIn1(f,a);

        if (2 == count){

            final Vertex o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"), a,e1,m1).classify(a),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),e1, o,m1).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),m1, o, c).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"), c, o,m2).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"), o,e2,m2).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),e2, b,m2).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),e1,e2, o).classify(m3)
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"), a,m1,e1).classify(a),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"),e1,m1, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"),m1, c, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"), c,m2, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"), o,m2,e2).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"),e2,m2, b).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"),e1, o,e2).classify(m3)
                };
            }
        }
        else {
            final int term = (count-1);

            Vertex o;

            o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"), a,e1,m1).classify(a),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),e1, o,m1).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),m1, o, c).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),e1, o,m3).classify(c),
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"), a,m1,e1).classify(a),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"),e1,m1, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/I)"),m1, c, o).classify(c),
                    new Face(s,f.name.copy("Triangulate.M_EIE(2/O)"),e1,m3, o).classify(c),
                };
            }

            for (int sc = 1; sc < term; sc++){
                Segment se = p.list[sc];
                Vertex o1 = se.endpoint1(f).vertex;
                Vertex o2 = se.endpoint2(f).vertex;

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_EIE(N/O)"),o1,m3,o2).classify(m3),
                            new Face(s,f.name.copy("Triangulate.M_EIE(N/O)"),o1,o2, c).classify(c)
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_EIE(N/I)"),o1,o2,m3).classify(m3),
                            new Face(s,f.name.copy("Triangulate.M_EIE(N/I)"),o1, c,o2).classify(c)
                        });
                }
            }

            o = p.endEndpoint1(f);

            if (outbound){
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/O)"),m3,e2, o).classify(m3),
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/O)"), o,e2,m2).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/O)"), o,m2, c).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/O)"),e2,m2, b).classify(b)
                    });
            }
            else {
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/I)"),m3, o,e2).classify(m3),
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/I)"), o,m2,e2).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/I)"), o, c,m2).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_EIE(N/I)"),e2, b,m2).classify(b)
                    });
            }
        }
        return replacements;
    }
    private final static Face[] M_ETE(Face f, Solid s, Segment.Path p){
        Face[] replacements = null;

        final int count = f.countMembership();

        final Vertex e1 = p.start.vertex;
        final Vertex e2 = p.end.vertex;
        final Vertex a = p.startFaceVertexForEdgeIn1(f);
        final Vertex b = f.next(a);
        final Vertex c = f.next(b);

        final Vertex m = c.midpoint(a);
        {
            m.status = p.sclass(f,m);
        }

        final boolean outbound = p.startIsOutboundFromFaceVertexForEdgeIn1(f,a);

        if (2 == count){

            final Vertex o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"), a,e1, m).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"),e1, o, m).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"), m, o,e2).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"), m,e2, c).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"),e1, b, o).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"), o, b,e2).classify(b)
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"), a, m,e1).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"),e1, m, o).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"), m,e2, o).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"), m, c,e2).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"),e1, o, b).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"), o,e2, b).classify(b)
                };
            }
        }
        else {
            final int term = (count-1);

            Vertex o;

            o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"), a,e1, m).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"),e1, o, m).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/O)"),e1, b, o).classify(b)
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"), a, m,e1).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"),e1, m, o).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_ETE(2/I)"),e1, o, b).classify(b)
                };
            }

            for (int sc = 1; sc < term; sc++){
                Segment se = p.list[sc];
                Vertex o1 = se.endpoint1(f).vertex;
                Vertex o2 = se.endpoint2(f).vertex;

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_ETE(N/O)"),o1,o2, m).classify(m),
                            new Face(s,f.name.copy("Triangulate.M_ETE(N/O)"),o1, b,o2).classify(b)
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_ETE(N/I)"),o1, m,o2).classify(m),
                            new Face(s,f.name.copy("Triangulate.M_ETE(N/I)"),o1,o2, b).classify(b)
                        });
                }
            }

            o = p.endEndpoint1(f);

            if (outbound){
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_ETE(N/O)"), o,e2, m).classify(m),
                        new Face(s,f.name.copy("Triangulate.M_ETE(N/O)"), m,e2, c).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_ETE(N/O)"), o, b,e2).classify(b)
                    });
            }
            else {
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_ETE(N/I)"), o, m,e2).classify(m),
                        new Face(s,f.name.copy("Triangulate.M_ETE(N/I)"), m, c,e2).classify(c),
                        new Face(s,f.name.copy("Triangulate.M_ETE(N/I)"), o,e2, b).classify(b)
                    });
            }
        }
        return replacements;
    }
    private final static Face[] M_VTV(Face f, Solid s, Segment.Path p){
        Face[] replacements = null;

        final int count = f.countMembership();

        final boolean outbound = f.isEdgeOrder(p.start.vertex,p.end.vertex);

        final Vertex a, b, c, m;
        {
            a = p.start.vertex;
            if (outbound){
                b = p.end.vertex;
                c = f.next(b);
                m = a.midpoint(b);
            }
            else {
                c = p.end.vertex;
                b = f.next(a);
                m = c.midpoint(a);
            }
            {
                m.status = p.sclass(f,m);
            }
        }

        if (2 == count){

            final Vertex o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), a, o, m).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), m, o, c).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), a, b, o).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), c, o, b).classify(b)
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), a, m, o).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), m, c, o).classify(m),
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), a, o, b).classify(b),
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), c, b, o).classify(b)
                };
            }
        }
        else {
            final int term = (count-1);

            Vertex o;

            o = p.startEndpoint2(f);

            if (outbound){
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), a, o, m).classify(m),
                };
            }
            else {
                replacements = new Face[]{
                    new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), a, m, o).classify(m),
                };
            }

            for (int sc = 1; sc < term; sc++){
                Segment se = p.list[sc];
                Vertex o1 = se.endpoint1(f).vertex;
                Vertex o2 = se.endpoint2(f).vertex;

                if (outbound){
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), m,o1,o2).classify(m),
                            new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"),o1, b,o2).classify(b)
                        });
                }
                else {
                    replacements = Face.Cat(replacements, new Face[]{
                            new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), m,o2,o1).classify(m),
                            new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"),o1,o2, b).classify(b)
                        });
                }
            }

            o = p.endEndpoint1(f);

            if (outbound){
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), m, o, c).classify(m),
                        new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), c, o, b).classify(b)
                    });
            }
            else {
                replacements = Face.Cat(replacements, new Face[]{
                        new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), m, c, o).classify(m),
                        new Face(s,f.name.copy("Triangulate.M_VTV(2/O)"), c, b, o).classify(b)
                    });
            }
        }
        return replacements;
    }
}
