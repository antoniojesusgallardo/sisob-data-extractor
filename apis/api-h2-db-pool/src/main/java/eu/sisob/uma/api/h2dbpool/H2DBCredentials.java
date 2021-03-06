/*
    Copyright (c) 2014 "(IA)2 Research Group. Universidad de Málaga"
                        http://iaia.lcc.uma.es | http://www.uma.es

    This file is part of SISOB Data Extractor.

    SISOB Data Extractor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SISOB Data Extractor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SISOB Data Extractor. If not, see <http://www.gnu.org/licenses/>.
*/
package eu.sisob.uma.api.h2dbpool;

import java.io.File;


public class H2DBCredentials 
{
    public final String user;
    public final String pass;
    public final String dbpath;
    public final String dbname;
    public final String connpath;
    
    public H2DBCredentials(String dbpath, String dbname, String user, String pass)
    {
        this.dbpath = dbpath;
        this.dbname = dbname;
        this.user = user;
        this.pass = pass;
        this.connpath = dbpath + File.separator + dbname;
    }
    
    
}
