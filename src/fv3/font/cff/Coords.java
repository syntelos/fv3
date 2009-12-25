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

/**
 * Main coordinate system class for glyphs...
 * You'll need to use this class if using the renderer...
 * @author Tim Tyler
 * @author John Pritchard
 */
public class Coords
    implements CoordsConstants, Cloneable
{
    private int width = -1;
    private int height = -1;

    private int aa_width; // result after anti-aliasing...
    private int aa_height; // result after anti-aliasing...

    private int one_pixel_width; // derived...
    private int one_pixel_height; // derived...

    private int x_scale = -1; // derived...
    private int y_scale = -1; // derived...

    private int aa_dx; // derived...
    private int aa_dy; // derived...


    public Coords(int width, int height, int aa_width, int aa_height) {
        super();
        setWidth(width);
        setHeight(height);
        setAAWidth(aa_width);
        setAAHeight(aa_height);
    }


    public void setWidth(int width) {
        this.width = width;
        this.x_scale = width;
        this.one_pixel_width = FACTOR_X / width;
    }
    public void setHeight(int height) {
        this.height = height;
        this.y_scale = height;
        this.one_pixel_height = FACTOR_Y / height;
    }
    public void setAAWidth(int aa_width) {
        this.aa_width = aa_width;
        this.aa_dx = FACTOR_X / aa_width;
    }
    public void setAAHeight(int aa_height) {
        this.aa_height = aa_height;
        this.aa_dy = FACTOR_Y / aa_height;
    }
    public Coords setAASize(int x, int y) {
        return new Coords(width, height, x, y);
    }

    /** 0000-FFFF -> pixels */
    public int scaleX(int x) {
        return (x * x_scale) >> LOG_FACTOR_X;
    }

    /** 0000-FFFF -> pixels */
    public int scaleY(int y) {
        return (y * y_scale) >> LOG_FACTOR_Y;
    }

    /** pixels -> 0000-FFFF */
    public int rescaleX(int x) {
        return (x << LOG_FACTOR_X) / x_scale;
    }

    /** pixels -> 0000-FFFF */
    public int rescaleY(int y) {
        return (y << LOG_FACTOR_Y) / y_scale;
    }

    public int nearestX(int x, int v) {
        x += (aa_dx >> 1) + v;
        return (((x * aa_width) & MASK_X) / aa_width) - v;
    }

    public int nearestY(int y, int v) {
        y += (aa_dy >> 1) + v;
        return (((y * aa_height) & MASK_Y) / aa_height) - v;
    }

    public int getAAHeight() {
        return aa_height;
    }

    public int getAAWidth() {
        return aa_width;
    }

    public int getHeight() {
        return height;
    }

    public int getOnePixelHeight() {
        return one_pixel_height;
    }

    public int getOnePixelWidth() {
        return one_pixel_width;
    }

    public int getWidth() {
        return width;
    }

    public int getXScale() {
        return x_scale;
    }

    public int getYScale() {
        return y_scale;
    }

    public void refresh() {
        setWidth(-99);
        setHeight(-99);
    }

    public void setXScale(int x_scale) {
        this.x_scale = x_scale;
    }

    public void setYScale(int y_scale) {
        this.y_scale = y_scale;
    }

    public void dump() {
        System.err.println("width:" + width+ ", height:" + height+ ", aa_width:" + aa_width+ ", aa_height:" + aa_height);
    }

    public Object clone(){
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException exc){
            throw new InternalError();
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        else if (!(o instanceof Coords)) {
            return false;
        }
        else {
            Coords c = (Coords) o;

            if (c.width != width) {
                return false;
            }
            else if (c.height != height) {
                return false;
            }
            else if (c.aa_width != aa_width) {
                return false;
            }
            else if (c.aa_height != aa_height) {
                return false;
            }
            else if (c.x_scale != x_scale) {
                return false;
            }
            else if (c.y_scale != y_scale) {
                return false;
            }
            else 
                return true;
        }
    }

    public int hashCode() {
        return width ^ (height << 8) ^ (aa_width << 16) ^ (aa_height << 24) ^ (x_scale << 14) ^ (y_scale << 22);
    }
}
