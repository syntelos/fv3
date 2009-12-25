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
package fv3.font.awt;

import fv3.font.CFFFontReader;
import fv3.font.cff.DisplayOptions;
import fv3.font.cff.InstructionStream;

import java.awt.geom.Path2D;

/**
 * 
 * @author jdp
 */
public class CFFFont
    extends fv3.font.CFFFont
{

    public CFFFont(String name, CFFFontReader reader){
        super(name,reader);
    }
    public CFFFont(String name, CFFFontReader reader, DisplayOptions opts){
        super(name,reader,opts);
    }


    protected fv3.font.CFFGlyph create(InstructionStream in){
        return new CFFGlyph(this,in);
    }
    public Path2D.Double getPath(int ch){
        CFFGlyph glyph = (CFFGlyph)this.get(ch);
        return glyph.getPath();
    }
}
