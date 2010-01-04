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
package fv3.font.test;

import fv3.font.awt.TTFFont;
import fv3.font.awt.TTFGlyph;
import fv3.font.FontOptions;
import fv3.font.TTFFontReader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class Main 
    extends java.awt.Frame 
    implements java.awt.event.KeyListener,
               java.awt.event.MouseListener,
               java.awt.event.MouseMotionListener,
               java.awt.event.MouseWheelListener, 
               java.awt.event.WindowListener
{

    public static void main(String[] argv){

        String name = "NEUROPOL";
        Main main = new Main(name);
        Screen screen = new Screen(main);
        FontOptions options = new FontOptions((screen.display.width),(screen.display.height));
        try {
            TTFFontReader reader = new TTFFontReader(name);
            try {
                TTFFont font = new TTFFont(name,reader,options);
                main.init(screen,font);
            }
            finally {
                reader.close();
            }
        }
        catch (java.io.IOException exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }


    private TTFFont font ;

    private Rectangle display;

    private AffineTransform flip;

    private int index;

    private String title;

    private TTFGlyph glyph;

    private Font large, small, micro;

    private Color gridFg, gridBg;


    public Main(String name){
        super(name);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addWindowListener(this);
        this.micro = new Font(Font.MONOSPACED,Font.PLAIN,8);
        this.small = new Font(Font.MONOSPACED,Font.PLAIN,12);
        this.large = new Font(Font.MONOSPACED,Font.BOLD,28);
        this.gridBg = new Color(0.7f,0.0f,0.0f,0.7f);
        this.gridFg = new Color(1.0f,0.0f,0.0f,1.0f);
    }


    public void init(Screen screen, TTFFont font){
        this.font = font;
        this.set('A');
        this.setBounds(screen.window);
        Rectangle display = screen.display;
        this.display = display;
        Point2D.Double c = new Point2D.Double(((display.width-display.x)/2.0),((display.height-display.y)/2.0));
        this.flip = AffineTransform.getQuadrantRotateInstance(2,c.x,c.y);
        this.flip.translate(display.x,display.y);
        this.show();
    }
    protected void dec(){
        this.set(this.index-1);
    }
    protected void inc(){
        this.set(this.index+1);
    }
    protected void set(int idx){
        if (-1 < idx){
            this.index = idx;
            this.glyph = (TTFGlyph)this.font.get(idx);
            this.set();
        }
    }
    protected void set(char ch){
        this.index = this.font.indexOf(ch);
        this.glyph = (TTFGlyph)this.font.get(this.index);
        this.set();
    }
    protected void set(){

        TTFGlyph glyph = this.glyph;
        if (null != glyph){
            char ch = glyph.character;
            this.title = String.format("Glyph '%c' 0x%x @ Index %d",ch,(int)ch,this.index);
        }
        else
            this.title = String.format("Glyph not found @ Index %d",this.index);
    }
    public void update(Graphics g){
        this.update( (Graphics2D)g);
    }
    public void paint(Graphics g){
        this.update( (Graphics2D)g);
    }
    public void update(Graphics2D g){
        g.setColor(Color.white);
        Rectangle bounds = this.getBounds();
        g.fillRect(0,0,bounds.width,bounds.height);
        /*
         */

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);


        g.setColor(this.gridFg);

        g.setFont(this.large);

        g.drawString(title,20,60);

        TTFGlyph glyph = this.glyph;

        if (null != glyph){

            g.transform(this.flip);

            glyph.drawGrid(g,this.small,this.micro,this.gridBg,this.gridFg);

            g.setColor(Color.black);

            glyph.drawOutline(g);
        }
    }
    public void windowOpened(WindowEvent e){
        this.requestFocus();
    }
    public void windowClosing(WindowEvent e) {
        this.hide();
        this.dispose();
    }
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    public void keyTyped(KeyEvent e){
        if (!e.isActionKey()){
            char ch = e.getKeyChar();
            this.set(ch);
            this.repaint();
        }
    }
    public void keyPressed(KeyEvent e){
    }
    public void keyReleased(KeyEvent e){
        if (e.isActionKey()){
            switch (e.getKeyCode()){
            case KeyEvent.VK_HOME:
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                this.dec();
                this.repaint();
                break;
            case KeyEvent.VK_END:
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                this.inc();
                this.repaint();
                break;
            default:
                this.hide();
                this.dispose();
                break;
            }
        }
    }
    public void mouseClicked(MouseEvent e){
        this.requestFocus();
    }
    public void mousePressed(MouseEvent e){
    }
    public void mouseReleased(MouseEvent e){
    }
    public void mouseEntered(MouseEvent e){
    }
    public void mouseExited(MouseEvent e){
    }
    public void mouseWheelMoved(MouseWheelEvent e){
    }
    public void mouseDragged(MouseEvent e){
    }
    public void mouseMoved(MouseEvent e){
    }
}
