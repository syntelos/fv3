/*
 * fv3
 * Copyright (C) 2010  John Pritchard, jdp@syntelos.org
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
/*
 * Copyright (c) James P. Buzbee 1996
 * House Blend Software
 * 
 *  Permission to use, copy, modify, and distribute this software
 *  for any use is hereby granted provided
 *  this notice is kept intact within the source file
 *  This is freeware, use it as desired !
 * 
 *  Very loosly based on code with authors listed as :
 *  Alan Richardson, Pete Holzmann, James Hurt
 */
package fv3.font;

import fv3.math.VertexArray;

/**
 * Hershey vector fonts are classic "plotter" line sets.  
 * 
 * This code and the supporting font files is based on the Hershey
 * font distribution from James Buzbee, and supported with information
 * from Paul Bourke.  The font files from Jim Buzbee (*.jhf) have been
 * sorted into the ASCII character set.
 * 
 * @see http://www.batbox.org/font.html
 * @see http://local.wasp.uwa.edu.au/~pbourke/dataformats/hershey/
 * @author John Pritchard
 */
public class HersheyFont
    extends Font<HersheyGlyph>
{
    public static class Astrology
        extends HersheyFont
    {
        public Astrology()
            throws java.io.IOException
        {
            super("astrology");
        }
        public Astrology(FontOptions opts)
            throws java.io.IOException
        {
            super("astrology",opts);
        }
    }
    public static class Cursive
        extends HersheyFont
    {
        public Cursive()
            throws java.io.IOException
        {
            super("cursive");
        }
        public Cursive(FontOptions opts)
            throws java.io.IOException
        {
            super("cursive",opts);
        }
    }
    public static class Cyrilc_1
        extends HersheyFont
    {
        public Cyrilc_1()
            throws java.io.IOException
        {
            super("cyrilc_1");
        }
        public Cyrilc_1(FontOptions opts)
            throws java.io.IOException
        {
            super("cyrilc_1",opts);
        }
    }
    public static class Cyrillic
        extends HersheyFont
    {
        public Cyrillic()
            throws java.io.IOException
        {
            super("cyrillic");
        }
        public Cyrillic(FontOptions opts)
            throws java.io.IOException
        {
            super("cyrillic",opts);
        }
    }
    public static class Futural
        extends HersheyFont
    {
        public Futural()
            throws java.io.IOException
        {
            super("futural");
        }
        public Futural(FontOptions opts)
            throws java.io.IOException
        {
            super("futural",opts);
        }
    }
    public static class Futuram
        extends HersheyFont
    {
        public Futuram()
            throws java.io.IOException
        {
            super("futuram");
        }
        public Futuram(FontOptions opts)
            throws java.io.IOException
        {
            super("futuram",opts);
        }
    }
    public static class Gothgbt
        extends HersheyFont
    {
        public Gothgbt()
            throws java.io.IOException
        {
            super("gothgbt");
        }
        public Gothgbt(FontOptions opts)
            throws java.io.IOException
        {
            super("gothgbt",opts);
        }
    }
    public static class Gothgrt
        extends HersheyFont
    {
        public Gothgrt()
            throws java.io.IOException
        {
            super("gothgrt");
        }
        public Gothgrt(FontOptions opts)
            throws java.io.IOException
        {
            super("gothgrt",opts);
        }
    }
    public static class Gothiceng
        extends HersheyFont
    {
        public Gothiceng()
            throws java.io.IOException
        {
            super("gothiceng");
        }
        public Gothiceng(FontOptions opts)
            throws java.io.IOException
        {
            super("gothiceng",opts);
        }
    }
    public static class Gothicger
        extends HersheyFont
    {
        public Gothicger()
            throws java.io.IOException
        {
            super("gothicger");
        }
        public Gothicger(FontOptions opts)
            throws java.io.IOException
        {
            super("gothicger",opts);
        }
    }
    public static class Gothicita
        extends HersheyFont
    {
        public Gothicita()
            throws java.io.IOException
        {
            super("gothicita");
        }
        public Gothicita(FontOptions opts)
            throws java.io.IOException
        {
            super("gothicita",opts);
        }
    }
    public static class Gothitt
        extends HersheyFont
    {
        public Gothitt()
            throws java.io.IOException
        {
            super("gothitt");
        }
        public Gothitt(FontOptions opts)
            throws java.io.IOException
        {
            super("gothitt",opts);
        }
    }
    public static class Greek
        extends HersheyFont
    {
        public Greek()
            throws java.io.IOException
        {
            super("greek");
        }
        public Greek(FontOptions opts)
            throws java.io.IOException
        {
            super("greek",opts);
        }
    }
    public static class Greekc
        extends HersheyFont
    {
        public Greekc()
            throws java.io.IOException
        {
            super("greekc");
        }
        public Greekc(FontOptions opts)
            throws java.io.IOException
        {
            super("greekc",opts);
        }
    }
    public static class Greeks
        extends HersheyFont
    {
        public Greeks()
            throws java.io.IOException
        {
            super("greeks");
        }
        public Greeks(FontOptions opts)
            throws java.io.IOException
        {
            super("greeks",opts);
        }
    }
    public static class Japanese
        extends HersheyFont
    {
        public Japanese()
            throws java.io.IOException
        {
            super("japanese");
        }
        public Japanese(FontOptions opts)
            throws java.io.IOException
        {
            super("japanese",opts);
        }
    }
    public static class Markers
        extends HersheyFont
    {
        public Markers()
            throws java.io.IOException
        {
            super("markers");
        }
        public Markers(FontOptions opts)
            throws java.io.IOException
        {
            super("markers",opts);
        }
    }
    public static class Mathlow
        extends HersheyFont
    {
        public Mathlow()
            throws java.io.IOException
        {
            super("mathlow");
        }
        public Mathlow(FontOptions opts)
            throws java.io.IOException
        {
            super("mathlow",opts);
        }
    }
    public static class Mathupp
        extends HersheyFont
    {
        public Mathupp()
            throws java.io.IOException
        {
            super("mathupp");
        }
        public Mathupp(FontOptions opts)
            throws java.io.IOException
        {
            super("mathupp",opts);
        }
    }
    public static class Meteorology
        extends HersheyFont
    {
        public Meteorology()
            throws java.io.IOException
        {
            super("meteorology");
        }
        public Meteorology(FontOptions opts)
            throws java.io.IOException
        {
            super("meteorology",opts);
        }
    }
    public static class Music
        extends HersheyFont
    {
        public Music()
            throws java.io.IOException
        {
            super("music");
        }
        public Music(FontOptions opts)
            throws java.io.IOException
        {
            super("music",opts);
        }
    }
    public static class Rowmand
        extends HersheyFont
    {
        public Rowmand()
            throws java.io.IOException
        {
            super("rowmand");
        }
        public Rowmand(FontOptions opts)
            throws java.io.IOException
        {
            super("rowmand",opts);
        }
    }
    public static class Rowmans
        extends HersheyFont
    {
        public Rowmans()
            throws java.io.IOException
        {
            super("rowmans");
        }
        public Rowmans(FontOptions opts)
            throws java.io.IOException
        {
            super("rowmans",opts);
        }
    }
    public static class Rowmant
        extends HersheyFont
    {
        public Rowmant()
            throws java.io.IOException
        {
            super("rowmant");
        }
        public Rowmant(FontOptions opts)
            throws java.io.IOException
        {
            super("rowmant",opts);
        }
    }
    public static class Scriptc
        extends HersheyFont
    {
        public Scriptc()
            throws java.io.IOException
        {
            super("scriptc");
        }
        public Scriptc(FontOptions opts)
            throws java.io.IOException
        {
            super("scriptc",opts);
        }
    }
    public static class Scripts
        extends HersheyFont
    {
        public Scripts()
            throws java.io.IOException
        {
            super("scripts");
        }
        public Scripts(FontOptions opts)
            throws java.io.IOException
        {
            super("scripts",opts);
        }
    }
    public static class Symbolic
        extends HersheyFont
    {
        public Symbolic()
            throws java.io.IOException
        {
            super("symbolic");
        }
        public Symbolic(FontOptions opts)
            throws java.io.IOException
        {
            super("symbolic",opts);
        }
    }
    public static class Timesg
        extends HersheyFont
    {
        public Timesg()
            throws java.io.IOException
        {
            super("timesg");
        }
        public Timesg(FontOptions opts)
            throws java.io.IOException
        {
            super("timesg",opts);
        }
    }
    public static class Timesi
        extends HersheyFont
    {
        public Timesi()
            throws java.io.IOException
        {
            super("timesi");
        }
        public Timesi(FontOptions opts)
            throws java.io.IOException
        {
            super("timesi",opts);
        }
    }
    public static class Timesib
        extends HersheyFont
    {
        public Timesib()
            throws java.io.IOException
        {
            super("timesib");
        }
        public Timesib(FontOptions opts)
            throws java.io.IOException
        {
            super("timesib",opts);
        }
    }
    public static class Timesr
        extends HersheyFont
    {
        public Timesr()
            throws java.io.IOException
        {
            super("timesr");
        }
        public Timesr(FontOptions opts)
            throws java.io.IOException
        {
            super("timesr",opts);
        }
    }
    public static class Timesrb
        extends HersheyFont
    {
        public Timesrb()
            throws java.io.IOException
        {
            super("timesrb");
        }
        public Timesrb(FontOptions opts)
            throws java.io.IOException
        {
            super("timesrb",opts);
        }
    }


    protected final float pathSetMinX, pathSetMaxX;
    protected final float pathSetMinY, pathSetMaxY;

    protected final float characterSetMinX, characterSetMaxX;
    protected final float characterSetMinY, characterSetMaxY;

    protected final float em, ascent, descent, leading;


    public HersheyFont(Font.Key key)
        throws java.io.IOException
    {
        this(key.name,key.opts);
    }
    public HersheyFont(String name)
        throws java.io.IOException
    {
        this(name,(new HersheyFontReader(name)));
    }
    public HersheyFont(String name, FontOptions opts)
        throws java.io.IOException
    {
        this(name,(new HersheyFontReader(name)),opts);
    }
    public HersheyFont(String name, HersheyFontReader reader) {
        this(name,reader,(new FontOptions()));
    }
    public HersheyFont(String name, HersheyFontReader reader, FontOptions opts) {
        super(name,reader,opts);

        float pathSetMinX = 0;
        float pathSetMaxX = 0;

        float pathSetMinY = 0;
        float pathSetMaxY = 0;

        for (int cc = 0; ; cc++){

            if (reader.asciiInt(5) < 1)
                break;
            else {
                HersheyPath path = new HersheyPath(this,reader,reader.asciiInt(3));

                HersheyGlyph glyph = this.createGlyph(path);

                final float minX = glyph.getPathMinX();
                final float maxX = glyph.getPathMaxX();
                final float minY = glyph.getPathMinY();
                final float maxY = glyph.getPathMaxY();

                if (0 < cc){
                    if (1 == cc){
                        pathSetMinX = minX;
                        pathSetMaxX = maxX;
                        pathSetMinY = minY;
                        pathSetMaxY = maxY;
                    }
                    else {

                        if (minX < pathSetMinX)
                            pathSetMinX = minX;

                        if (maxX > pathSetMaxX)
                            pathSetMaxX = maxX;

                        if (minY < pathSetMinY)
                            pathSetMinY = minY;

                        if (maxY > pathSetMaxY)
                            pathSetMaxY = maxY;
                    }
                }
            }
        }

        this.pathSetMinX = pathSetMinX;
        this.pathSetMaxX = pathSetMaxX;

        this.pathSetMinY = pathSetMinY;
        this.pathSetMaxY = pathSetMaxY;

        final int count = this.getLength();

        float characterSetMinX = 0;
        float characterSetMaxX = 0;

        float characterSetMinY = 0;
        float characterSetMaxY = 0;

        for (int cc = 0; cc < count; cc++){

            final HersheyGlyph glyph = this.get(cc);

            glyph.init(opts);

            final float minX = glyph.getBoundsMinX();
            final float maxX = glyph.getBoundsMaxX();
            final float minY = glyph.getBoundsMinY();
            final float maxY = glyph.getBoundsMaxY();

            if (0 == cc){
                characterSetMinX = minX;
                characterSetMaxX = maxX;
                characterSetMinY = minY;
                characterSetMaxY = maxY;
            }
            else {

                if (minX < characterSetMinX)
                    characterSetMinX = minX;

                if (maxX > characterSetMaxX)
                    characterSetMaxX = maxX;

                if (minY < characterSetMinY)
                    characterSetMinY = minY;

                if (maxY > characterSetMaxY)
                    characterSetMaxY = maxY;
            }
        }

        float em;
        try {
            HersheyGlyph cM = this.get('M');
            HersheyGlyph cH = this.get('H');
            em = Math.max( ((cM.getBoundsMaxX()-cM.getBoundsMinX())*1.1f),
                           ((cH.getBoundsMaxX()-cH.getBoundsMinX())*1.1f));
            HersheyGlyph sp = this.get(0);
            sp.setSpaceHorizontal(this.em);
        }
        catch (Exception notfound){
            HersheyGlyph la = this.get(count-1);
            em = (la.getBoundsMaxX()-la.getBoundsMinX());
            HersheyGlyph sp = this.get(0);
            sp.setSpaceHorizontal(this.em);
        }
        this.em = em;

        this.characterSetMinX = characterSetMinX;
        this.characterSetMaxX = characterSetMaxX;

        this.characterSetMinY = characterSetMinY;
        this.characterSetMaxY = characterSetMaxY;

        final float height = (characterSetMaxY-characterSetMinY);

        this.ascent = (0.7f * height);
        this.descent = (0.3f * height);
        this.leading = (0.1f * height);
    }



    public float getEm(){
        return this.em;
    }
    public float getAscent(){
        return this.ascent;
    }
    public float getDescent(){
        return this.descent;
    }
    public float getLeading(){
        return this.leading;
    }
    public VertexArray.Type getGlyphVectorType(){
        return VertexArray.Type.Lines;
    }
    public HersheyGlyph get(char ch){
        return super.get(ch - ' ');
    }
    public HersheyGlyph clone(char ch){
        return super.get(ch - ' ').clone();
    }
    public float spacing(char previous, char next){
        return this.em;
    }
    protected HersheyGlyph createGlyph(HersheyPath path){

        HersheyGlyph glyph = new HersheyGlyph(this,path);

        this.add(glyph);

        return glyph;
    }
}
