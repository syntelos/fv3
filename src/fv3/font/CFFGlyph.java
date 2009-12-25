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

import fv3.font.cff.DisplayOptions;
import fv3.font.cff.InstructionStream;

/**
 * This class may be subclassed with a {@link CFFFont} subclass as for
 * implementing a text editor.
 * 
 * @author Tim Tyler
 * @author John Pritchard
 */
public class CFFGlyph
    extends Object
{

    private final CFFFont font;

    private InstructionStream instruction_stream;


    protected CFFGlyph(CFFFont font, InstructionStream instruction_stream){
        super();
        this.font = font;
        this.instruction_stream = instruction_stream;
    }


    public void destroy(){
        InstructionStream in = this.instruction_stream;
        if (null != in){
            this.instruction_stream = null;
            in.destroy();
        }
    }
    public void init(DisplayOptions gdo) {

        this.instruction_stream.execute();
        this.expand(gdo);
        this.slant(gdo);
        this.instruction_stream.setRemakeFlag(false);
    }
    public final InstructionStream getInstructionStream() {
        return instruction_stream;
    }
    public final CFFFont getFont() {
        return font;
    }
    public final CFFPathList getCFFPathList() {

        return instruction_stream.getCFFPathList();
    }
    public final CFFPointList getCFFPointList() {

        return instruction_stream.getCFFPointList();
    }
    public final boolean hasX(int x) {
        CFFPointList point_list = this.getCFFPointList();
        return point_list.hasX(x);
    }
    public final boolean hasY(int y) {
        CFFPointList point_list = this.getCFFPointList();
        return point_list.hasY(y);
    }
    public final int getMinX() {
        CFFPointList fepointlist = this.getCFFPointList();
        return fepointlist.getMinX();
    }
    public final int getMinY() {
        CFFPointList fepointlist = this.getCFFPointList();
        return fepointlist.getMinY();
    }
    public final int getMaxX() {
        CFFPointList fepointlist = this.getCFFPointList();
        return fepointlist.getMaxX();
    }
    public final int getMaxY() {
        CFFPointList fepointlist = this.getCFFPointList();
        return fepointlist.getMaxY();
    }
    /*
     * [TODO]: Call into one of these from 'init' according to options.
     */
    protected void rescaleWithFixedBottom(int fixed, int o, int n, DisplayOptions gdo) {
        this.getCFFPointList().rescaleWithFixedBottom(fixed, o, n);
    }
    protected void rescaleWithFixedTop(int fixed, int o, int n, DisplayOptions gdo) {
        this.getCFFPointList().rescaleWithFixedTop(fixed, o, n);
    }
    protected void rescaleWithFixedLeft(int fixed, int o, int n, DisplayOptions gdo) {
        this.getCFFPointList().rescaleWithFixedLeft(fixed, o, n);
    }
    protected void rescaleWithFixedRight(int fixed, int o, int n, DisplayOptions gdo) {
        this.getCFFPointList().rescaleWithFixedRight(fixed, o, n);
    }
    protected void slant(DisplayOptions gdo) {

        int slant_factor = gdo.getSlant();
        if (slant_factor != 0) {
            CFFPointList point_list = this.getCFFPointList();
            CFFPoint point;
      
            int len = point_list.getNumber();
            if (slant_factor > 0) {
                for (int index = len; --index >= 0;) {
                    point = point_list.getPoint(index);
                    point.setX(point.getX() + ((slant_factor * (0x10000 - point.getY())) >> 8));
                }
            } else if (slant_factor < 0) {
                for (int index = len; --index >= 0;) {
                    point = point_list.getPoint(index);
                    point.setX(point.getX() - ((slant_factor * point.getY()) >> 8));
                }
            }
        }
    }
    protected void expand(DisplayOptions gdo) {

        int expand_factor = gdo.getExpand();
        if (expand_factor > 0) {
            CFFPointList fepl_of_glyph = this.getCFFPointList();
            for (int index = fepl_of_glyph.getNumber(); --index >= 0;) {
                CFFPoint point = fepl_of_glyph.getPoint(index);
                point.setX((point.getX() * expand_factor) >> 10);
            }
        }
    }
}
