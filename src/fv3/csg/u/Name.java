/*
 * fv3
 * Copyright (C) 2012, John Pritchard, all rights reserved.
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fv3.csg.u;

public class Name
    extends java.lang.Object
{
    public enum Kind {
        Solid, Face, Vertex;


        public String toString(){
            return String.format("%6s",this.name());
        }
    }
    public interface Named {

        Name getName();
    }

    public final Kind kind;

    public final String in;

    public final int id;

    public final String desc, string;


    public Name(Kind kind, String desc){
        super();
        if (Kind.Solid == kind && null != desc){
            this.kind = kind;
            this.in = null;
            this.id = 0;
            this.desc = desc;
            this.string = String.format("%40s",String.format("%s %s",this.kind,this.desc));
        }
        else
            throw new IllegalArgumentException();
    }
    public Name(Kind kind, Object in, int index, String desc){
        super();
        if (null != kind && null != in && -1 < index && null != desc){
            this.kind = kind;
            this.in = In(in);
            this.id = Math.abs(index);
            this.desc = desc;
            this.string = String.format("%40s",String.format("%s (%6d) in (%s) %s",this.kind,this.id,this.in,this.desc));
        }
        else
            throw new IllegalArgumentException();
    }
    /**
     * Kind component clone or copy operation 
     */
    public Name(Name n, String desc2){
        super();
        if (null != n && null != desc2){
            this.kind = n.kind;
            this.in = n.in;
            this.id = -(n.id);
            this.desc = n.desc+'/'+desc2;
            this.string = String.format("%40s",String.format("%s (%6d) in (%s) %s",this.kind,this.id,this.in,this.desc));
        }
        else
            throw new IllegalArgumentException();
    }


    public Name copy(String desc2){
        return new Name(this,desc2);
    }
    public final int hashCode(){
        return this.string.hashCode();
    }
    public final String toString(){
        return this.string;
    }

    protected final static String In(Object in){

        if (in instanceof String)
            return (String)in;
        else if (in instanceof Named)
            return ((Named)in).getName().toString();
        else
            return in.getClass().getName();
    }
}
