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
package fv3.font.ttf;

import fv3.font.TTFFont;
import fv3.font.TTFFontReader;
import fv3.font.TTFGlyph;
import fv3.font.TTFPath;

/**
 * 
 * @author John Pritchard
 */
public abstract class Table
    extends Object
    implements Cloneable
{
    public final static int COUNT = 68;

    /**
     * 
     */
    public final static class Unknown
        extends Table
    {
        public final int tag, type;

        public final String name;


        public Unknown(int tag, int ofs, int len, int type) {
            super(ofs,len);
            this.tag = tag;
            this.type = type;
            char a = (char)((tag>>24)&0xff);
            char b = (char)((tag>>16)&0xff);
            char c = (char)((tag>>8)&0xff);
            char d = (char)(tag&0xff);

            this.name = new String(new char[]{a,b,c,d});
        }


        public String getName(){
            return this.name;
        }
        public int getTag(){
            return this.tag;
        }
        public int getType(){
            return this.type;
        }
        public void init(TTFFont font, TTF tables, TTFFontReader reader){
        }
    }


    public final int offset, length;


    protected Table(int ofs, int len) {
        super();
        this.offset = ofs;
        this.length = len;
    }


    public abstract String getName();

    public abstract int getTag();
    /**
     * @return Internal dense index for {@link TTF}
     */
    public abstract int getType();

    public abstract void init(TTFFont font, TTF tables, TTFFontReader reader);

    public final void seekto(TTFFontReader reader){
        reader.seek(this.offset);
    }



    public final static Table Create(int tag, int ofs, int len){
        switch(tag){
        case Acnt.ID:
            return new Acnt(ofs,len);
        case Avar.ID:
            return new Avar(ofs,len);
        case Base.ID:
            return new Base(ofs,len);
        case Bdat.ID:
            return new Bdat(ofs,len);
        case Bdf.ID:
            return new Bdf(ofs,len);
        case Bhed.ID:
            return new Bhed(ofs,len);
        case Bloc.ID:
            return new Bloc(ofs,len);
        case Bsln.ID:
            return new Bsln(ofs,len);
        case Cff.ID:
            return new Cff(ofs,len);
        case Cid.ID:
            return new Cid(ofs,len);
        case Cmap.ID:
            return new Cmap(ofs,len);
        case Cvar.ID:
            return new Cvar(ofs,len);
        case Cvt.ID:
            return new Cvt(ofs,len);
        case Dsig.ID:
            return new Dsig(ofs,len);
        case Ebdt.ID:
            return new Ebdt(ofs,len);
        case Eblc.ID:
            return new Eblc(ofs,len);
        case Ebsc.ID:
            return new Ebsc(ofs,len);
        case Elua.ID:
            return new Elua(ofs,len);
        case Fdsc.ID:
            return new Fdsc(ofs,len);
        case Feat.ID:
            return new Feat(ofs,len);
        case Fftm.ID:
            return new Fftm(ofs,len);
        case Fmtx.ID:
            return new Fmtx(ofs,len);
        case Fpgm.ID:
            return new Fpgm(ofs,len);
        case Fvar.ID:
            return new Fvar(ofs,len);
        case Gasp.ID:
            return new Gasp(ofs,len);
        case Gdef.ID:
            return new Gdef(ofs,len);
        case Glat.ID:
            return new Glat(ofs,len);
        case Gloc.ID:
            return new Gloc(ofs,len);
        case Glyf.ID:
            return new Glyf(ofs,len);
        case Gpos.ID:
            return new Gpos(ofs,len);
        case Gvar.ID:
            return new Gvar(ofs,len);
        case Gsub.ID:
            return new Gsub(ofs,len);
        case Hdmx.ID:
            return new Hdmx(ofs,len);
        case Head.ID:
            return new Head(ofs,len);
        case Hhea.ID:
            return new Hhea(ofs,len);
        case Hmtx.ID:
            return new Hmtx(ofs,len);
        case Hsty.ID:
            return new Hsty(ofs,len);
        case Just.ID:
            return new Just(ofs,len);
        case Jstf.ID:
            return new Jstf(ofs,len);
        case Kern.ID:
            return new Kern(ofs,len);
        case Lcar.ID:
            return new Lcar(ofs,len);
        case Loca.ID:
            return new Loca(ofs,len);
        case Ltsh.ID:
            return new Ltsh(ofs,len);
        case Math.ID:
            return new Math(ofs,len);
        case Maxp.ID:
            return new Maxp(ofs,len);
        case Mmsd.ID:
            return new Mmsd(ofs,len);
        case Mmfx.ID:
            return new Mmfx(ofs,len);
        case Mort.ID:
            return new Mort(ofs,len);
        case Morx.ID:
            return new Morx(ofs,len);
        case Name.ID:
            return new Name(ofs,len);
        case Opbd.ID:
            return new Opbd(ofs,len);
        case Os2.ID:
            return new Os2(ofs,len);
        case Pclt.ID:
            return new Pclt(ofs,len);
        case Pfed.ID:
            return new Pfed(ofs,len);
        case Post.ID:
            return new Post(ofs,len);
        case Prep.ID:
            return new Prep(ofs,len);
        case Prop.ID:
            return new Prop(ofs,len);
        case Silf.ID:
            return new Silf(ofs,len);
        case Sill.ID:
            return new Sill(ofs,len);
        case Silt.ID:
            return new Silt(ofs,len);
        case Tex.ID:
            return new Tex(ofs,len);
        case Trak.ID:
            return new Trak(ofs,len);
        case Typ1.ID:
            return new Typ1(ofs,len);
        case Vdmx.ID:
            return new Vdmx(ofs,len);
        case Vhea.ID:
            return new Vhea(ofs,len);
        case Vmtx.ID:
            return new Vmtx(ofs,len);
        case Vorg.ID:
            return new Vorg(ofs,len);
        case Zapf.ID:
            return new Zapf(ofs,len);
        default:
            return null;
        }

    }
}
