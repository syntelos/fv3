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
public class CurveStraightLine
    extends Curve
{

    public CurveStraightLine(CFFPoint p1, CFFPoint p4) {
        super();
        this.setP1(p1);
        this.setP4(p4);
        this.points = new double[]{p1.x,p1.y,p4.x,p4.y};
    }


    public String getName() {
        return "StraightLine";
    }
    public void simplyAddPoints(CFFPointList fepl) {
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
    public boolean isStraight() {
        return true;
    }
}
