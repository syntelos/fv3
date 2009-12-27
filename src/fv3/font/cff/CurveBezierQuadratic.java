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
public class CurveBezierQuadratic
    extends Curve 
    implements CurveConstants
{
    private final static int DT = (EM / STEPS_IN_QUADRATIC_BEZIER);


    public CurveBezierQuadratic(CFFPoint p1, CFFPoint p2, CFFPoint p4) {
        super();
        this.setP1(p1);
        this.setP2(p2);
        this.setP4(p4);

        double x0 = getP1().getX();
        double y0 = getP1().getY();
        double x1 = getP2().getX();
        double y1 = getP2().getY();
        double x3 = getP4().getX();
        double y3 = getP4().getY();

        double x_b = 1 * x0 - 2 * x1 + 1 * x3;
        double x_c = -2 * x0 + 2 * x1;
        double x_d = x0;
        double y_b = 1 * y0 - 2 * y1 + 1 * y3;
        double y_c = -2 * y0 + 2 * y1;
        double y_d = y0;

        this.addPoint(x0, y0);

        for (int dt = DT, t = dt; t < EM; t += dt){

            double x = (((((x_b * t) / EM) + x_c) * t) / EM) + x_d;
            double y = (((((y_b * t) / EM) + y_c) * t) / EM) + y_d;

            this.addPoint(x, y);
        }
    }
  
    public String getName() {
        return "QuadraticBezier";
    }
    public void simplyAddPoints(CFFPointList fepl) {
        fepl.add(getP2());
        fepl.add(getP4());
    }
    public CFFPoint returnStartPoint() {
        return getP1();
    }
    public CFFPoint returnStartControlPoint() {
        return getP2();
    }
    public CFFPoint returnEndControlPoint() {
        return getP3();
    }
    public CFFPoint returnEndPoint() {
        return getP4();
    }
}
