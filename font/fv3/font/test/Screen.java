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
/*
 * Copyright (c) 2009 John Pritchard, all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fv3.font.test;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;


public final class Screen
    extends Object
{
    volatile static Screen Current;



    public final GraphicsEnvironment environment;

    public final GraphicsDevice device;

    public final GraphicsConfiguration configuration;

    public final Rectangle screen, window, display;


    /**
     * Fullscreen, with insets.
     */
    public Screen(Window window){
        this(window,GraphicsEnvironment.getLocalGraphicsEnvironment(),window.getGraphicsConfiguration());
    }
    private Screen(Window window, GraphicsEnvironment environment, GraphicsConfiguration gc){
        this(window,environment,gc,gc.getDevice());
    }
    private Screen(Window window, GraphicsEnvironment environment, GraphicsConfiguration gc, GraphicsDevice device){
        super();
        Current = this;
        this.environment = environment;
        this.device = device;
        this.configuration = gc;
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        this.screen = this.configuration.getBounds();
        this.screen.y += insets.top;
        this.screen.height -= (insets.top + insets.bottom);
        this.screen.x += insets.left;
        this.screen.height -= (insets.left + insets.right);
        this.window = new Rectangle(this.screen);
        insets = window.getInsets();
        this.window.y += insets.top;
        this.window.height -= (insets.top + insets.bottom);
        this.window.x += insets.left;
        this.window.height -= (insets.left + insets.right);
        this.display = new Rectangle(this.window);
        this.display.x += 100;
        this.display.width -= 200;
        this.display.y += 100;
        this.display.height -= 200;
    }


    public final GraphicsEnvironment getEnvironment(){
        return this.environment;
    }
    public final GraphicsDevice getDevice(){
        return this.device;
    }
    public final GraphicsConfiguration getConfiguration(){
        return this.configuration;
    }
}
