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
import fv3.font.FontOptions;
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
        String name = "NEUROPOL";
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
        this.addWindowListener(this);
        this.screen = new Screen(this);
        this.font = font;
        Rectangle window = this.screen.window;
        this.reshape(window.x,window.y,window.width,window.height);
        this.show();
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
        g.setColor(Color.black);

        TTFFont font = this.font;
        Path2D.Double glyph = font.getPath('A');
        if (null != glyph){
             g.drawString("Glyph 'A' ",10,40);
             g.draw(glyph);
         }
         else
             g.drawString("Glyph 'A' not found.",10,40);
    }
    public void windowOpened(WindowEvent e) {}
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
}
