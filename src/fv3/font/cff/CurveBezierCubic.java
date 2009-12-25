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
public class CurveBezierCubic
    extends Curve 
    implements CurveConstants
{
    private final static int DT = (4096 / STEPS_IN_CUBIC_BEZIER);


    public CurveBezierCubic(CFFPoint p1, CFFPoint p2, CFFPoint p3, CFFPoint p4) {
        super();
        this.setP1(p1);
        this.setP2(p2);
        this.setP3(p3);
        this.setP4(p4);

        double x0 = getP1().getX();
        double y0 = getP1().getY();
        double x1 = getP2().getX();
        double y1 = getP2().getY();
        double x2 = getP3().getX();
        double y2 = getP3().getY();
        double x3 = getP4().getX();
        double y3 = getP4().getY();
        double x_a = -x0 + 3 * x1 - 3 * x2 + x3;
        double x_b = 3 * x0 - 6 * x1 + 3 * x2;
        double x_c = -3 * x0 + 3 * x1;
        double x_d = x0;
        double y_a = -y0 + 3 * y1 - 3 * y2 + y3;
        double y_b = 3 * y0 - 6 * y1 + 3 * y2;
        double y_c = -3 * y0 + 3 * y1;
        double y_d = y0;

        this.addPoint(x0, y0);

        for (int dt = DT, t = dt; t < 4096; t += dt){

            double x = ((((((((x_a * t) / 4096.0) + x_b) * t) / 4096.0) + x_c) * t) / 4096.0) + x_d;
            double y = ((((((((y_a * t) / 4096.0) + y_b) * t) / 4096.0) + y_c) * t) / 4096.0) + y_d;

            this.addPoint(x, y);
        }
    }


    public String getName() {
        return "CubicBezier";
    }
    public void simplyAddPoints(CFFPointList fepl) {
        fepl.add(getP2());
        fepl.add(getP3());
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
