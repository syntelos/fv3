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
import fv3.font.TTFFontReader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;


public class Main 
    extends java.awt.Frame 
    implements java.awt.event.WindowListener
{

    public static void main(String[] argv){
        String name = "typeright";
        try {
            TTFFontReader reader = new TTFFontReader(name);
            try {
                TTFFont font = new TTFFont(name,reader);
                Main main = new Main(font);
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


    private final TTFFont font ;

    private final Screen screen;


    public Main(TTFFont font){
        super(font.getName());
        this.screen = new Screen(this);
        this.font = font;
        Rectangle window = this.screen.window;
        this.reshape(window.x,window.y,window.width,window.height);
        this.show();
    }


    public void update(Graphics g){
        this.update( (Graphics2D)g);
    }
    public void update(Graphics2D g){
        g.setColor(Color.white);
        Rectangle bounds = this.getBounds();
        g.fillRect(0,0,bounds.width,bounds.height);
        g.setColor(Color.black);
        TTFFont font = this.font;
        double x = 0, y = 0, width = bounds.width, height = bounds.height;
        for (int glc = 0, glz = font.getLength(); glc < glz; glc++){
            Path2D.Double glyph = font.getPath(glc);
            if (null != glyph){
                Rectangle2D.Double glyphBounds = (Rectangle2D.Double)glyph.getBounds2D();
                Graphics2D glyphGraphics = (Graphics2D)g.create();
                glyphGraphics.translate(x,y);
                glyphGraphics.draw(glyph);
                x += glyphBounds.width;
                if (x >= width){
                    x = 0.0;
                    y += glyphBounds.height;
                    if (y >= height)
                        break;
                }
            }
        }
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {
        this.hide();
    }
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
}
