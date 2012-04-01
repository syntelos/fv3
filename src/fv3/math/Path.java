/*
 * fv3.math
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
package fv3.math;

/**
 * <h3>Work In Progress</h3>
 * 
 * An initial experiment in path composition for producing line sets.
 * 
 * 
 * @see VertexArray
 * @author jdp
 */
public class Path
    extends VertexArray
{

    /** 
     * Winding algorithms make more sense for a ray from centroid
     * point to subject point.
     * 
     * <h3>Non zero winding</h3>
     * 
     * <p> From P, a ray R intersects the outline having clock-wise
     * (CW) or counter-clock-wise (CCW) direction.  </p>
     * 
     * <p> An accumulator A is initialized to zero, and incremented
     * for a CCW intersection, and decremented for a CW
     * intersection.</p>
     * 
     * <p> For A equal to zero P is "outside" the outline, otherwise P
     * is "inside" the outline. </p>
     * 
     * <h3>Even odd winding</h3>
     * 
     * <p> From P, a ray R intersects the outline an even or odd
     * number of times.  If even, P is "outside" the outline.
     * Otherwise when P is odd, P is "inside" the outline.  </p>
     * 
     * <h3>Future</h3>
     * 
     * <p> The Winding enum constant {@link Path$Winding#Future
     * Future} represents and unknown, wait and see status. </p>
     */
    public static enum Winding {
        EvenOdd, NonZero, Future;


        public final static Winding For(int rule){
            switch(rule){
            case 0:
                return EvenOdd;
            case 1:
                return NonZero;
            default:
                return null;
            }
        }

        /**
         * Missing a required winding 
         */
        public static class Missing
            extends IllegalStateException
        {

            public Missing(){
                super("Require winding");
            }
        }
    }
    /**
     *
     */
    public static enum Op {
        MoveTo, LineTo, QuadTo, CubicTo, Close;


        public final static Op[] Add(Op[] list, Op item){
            if (null == item)
                return list;
            else if (null == list)
                return new Op[]{item};
            else {
                int len = list.length;
                Op[] copier = new Op[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = item;
                return copier;
            }
        }
    }


    protected Path.Winding winding;

    protected boolean closed;

    protected int index;

    protected Op[] operators;


    public Path(){
        super(Type.Lines,0);
    }


    public Path.Winding getWinding(){
        return this.winding;
    }
    public boolean isWindingNonZero(){
        return (Path.Winding.NonZero == this.winding);
    }
    public boolean isWindingEvenOdd(){
        return (Path.Winding.EvenOdd == this.winding);
    }
    public Path setWindingNonZero(){
        return this.setWinding(Path.Winding.NonZero);
    }
    public Path setWindingEvenOdd(){
        return this.setWinding(Path.Winding.EvenOdd);
    }
    /**
     * Preparation to define new data: clear path and define winding.
     */
    public Path setWinding(Winding winding){
        if (null != winding){
            this.countVertices(0);

            this.winding = winding;

            this.closed = false;

            this.index = 0;

            return this;
        }
        else
            throw new IllegalArgumentException();
    }
    protected Op op(int index){
        Op[] operators = this.operators;
        if (null == operators || 0 > index)
            return null;
        else if (index < operators.length)
            return operators[index];
        else
            return null;
    }
    protected Op lop(){
        Op[] operators = this.operators;
        if (null == operators)
            return null;
        else
            return operators[operators.length-1];
    }
    protected void nop(Op op){
        this.operators = Op.Add(this.operators,op);
    }
    protected int lindexOf(Op op){
        Op[] operators = this.operators;
        if (null != operators){
            for (int lindex = (operators.length-1); lindex >= 0; lindex--){

                if (op == operators[lindex])
                    return lindex;
            }
        }
        return -1;
    }
    protected boolean valid(int index){
        Op[] operators = this.operators;
        if (null == operators || 0 > index)
            return false;
        else if (index < operators.length)
            return true;
        else
            return false;
    }

    public final void moveTo(float x, float y) {
        if (this.lop() == Op.MoveTo)
            this.setVertex(this.index-1,x,y,0);
        else {
            this.nop(Op.MoveTo);
            this.setVertex(this.index++,x,y,0);
        }
    }
    public final void lineTo(float x, float y) {

        this.nop(Op.LineTo);
        this.setVertex(this.index++,x,y,0);
    }
    public void quadTo(float x1, float y1,
                       float x2, float y2)
    {
    }
    public void curveTo(float x1, float y1,
                        float x2, float y2,
                        float x3, float y3)
    {
    }
    public final void closePath(){
        int lindex = lindexOf(Op.Close);
        if (-1 < lindex){
            int mt = (lindex+1);
            while (Op.MoveTo != this.op(mt))
                mt += 1;

            if (this.valid(mt)){
                this.nop(Op.Close);
                this.setVertex(this.index++,this.getVertex(mt));
            }
            else {
                this.nop(Op.Close);
                this.setVertex(this.index++,this.getVertex(lindex));
            }
        }
        else {
            lindex = lindexOf(Op.MoveTo);
            if (-1 < lindex){

                this.nop(Op.Close);
                this.setVertex(this.index++,this.getVertex(lindex));
            }
        }
    }

    public final Path apply(String pexpr){
        return this.apply(new Path.Parser(pexpr));
    }
    public final Path apply(Path.Parser p){
        Path.Parser.Token last = null;

        float mx = 0, my = 0, sx = 0, sy = 0;

        for (Path.Parser.Token tok : p){
            switch(tok){
            case Coordinate:
            case M:
                this.moveTo((mx = p.getCoordinate()),(my = p.getCoordinate()));
                sx = mx;
                sy = my;
                break;
            case m:
                this.moveTo((mx += p.getCoordinate()),(my += p.getCoordinate()));
                sx = mx;
                sy = my;
                break;
            case Z:
            case z:
                this.closePath();
                break;
            case L:
                this.lineTo((sx = p.getCoordinate()),(sy = p.getCoordinate()));
                break;
            case l:
                this.lineTo((sx += p.getCoordinate()),(sy += p.getCoordinate()));
                break;
            case H:
                sx = p.getCoordinate();
                this.lineTo(sx,sy);
                break;
            case h:
                sx += p.getCoordinate();
                this.lineTo(sx,sy);
                break;
            case V:
                sy = p.getCoordinate();
                this.lineTo(sx,sy);
                break;
            case v:
                sy += p.getCoordinate();
                this.lineTo(sx,sy);
                break;
            case C:
                this.curveTo(p.getCoordinate(),p.getCoordinate(),
                             p.getCoordinate(),p.getCoordinate(),
                             (sx = p.getCoordinate()),(sy = p.getCoordinate()));
                break;
            case c:
                this.curveTo((sx + p.getCoordinate()),(sy + p.getCoordinate()),
                             (sx + p.getCoordinate()),(sy + p.getCoordinate()),
                             (sx += p.getCoordinate()),(sy += p.getCoordinate()));
                break;
            case S:
            case s:
                throw new UnsupportedOperationException(tok.name());
            case Q:
                this.quadTo(p.getCoordinate(),p.getCoordinate(),
                            (sx = p.getCoordinate()),(sy = p.getCoordinate()));
                break;
            case q:
                this.quadTo((sx + p.getCoordinate()),(sy + p.getCoordinate()),
                            (sx += p.getCoordinate()),(sy += p.getCoordinate()));
                break;
            case T:
            case t:
                throw new UnsupportedOperationException(tok.name());
            case A:
            case a:
                throw new UnsupportedOperationException(tok.name());
            default:
                throw new IllegalArgumentException(tok.name());
            }
            last = tok;
        }
        return this;
    }
    /**
     * Parse SVG Path "d" attribute value expression.
     */
    public final static class Parser
        extends Object
        implements Iterable<Parser.Token>,
                   java.util.Iterator<Parser.Token>
    {
        public enum Token {
            Unknown, Coordinate, M, m, Z, z, L, l, H, h, V, v, C, c, S, s, Q, q, T, t, A, a;
        }


        private final char[] string;
        private int index;
        private java.lang.Float coordinate;


        public Parser(String string){
            super();
            if (null != string){
                this.string = string.trim().toCharArray();
                if (0 == this.string.length)
                    throw new IllegalArgumentException();
            }
            else
                throw new IllegalArgumentException();
        }


        public java.lang.Float getCoordinate(){
            java.lang.Float c = this.coordinate;
            if (null != this.coordinate){
                this.coordinate = null;
                return c;
            }
            else if (this.hasNext() && Token.Coordinate == this.next()){
                c = this.coordinate;
                this.coordinate = null;
                return c;
            }
            else
                throw new java.util.NoSuchElementException();
        }
        public boolean hasNext(){
            return (this.index < this.string.length);
        }
        public Parser.Token next(){
            this.coordinate = null;
            if (this.index < this.string.length){
                Parser.Token token = null;
                int start = this.index;
                int end = start;
                boolean decpt = false;
                scan:
                while (this.index < this.string.length){

                    switch(this.string[this.index]){
                    case ' ':
                    case ',':
                        if (this.index != start){
                            end = (this.index-1);
                            this.index++;
                            break scan;
                        }
                        break;
                    case '.':
                        if (null != token){
                            if (decpt || Parser.Token.Coordinate != token){
                                end = (this.index-1);
                                break scan;
                            }
                        }
                        else
                            token = Parser.Token.Coordinate;

                        decpt = true;
                        break;
                    case '-':
                        if (null != token){
                            end = (this.index-1);
                            break scan;
                        }
                        else
                            token = Parser.Token.Coordinate;
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        if (null == token)
                            token = Parser.Token.Coordinate;
                        else if (token != Parser.Token.Coordinate){
                            end = (this.index-1);
                            break scan;
                        }
                        break;
                    default:
                        if (null == token)
                            return Parser.Token.valueOf(String.valueOf(this.string[this.index++]));
                        else {
                            end = (this.index-1);
                            break scan;
                        }
                    }
                    this.index++;
                }

                if (Parser.Token.Coordinate == token){

                    if (start == end && this.index == this.string.length)
                        end = (this.index-1);

                    this.coordinate = java.lang.Float.parseFloat(new String(this.string,start,(end-start+1)));
                    return token;
                }
                else
                    return Parser.Token.Unknown;
            }
            else
                throw new java.util.NoSuchElementException();
        }
        public void remove(){
            throw new UnsupportedOperationException();
        }
        public java.util.Iterator<Parser.Token> iterator(){
            return this;
        }
    }

    public static void main(String[] argv){
        try {
            java.io.File file = new java.io.File(argv[0]);
            java.io.FileInputStream fin = new java.io.FileInputStream(file);
            int stateP = 0, stateD = 0;
            int ch;
            String data = null;

            read:
            while (-1 < (ch = fin.read())){
                switch(ch){
                case 'p':
                    if (0 == stateP)
                        stateP = 1;
                    break;
                case 'a':
                    if (1 == stateP)
                        stateP = 2;
                    else
                        stateP = 0;
                    break;
                case 't':
                    if (2 == stateP)
                        stateP = 3;
                    else
                        stateP = 0;
                    break;
                case 'h':
                    if (3 == stateP)
                        stateP = 4;
                    else
                        stateP = 0;
                    break;
                case ' ':
                    if (4 == stateP && 0 == stateD)
                        stateD = 1;
                    else
                        stateD = 0;
                    break;
                case 'd':
                    if (4 == stateP && 1 == stateD)
                        stateD = 2;
                    else
                        stateD = 0;
                    break;
                case '=':
                    if (4 == stateP && 2 == stateD)
                        stateD = 3;
                    else
                        stateD = 0;
                    break;
                case '"':
                    if (4 == stateP && 3 == stateD){
                        StringBuilder string = new StringBuilder();
                        while (-1 < (ch = fin.read())){
                            if ('"' != ch)
                                string.append(ch);
                            else
                                break;
                        }
                        data = string.toString();

                        break read;
                    }
                    else
                        stateD = 0;
                    break;
                default:
                    break;
                }
            }
            fin.close();

            if (null != data){
                Path path = new Path();
                path.apply(data);
                System.out.println(path.toString("    ","\n"));
                System.exit(0);
            }
            else {
                System.err.printf("Error, data not found in file '%s'%n",file);
                System.exit(1);
            }
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
