/*
 * fv3
 * Copyright (C) 2009  John Pritchard, jdp@syntelos.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fv3.font.cff;

import fv3.font.CFFPoint;
import fv3.font.CFFPointList;


/**
 * @author Tim Tyler
 * @author John Pritchard
 */
public abstract class Curve
{
    private CFFPoint p1 = null;
    private CFFPoint p2 = null; // 
    private CFFPoint p3 = null; // 
    private CFFPoint p4 = null;

    protected double[] points;


    public Curve(){
        super();
    }


    public abstract String getName();

    public abstract CFFPoint returnStartPoint();

    public abstract CFFPoint returnEndPoint();

    public abstract void simplyAddPoints(CFFPointList fepl);

    public abstract CFFPoint returnStartControlPoint();

    public abstract CFFPoint returnEndControlPoint();


    /**
     * @return (X,Y)+ list of points in this curve.  The last point of
     * a curve must be identical the first point of a following
     * subsequent curve.
     */
    public double[] points(){
        return this.points;
    }
    protected Curve addPoint(double x, double y){
        double[] points = this.points;
        if (null == points)
            this.points = new double[]{x,y};
        else {
            int len = points.length;
            double[] copier = new double[len+2];
            System.arraycopy(points,0,copier,0,len);
            copier[len] = x;
            len += 1;
            copier[len] = y;
            this.points = copier;
        }
        return this;
    }
    public void setP1(CFFPoint p1) {
        this.p1 = p1;
    }
    public CFFPoint getP1() {
        return p1;
    }
    void setP2(CFFPoint p2) {
        this.p2 = p2;
    }
    CFFPoint getP2() {
        return p2;
    }
    void setP3(CFFPoint p3) {
        this.p3 = p3;
    }
    CFFPoint getP3() {
        return p3;
    }
    public void setP4(CFFPoint p4) {
        this.p4 = p4;
    }
    public CFFPoint getP4() {
        return p4;
    }
    public boolean isStraight() {
        return false;
    }
    public void dump() {
        System.err.printf("    Curve %s (%d,%d)\n", getName(), p1.getX(), p1.getY());
    }
}
