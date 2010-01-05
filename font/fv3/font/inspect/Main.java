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
package fv3.font.inspect;

import fv3.font.awt.TTFFont;
import fv3.font.awt.TTFGlyph;
import fv3.font.FontOptions;
import fv3.font.FontsDir;
import fv3.font.TTFFontReader;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class Main 
    extends java.awt.Frame 
    implements java.awt.event.ComponentListener,
               java.awt.event.KeyListener,
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

    private Rectangle titleDescBox;

    private AffineTransform norm, flip;

    private int index, descPage;

    private volatile boolean descGlyph, cursor;

    private String title, desc[];

    private TTFGlyph glyph;

    private Font large, small, micro;

    private Color titleBg, titleFg, gridFg, gridBg;

    private BufferedImage backing;

    private FontsDir fontsDir;


    public Main(String name){
        super(name);
        this.addComponentListener(this);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addWindowListener(this);
        this.micro = new Font(Font.MONOSPACED,Font.PLAIN,8);
        this.small = new Font(Font.MONOSPACED,Font.PLAIN,12);
        this.large = new Font(Font.MONOSPACED,Font.BOLD,28);
        this.titleBg = new Color(0.7f,0.7f,0.7f,0.7f);
        this.titleFg = new Color(0.9f,0.1f,0.1f,0.9f);
        this.gridBg = new Color(0.7f,0.0f,0.0f,0.7f);
        this.gridFg = new Color(1.0f,0.0f,0.0f,1.0f);
        this.norm = new AffineTransform();
        this.titleDescBox = new Rectangle(30,30,500,100);
        this.fontsDir = new FontsDir();
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
            this.title = String.format("%s glyph '%c' 0x%x @ Index %d",this.font.getName(),ch,(int)ch,this.index);
            if (this.descGlyph)
                this.desc = glyph.getPath2dDescription();
            else
                this.desc = this.font.getDescription();
            this.descPage = 0;
        }
        else {
            this.title = String.format("%s glyph not found @ Index %d",this.font.getName(),this.index);
            if (this.descGlyph)
                this.desc = null;
            else
                this.desc = this.font.getDescription();

            this.descPage = 0;
        }
    }
    private boolean _desc(int dp){
        if (dp != this.descPage){
            this.descPage = dp;
            return true;
        }
        else
            return false;
    }
    protected int descLen(){
        String[] desc = this.desc;
        if (null == desc)
            return 0;
        else
            return desc.length;
    }
    protected boolean descHome(){
        return this._desc(0);
    }
    protected boolean descPgUp(){
        int dp = (this.descPage - 10);
        if (0 > dp)
            return this._desc(0);
        else
            return this._desc(dp);
    }
    protected boolean descPgDn(){
        int len = this.descLen();
        if (0 == len)
            return this._desc(0);
        else {
            int dp = (this.descPage + 10);
            if (dp > len)
                return this._desc(len-10);
            else
                return this._desc(dp);
        }
    }
    protected boolean descEnd(){
        int len = this.descLen();
        if (0 == len)
            return this._desc(0);
        else {
            return this._desc(len-10);
        }
    }
    public void update(Graphics g){
        BufferedImage backing = this.backing;
        if (null == backing)
            backing = this.backing();
        Graphics2D bg = backing.createGraphics();
        try {
            this.update(bg);
        }
        finally {
            bg.dispose();
        }
        g.drawImage(backing,0,0,this);
    }
    public void paint(Graphics g){
        this.update(g);
    }
    public void update(Graphics2D g){
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        /*
         * 
         */
        g.setColor(Color.white);
        Rectangle bounds = this.getBounds();
        g.fillRect(0,0,bounds.width,bounds.height);
        /*
         * 
         */
        TTFGlyph glyph = this.glyph;

        if (null != glyph){

            //
            g.setTransform(this.flip);

            glyph.drawGrid(g,this.small,this.micro,this.gridBg,this.gridFg);

            //
            g.setTransform(this.norm);

            this.drawTitle(g);

            this.drawDesc(g);
            //
            g.setTransform(this.flip);

            g.setColor(Color.black);

            glyph.drawOutline(g);
        }
        else {
            this.drawTitle(g);
        }
    }
    private void drawDesc(Graphics2D g){

        String[] desc = this.desc;

        float x = 40.0f, y = 100.0f;

        if (null != this.desc){
            g.setFont(this.small);
            g.setColor(this.gridBg);

            for (int cc = this.descPage, count = this.desc.length; cc < count; cc++){
                g.drawString(this.desc[cc],x,y);
                y += 20;
            }
            this.titleDescBox.height = (int)Math.ceil(y);
        }
    }
    private void drawTitle(Graphics2D g){

        String title = this.title;

        float x = 30.0f, y = 60.0f;

        g.setFont(this.large);

        g.setColor(this.titleBg);

        g.drawString(title,x-0.8f,y-0.8f);

        g.setColor(this.titleFg);

        g.drawString(title,x,y);
    }
    private BufferedImage backing(){
        if (null != this.backing)
            this.backing.flush();
        this.backing = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
        return this.backing;
    }
    public void componentResized(ComponentEvent e){
        this.backing();
    }
    public void componentMoved(ComponentEvent e){
    }
    public void componentShown(ComponentEvent e){
        this.backing();
    }
    public void componentHidden(ComponentEvent e){
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
                if (this.descHome())
                    this.repaint();
                break;
            case KeyEvent.VK_PAGE_UP:
                if (this.descPgUp())
                    this.repaint();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                this.dec();
                this.repaint();
                break;
            case KeyEvent.VK_END:
                if (this.descEnd())
                    this.repaint();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                if (this.descPgDn())
                    this.repaint();
                break;
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
        int x = e.getX();
        int y = e.getY();
        if (this.titleDescBox.contains(x,y)){
            this.descGlyph = (!this.descGlyph);
            if (this.descGlyph){
                if (null != this.glyph)
                    this.desc = this.glyph.getPath2dDescription();
                else
                    this.desc = null;
            }
            else
                this.desc = this.font.getDescription();
            this.repaint();
        }
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
        if (0 > e.getWheelRotation()){
            if (this.descPgUp())
                this.repaint();
        }
        else {
            if (this.descPgDn())
                this.repaint();
        }
    }
    public void mouseDragged(MouseEvent e){
    }
    public void mouseMoved(MouseEvent e){
        if (e.getX() < 600){
            if (this.cursor)
                return;
            else {
                this.cursor = true;
                this.setCursor(Cursor.HAND_CURSOR);
            }
        }
        else if (this.cursor){
            this.cursor = false;
            this.setCursor(Cursor.DEFAULT_CURSOR);
        }
    }
}
