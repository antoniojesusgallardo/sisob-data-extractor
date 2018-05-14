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

package eu.sisob.uma.restserver.client;

import eu.sisob.uma.restserver.TheConfig;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class RESTUri {
    
    /**
     * Return the URI to access to the file in the task folder
     * @param task_code
     * @param file_name
     * @param type 
     * @return
     */
    public static String getUriFile(String task_code, String file_name, String type){
        
        String url = TheConfig.getInstance().getString(TheConfig.SERVER_URL) + 
                        "/resources/file";
        
        url+=   "?task_code=" + task_code + 
                "&type=" + type +
                "&file=" + file_name;
        return url;
    }
}
