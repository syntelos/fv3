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

import fv3.font.CFFPath;
import fv3.font.CFFPathList;
import fv3.font.cff.Curve;
import fv3.font.cff.DisplayOptions;
import fv3.font.cff.InstructionStream;

import java.awt.geom.Path2D;

/**
 * 
 * @author jdp
 */
public class CFFGlyph
    extends fv3.font.CFFGlyph
{

    protected Path2D.Double path;


    protected CFFGlyph(CFFFont font, InstructionStream in){
        super(font,in);
    }


    public final boolean hasPath(){
        return (null != this.path);
    }
    public final Path2D.Double getPath(){
        return this.path;
    }
    public void init(DisplayOptions gdo) {
        super.init(gdo);
        Path2D.Double path = new Path2D.Double();
        {
            CFFPathList cffList = this.getCFFPathList();
            CFFPath cffPath;
            Curve curve;
            double[] points = null, last;
            for (int pc = 0, pz = cffList.getNumber(), fc, fz, cc, cz; pc < pz; pc++){
                cffPath = cffList.getPath(pc);
                for (fc = 0, fz = cffPath.getNumberOfCurves(); fc < fz; fc++){
                    curve = cffPath.getCurve(fc);
                    last = points;
                    points = curve.points();
                    for (cc = ((null == last)?(0):(1)), cz = points.length; cc < cz; cc++){
                        if (0 == cc)
                            path.moveTo(points[cc++],points[cc]);
                        else
                            path.lineTo(points[cc++],points[cc]);
                    }
                }
            }
        }
        this.path = path;
    }
}
