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
package fv3.font;

import fv3.font.cff.InstructionStream;

/**
 * @author Tim Tyler
 * @author John Pritchard
 */
public class CFFPointList
    extends Object
    implements fv3.font.cff.PathConstants
{
    private static final int INCREMENT = 16;


    private int number = 0;
    private int number_of_points = 0;
    private CFFPoint[] points = new CFFPoint[number_of_points];


    public CFFPointList(){
        super();
    }


    public boolean alive(){
        return (null != this.points);
    }
    public void destroy(){
        this.points = null;
    }
    public CFFPoint add(InstructionStream is) {

        return this.add(new CFFPoint(is));
    }
    public CFFPoint add(CFFPoint p) {
        if (number >= number_of_points) {
            CFFPoint[] new_array = new CFFPoint[number_of_points + INCREMENT];
            System.arraycopy(points, 0, new_array, 0, number_of_points);
            points = new_array;
            number_of_points += INCREMENT;
        }
        points[number] = p;
        return points[number++];
    }

    public int getMinX() {
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];

            if (p.getX() <= min) {
                min = p.getX();
            }
        }
        return min;
    }

    public int getMaxX() {
        int max = 0;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];

            if (p.getX() >= max) {
                max = p.getX();
            }
        }

        return max;
    }

    public int getMinY() {
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];

            if (p.getY() <= min) {
                min = p.getY();
            }
        }
        return min;
    }

    public int getMaxY() {
        int max = 0;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];

            if (p.getY() >= max) {
                max = p.getY();
            }
        }
        return max;
    }

    public void translate(int dx, int dy) {
        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];

            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
        }
    }

    // min---centre---max
    public void rescaleRangeX(int min, int centre, int max, int new_centre) {
        if (new_centre < centre) {
            rescaleWithFixedLeft(min, centre, new_centre);
            rescaleWithFixedRight(max, centre, new_centre);
        }
        if (centre < new_centre) {
            rescaleWithFixedRight(max, centre, new_centre);
            rescaleWithFixedLeft(min, centre, new_centre);
        }
    }

    // min---centre---max
    public void rescaleRangeY(int min, int centre, int max, int new_centre) {
        if (new_centre < centre) {
            rescaleWithFixedTop(min, centre, new_centre);
            rescaleWithFixedBottom(max, centre, new_centre);
        }
        if (centre < new_centre) {
            rescaleWithFixedBottom(max, centre, new_centre);
            rescaleWithFixedTop(min, centre, new_centre);
        }
    }

    public void rescaleWithFixedLeft(int fixed, int o, int n) {
        int d1 = (o - fixed) >> 1;

        if (d1 == 0) {
            return;
        }

        int d2 = (n - fixed) >> 1;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];
            int x = p.getX();

            if ((x >= fixed) && (x <= o)) {
                p.setX(fixed + ((x - fixed) * d2) / d1);
            }
        }
    }

    public void rescaleWithFixedRight(int fixed, int o, int n) {
        int d1 = (fixed - o) >> 1;

        if (d1 == 0) {
            return;
        }

        int d2 = (fixed - n) >> 1;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];
            int x = p.getX();

            if ((x >= o) && (x <= fixed)) {
                p.setX(fixed - ((fixed - x) * d2) / d1);
            }
        }
    }

    public void rescaleWithFixedTop(int fixed, int o, int n) {
        int d1 = (o - fixed) >> 1;

        if (d1 == 0) {
            return;
        }
        int d2 = (n - fixed) >> 1;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];
            int y = p.getY();

            if ((y >= fixed) && (y <= o)) {
                p.setY(fixed + ((y - fixed) * d2) / d1);
            }
        }
    }

    public void rescaleWithFixedBottom(int fixed, int o, int n) {
        int d1 = (fixed - o) >> 1;

        if (d1 == 0) {
            return;
        }
        int d2 = (fixed - n) >> 1;

        for (int i = 0; i < number; i++) {
            CFFPoint p = points[i];
            int y = p.getY();

            if ((y >= o) && (y <= fixed)) {
                p.setY(fixed - ((fixed - y) * d2) / d1);
            }
        }
    }

    private void rescaleWithFixedLeft(int fixed, int o, int n, int idx_min, int idx_max) {
        //idx_max = (idx_max + 1) % number;
        int d1 = (o - fixed) >> 1;

        if (d1 == 0) {
            return;
        }
        int d2 = (n - fixed) >> 1;

        for (int i = idx_min; i != idx_max; i = (i + 1) % number) {
            CFFPoint p = points[i];

            if ((p.getX() >= fixed) && (p.getX() <= o)) {
                p.setX(fixed + ((p.getX() - fixed) * d2) / d1);
            }
        }
    }

    private void rescaleWithFixedRight(int fixed, int o, int n, int idx_min, int idx_max) {
        //idx_max = (idx_max + 1) % number;
        int d1 = (fixed - o) >> 1;

        if (d1 == 0) {
            return;
        }
        int d2 = (fixed - n) >> 1;

        for (int i = idx_min; i != idx_max; i = (i + 1) % number) {
            CFFPoint p = points[i];

            if ((p.getX() >= o) && (p.getX() <= fixed)) {
                p.setX(fixed - ((fixed - p.getX()) * d2) / d1);
            }
        }
    }

    private void rescaleWithFixedTop(int fixed, int o, int n, int idx_min, int idx_max) {
        int d1 = (o - fixed) >> 1;

        if (d1 == 0) {
            return;
        }

        int d2 = (n - fixed) >> 1;

        for (int i = idx_min; i != idx_max; i = (i + 1) % number) {
            CFFPoint p = points[i];

            if ((p.getY() >= fixed) && (p.getY() <= o)) {
                p.setY(fixed + ((p.getY() - fixed) * d2) / d1);
            }
        }
    }

    private void rescaleWithFixedBottom(int fixed, int o, int n, int idx_min, int idx_max) {
        int d1 = (fixed - o) >> 1;

        if (d1 == 0) {
            return;
        }

        int d2 = (fixed - n) >> 1;

        for (int i = idx_min; i != idx_max; i = (i + 1) % number) {
            CFFPoint p = points[i];

            if ((p.getY() >= o) && (p.getY() <= fixed)) {
                p.setY(fixed - ((fixed - p.getY()) * d2) / d1);
            }
        }
    }

    public void scaleRangeX(int index_fixed, int index_moving, int new_pos, int index_min, int index_max) {
        int x1 = points[index_fixed].getX();
        int x2 = points[index_moving].getX();

        if (x1 > x2) {
            rescaleWithFixedRight(x1, x2, new_pos, index_min, index_max);
        }
        else if (x2 > x1) {
            rescaleWithFixedLeft(x1, x2, new_pos, index_min, index_max);
        }
    }

    public void scaleRangeY(int index_fixed, int index_moving, int new_pos, int index_min, int index_max) {
        int y1 = points[index_fixed].getY();
        int y2 = points[index_moving].getY();

        if (y1 > y2) {
            rescaleWithFixedBottom(y1, y2, new_pos, index_min, index_max);
        }
        else if (y2 > y1) {
            rescaleWithFixedTop(y1, y2, new_pos, index_min, index_max);
        }
    }
    public void translate(InstructionStream is, int dx, int dy){
        for (int i = 0; i < number; i++) {

            is.translatePoint(points[i].getInstructionPointer(),dx,dy);
        }
    }
    public void scale(InstructionStream is, float dx, float dy){
        for (int i = 0; i < number; i++) {

            is.scalePoint(points[i].getInstructionPointer(),dx,dy);
        }
    }
    public boolean contains(CFFPoint p) {
        return indexOf(p) >= 0;
    }

    public int indexOf(CFFPoint p) {
        for (int i = 0; i < number; i++) {
            if (p.equals(points[i])) {
                return i;
            }
        }

        return -1;
    }

    public int getIndexOf(CFFPoint fepoint) {
        for (int i = 0; i < number; i++) {
            if (fepoint == points[i]) {
                return i;
            }
        }

        return -1;
    }

    public void adjustInstructionsFromPoints(InstructionStream is) {
        for (int i = 0; i < number; i++) {
            CFFPoint fepoint = points[i];
            int ip = fepoint.getInstructionPointer();

            is.setInstructionAt(ip++, fepoint.getX() >> 2);
            is.setInstructionAt(ip++, fepoint.getY() >> 2);
        }
    }

    public boolean hasX(int x) {
        for (int i = 0; i < number; i++) {
            CFFPoint fepoint = points[i];

            if (fepoint.getX() == x) {
                return true;
            }
        }
        return false;
    }

    public boolean hasY(int y) {
        for (int i = 0; i < number; i++) {
            CFFPoint fepoint = points[i];

            if (fepoint.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setPoints(CFFPoint[] points) {
        this.points = points;
    }

    public CFFPoint getPoint(int i) {
        return points[i];
    }

    public CFFPoint safelyGetPoint(int i) {
        while (i < 0) {
            i += number;
        }
        while (i >= number) {
            i -= number;
        }

        return points[i];
    }
    public void dump() {
        System.err.printf("PointList[%d]\n", number);
    }
}
