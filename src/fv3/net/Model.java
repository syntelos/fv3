/*
 * Fv3
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
package fv3.net;

import java.net.URL;

public abstract class Model 
    extends lxl.net.ContentLoader
    implements fv3.Model
{

    protected Model(URL source, boolean lazy){
        super(source,lazy);
    }
    protected Model(String codebase, String path, boolean lazy){
        super(codebase,path,lazy);
    }
    protected Model(String url, boolean lazy){
        super(url,lazy);
    }

}
