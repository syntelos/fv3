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

import fv3.font.awt.FontsDir;
import fv3.font.awt.TTFFont;
import fv3.font.awt.TTFGlyph;

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

        Main main = new Main();
        Screen screen = new Screen(main);
        main.init(screen);
    }

    private enum Nav {
        Fonts, Glyphs, Desc;
    }

    private final Font large, small, micro;

    private final Color titleBg, titleFg, gridFg, gridBg;

    private final FontsDir fontsDir;

    private volatile int fontsDirIndex;

    private final AffineTransform norm;

    private volatile AffineTransform flip;

    private volatile TTFFont font ;

    private volatile Rectangle2D.Double display;

    private volatile Point2D.Double displayCenter;

    private volatile int glyphIndex, descPage;

    private volatile double glLeft, glTop;

    private volatile boolean descGlyph, cursor;

    private volatile Nav nav = Nav.Glyphs;

    private volatile String titleString, desc[];

    private volatile TTFGlyph glyph;

    private volatile Font titleFont;

    private volatile BufferedImage backing;


    public Main(){
        super("");
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
        this.fontsDir = new FontsDir();
        this.fontsDirIndex = this.fontsDir.indexOf("NEUROPOL");
    }


    public void init(Screen screen){
        Rectangle window = screen.window;
        this.reshape(window.x,window.y,window.width,window.height);
        this.show();
    }
    public void init(){
        TTFFont font = this.fontsDir.cache(this.fontsDirIndex,(this.display.width*0.9),(this.display.height*0.9));
        if (null != font){
            this.font = font;
            this.titleFont = this.fontsDir.as(this.fontsDirIndex,Font.BOLD,28);
            if (null == this.titleFont)
                this.titleFont = this.large;

            this.setName(font.getName());
            this.font = font;
            if (null != this.glyph){
                if (!this.glyphSet(this.glyph.character,false))
                    this.glyphSet('A',false);
            }
            else
                this.glyphSet('A',false);
        }
    }
    public void reshape(int x, int y, int w, int h){
        super.reshape(x,y,w,h);
        
        this.display = new Rectangle2D.Double(72d,72d,(w-144d),(h-144d));

        this.displayCenter = new Point2D.Double(((display.width-display.x)/2.0),((display.height-display.y)/2.0));

        this.flip = AffineTransform.getQuadrantRotateInstance(2,this.displayCenter.x,this.displayCenter.y);
        this.flip.translate((this.display.x),this.display.y);

        this.glLeft = Math.max(250,(this.display.width-800));
        this.glTop = Math.max(250,(this.display.height-100));

        this.init();
    }
    protected boolean glyphDec(int dec){
        return this.glyphSet(this.glyphIndex-dec);
    }
    protected boolean glyphInc(int inc){
        return this.glyphSet(this.glyphIndex+inc);
    }
    protected boolean glyphHome(){
        return this.glyphSet(0);
    }
    protected boolean glyphEnd(){
        return this.glyphSet(this.font.getLength()-1);
    }
    protected boolean glyphSet(int idx){
        return this._glyphSet(idx,true);
    }
    protected boolean glyphSet(char ch, boolean nav){
        int idx = this.font.indexOf(ch);
        return this._glyphSet(idx,nav);
    }
    protected boolean glyphSet(char ch){
        return this.glyphSet(ch,true);
    }
    private boolean _glyphSet(int idx, boolean nav){
        if (nav)
            this.nav = Nav.Glyphs;

        if (-1 < idx){
            if (idx >= this.font.getLength())
                idx = (idx - this.font.getLength());
        }
        else {
            idx = (idx + this.font.getLength());
        }

        if (idx != this.glyphIndex || (!nav)){
            this.glyphIndex = idx;
            TTFGlyph glyph = (TTFGlyph)this.font.get(idx);
            this.glyph = glyph;
            if (null != glyph){
                char ch = glyph.character;
                this.titleString = String.format("%s glyph '%c' 0x%x @ Index %d",this.font.getName(),ch,(int)ch,this.glyphIndex);
                if (this.descGlyph)
                    this.desc = glyph.getPath2dDescription();
                else
                    this.desc = this.font.getDescription();
                this.descPage = 0;
            }
            else {
                this.titleString = String.format("%s glyph not found @ Index %d",this.font.getName(),this.glyphIndex);
                if (this.descGlyph)
                    this.desc = null;
                else
                    this.desc = this.font.getDescription();

                this.descPage = 0;
            }
            return true;
        }
        else
            return false;
    }
    protected boolean fontsInc(int n){
        this.nav = Nav.Fonts;

        this.fontsDirIndex += n;
        if (this.fontsDirIndex >= this.fontsDir.size)
            this.fontsDirIndex = 0;
        this.init();
        return true;
    }
    protected boolean fontsDec(int n){
        this.nav = Nav.Fonts;

        this.fontsDirIndex -= n;
        if (0 > this.fontsDirIndex)
            this.fontsDirIndex = (this.fontsDir.size-1);
        this.init();
        return true;
    }
    protected boolean fontsHome(){
        this.nav = Nav.Fonts;
        if (0 != this.fontsDirIndex){
            this.fontsDirIndex = 0;
            this.init();
            return true;
        }
        else
            return false;
    }
    protected boolean fontsEnd(){
        this.nav = Nav.Fonts;
        int idx = (this.fontsDir.size-1);
        if (idx != this.fontsDirIndex){
            this.fontsDirIndex = idx;
            this.init();
            return true;
        }
        else
            return false;
    }
    protected void descToggle(){
        this.nav = Nav.Desc;

        this.descGlyph = (!this.descGlyph);
        if (this.descGlyph){
            if (null != this.glyph)
                this.desc = this.glyph.getPath2dDescription();
            else
                this.desc = null;
        }
        else
            this.desc = this.font.getDescription();
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
    protected boolean descDec(int dec){
        int dp = (this.descPage - dec);
        if (0 > dp)
            return this._desc(0);
        else
            return this._desc(dp);
    }
    protected boolean descInc(int inc){
        int len = this.descLen();
        if (0 == len)
            return this._desc(0);
        else {
            int dp = (this.descPage + inc);
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
    protected boolean inc(){
        switch (this.nav){
        case Fonts:
            return this.fontsInc(1);
        case Glyphs:
            return this.glyphInc(1);
        default:
            return this.descInc(1);
        }
    }
    protected boolean dec(){

        switch (this.nav){
        case Fonts:
            return this.fontsDec(1);
        case Glyphs:
            return this.glyphDec(1);
        default:
            return this.descDec(1);
        }
    }
    protected boolean pgUp(){
        switch (this.nav){
        case Fonts:
            return this.fontsDec(10);
        case Glyphs:
            return this.glyphDec(10);
        default:
            return this.descDec(10);
        }
    }
    protected boolean pgDn(){
        switch (this.nav){
        case Fonts:
            return this.fontsInc(10);
        case Glyphs:
            return this.glyphInc(10);
        default:
            return this.descInc(10);
        }
    }
    protected boolean home(){
        switch (this.nav){
        case Fonts:
            return this.fontsHome();
        case Glyphs:
            return this.glyphHome();
        default:
            return this.descHome();
        }
    }
    protected boolean end(){
        switch (this.nav){
        case Fonts:
            return this.fontsEnd();
        case Glyphs:
            return this.glyphEnd();
        default:
            return this.descEnd();
        }
    }
    public void update(Graphics g){
        BufferedImage backing = this.backing;
        if (null == backing)
            backing = this.backing();
        Graphics2D bg = backing.createGraphics();
        try {
            this.draw(bg);
        }
        finally {
            bg.dispose();
        }
        g.drawImage(backing,0,0,this);
    }
    public void paint(Graphics g){
        this.update(g);
    }
    public void draw(Graphics2D g){
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

            this.font.drawGrid(g,this.small,this.micro,this.gridBg,this.gridFg, this.glLeft, this.glTop);

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
        }
    }
    private void drawTitle(Graphics2D g){

        String title = this.titleString;
        if (null != title){
            float x = 30.0f, y = 60.0f;
            g.setFont(this.titleFont);
            g.setColor(this.titleBg);
            g.drawString(title,x-0.8f,y-0.8f);
            g.setColor(this.titleFg);
            g.drawString(title,x,y);
        }
    }

    private BufferedImage backing(){
        if (null != this.backing)
            this.backing.flush();
        this.backing = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
        return this.backing;
    }
    public void componentResized(ComponentEvent e){
        Rectangle bounds = this.getBounds();

        BufferedImage backing = this.backing;
        if (null != backing){
            this.backing = null;
            backing.flush();
        }
        this.backing = new BufferedImage(bounds.width,bounds.height,BufferedImage.TYPE_INT_ARGB);

        this.reshape(bounds.x,bounds.y,bounds.width,bounds.height);
        this.repaint();
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
            this.glyphSet(ch);
            this.repaint();
        }
    }
    public void keyPressed(KeyEvent e){
    }
    public void keyReleased(KeyEvent e){
        if (e.isActionKey()){
            switch (e.getKeyCode()){
            case KeyEvent.VK_HOME:
                if (this.home())
                    this.repaint();
                break;
            case KeyEvent.VK_PAGE_UP:
                if (this.pgUp())
                    this.repaint();
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                if (this.dec())
                    this.repaint();
                break;
            case KeyEvent.VK_END:
                if (this.end())
                    this.repaint();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                if (this.pgDn())
                    this.repaint();
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                if (this.inc())
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

        if (e.getX() < 600){

            if (e.getY() < 100){
                if (e.isPopupTrigger())
                    this.fontsDec(1);
                else 
                    this.fontsInc(1);
            }
            else {
                this.descToggle();
            }
        }
        else {
            if (e.getY() < 100){
                if (e.isPopupTrigger())
                    this.fontsDec(1);
                else 
                    this.fontsInc(1);
            }
            else {
                if (e.isPopupTrigger())
                    this.glyphDec(1);
                else 
                    this.glyphInc(1);
            }
        }
        this.repaint();
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
            if (this.pgUp())
                this.repaint();
        }
        else {
            if (this.pgDn())
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
