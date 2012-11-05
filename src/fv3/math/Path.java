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

import path.Op;
import path.Operand;
import path.Winding;

/**
 * {@link Path} is a list of path operations and their operands.  A
 * Path is an intermediate value, not for GL.
 * 
 * <h3>Vertex Array</h3>
 * 
 * <p> The initial type of a {@link Path} is {@link
 * VertexArray$Type#Path VertexArray.Type Path}.  This vertex array is
 * not applicable to GL, but is an intermediate value for conversions
 * to and from Path data lists. </p>
 *
 * <h3>GL data type conversions</h3>
 * 
 * <p> Data format conversions are defined here and in {@link
 * VertexArray}.  </p>
 * 
 * <blockquote><b>Work in progress</b></blockquote>
 * 
 * 
 * @see VertexArray
 * @author jdp
 */
public class Path
    extends VertexArray
    implements path.Path
{
    public final static float[] EmptySet = {};



    protected Winding winding;

    protected boolean closed;

    protected int index;

    protected Op[] operators;


    public Path(){
        super(Type.Path,0);
    }
    public Path(Type type){
        super(type,0);
    }
    public Path(Type type, int cap){
        super(type,cap);
    }
    public Path(Winding winding){
        this();
        this.winding = winding;
    }


    public Winding getWinding(){
        return this.winding;
    }
    public boolean isWindingNonZero(){
        return (Winding.NonZero == this.winding);
    }
    public boolean isWindingEvenOdd(){
        return (Winding.EvenOdd == this.winding);
    }
    public Path setWindingNonZero(){
        return this.setWinding(Winding.NonZero);
    }
    public Path setWindingEvenOdd(){
        return this.setWinding(Winding.EvenOdd);
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
    public float[] getVerticesPath(int index, Op op, float[] vertices){

        final int vx = IndexForVertices(this.operators,index);
        if (-1 < vx)
            return OperandsFromVertices(op,vertices,vx);
        else
            return null;
    }
    public Op op(int index){
        Op[] operators = this.operators;
        if (null == operators || 0 > index)
            return null;
        else if (index < operators.length)
            return operators[index];
        else
            return null;
    }
    public Op lop(){
        Op[] operators = this.operators;
        if (null == operators)
            return null;
        else
            return operators[operators.length-1];
    }
    /**
     * @param op Operator
     * @param operands Two dimensional point list (2D, XY)
     */
    public void add(Op op, float[] operands){

        this.operators = Op.Add(this.operators,op);

        this.addVerticesXY(operands);
    }
    public void reset(){

        this.clear();
    }
    public void set(path.Path path){
        this.reset();
        this.add(path);
    }
    public void add(path.Path path){
        for (Operand operand: path.toPathIterable()){
            switch(operand.op){
            case MoveTo:
                this.moveTo(operand.vertices);
                break;
            case LineTo:
                this.lineTo(operand.vertices);
                break;
            case QuadTo:
                this.quadTo(operand.vertices);
                break;
            case CubicTo:
                this.cubicTo(operand.vertices);
                break;
            case Close:
                this.close();
                break;
            }
        }
    }
    public int lindexOf(Op op){
        Op[] operators = this.operators;
        if (null != operators){
            for (int lindex = (operators.length-1); lindex >= 0; lindex--){

                if (op == operators[lindex])
                    return lindex;
            }
        }
        return -1;
    }
    public boolean valid(int index){
        Op[] operators = this.operators;
        if (null == operators || 0 > index)
            return false;
        else if (index < operators.length)
            return true;
        else
            return false;
    }

    public final void moveTo(float[] operands) {

        this.moveTo(operands[0],operands[1]);
    }
    public final void moveTo(float x, float y) {
        if (this.lop() == Op.MoveTo)
            this.setVertex(this.index-1,x,y,0);
        else {
            this.add(Op.MoveTo,new float[]{x,y});
        }
    }
    public final void lineTo(float[] operands) {

        this.lineTo(operands[0],operands[1]);
    }
    public final void lineTo(float x, float y) {

        this.add(Op.LineTo,new float[]{x,y});
    }
    public void quadTo(float[] operands)
    {
        switch(operands.length){
        case 4:
            this.quadTo(operands[0],operands[1],operands[2],operands[3]);
            break;
        case 6:
            this.quadTo(operands[0],operands[1],operands[3],operands[4]);
            break;
        default:
            throw new IllegalArgumentException(String.valueOf(operands.length));
        }
    }
    public void quadTo(float x1, float y1,
                       float x2, float y2)
    {

        this.add(Op.QuadTo,new float[]{x1,y1,0,x2,y2,0});
    }
    public void cubicTo(float[] operands)
    {
        switch(operands.length){
        case 6:
            this.cubicTo(operands[0],operands[1],operands[2],operands[3],operands[4],operands[5]);
            break;
        case 9:
            this.cubicTo(operands[0],operands[1],operands[3],operands[4],operands[6],operands[7]);
            break;
        default:
            throw new IllegalArgumentException(String.valueOf(operands.length));
        }
    }
    public void cubicTo(float x1, float y1,
                        float x2, float y2,
                        float x3, float y3)
    {
        this.add(Op.CubicTo,new float[]{x1,y1,0,x2,y2,0,x3,y3,0});
    }
    public final void close(){
        int lindex = lindexOf(Op.Close);
        if (-1 < lindex){
            int mt = (lindex+1);
            while (Op.MoveTo != this.op(mt))
                mt += 1;

            if (this.valid(mt)){

                this.add(Op.Close,this.getVertex(mt));
            }
            else {
                this.add(Op.Close,this.getVertex(lindex));
            }
        }
        else {
            lindex = lindexOf(Op.MoveTo);
            if (-1 < lindex){

                this.add(Op.Close,this.getVertex(lindex));
            }
        }
    }

    public final Path apply(String pexpr){
        return this.apply(new path.Parser(pexpr));
    }
    public final Path apply(path.Parser p){
        return path.Parser.Apply(this,p);
    }
    public java.lang.Iterable<Operand> toPathIterable(){
        return new path.Iterator(this,this.operators,this.vertices);
    }
    public java.util.Iterator<Operand> toPathIterator(){
        return new path.Iterator(this,this.operators,this.vertices);
    }


    /**
     * Three dimensional vertices
     */
    public final static int IndexForVertices(Op[] operators, int search){
        if (null == operators || 0 > search)
            return -1;
        else {
            int vx = 0;
            for (int cc = 0; cc <= search; cc++){

                vx += (operators[cc].operands*3);
            }
            throw new IllegalStateException();
        }
    }
    /**
     * Three dimensional vertices
     */
    public final static float[] OperandsFromVertices(Op op, float[] vertices, int vofs){
        if (null == vertices || 3 > vertices.length || 0 > vofs)
            return EmptySet;
        else {
            final int olen = (op.operands*3);
            final float[] oary = new float[olen];
            System.arraycopy(vertices,vofs,oary,0,olen);
            return oary;
        }
    }
    /**
     * Test read XML file SVG "Path D"
     */
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
