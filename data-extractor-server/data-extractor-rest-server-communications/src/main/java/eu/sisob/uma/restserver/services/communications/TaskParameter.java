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
package eu.sisob.uma.restserver.services.communications;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskParameter
{
    /**
     *
     */
    private String key = "";   
    /**
     *
     */
    private String value = "";   
    
    
    /**
     *
     * @param key
     * @param ps
     * @return
     */
    public static String get(String key, TaskParameter[] ps)
    {
        String value = null;
        for(TaskParameter p : ps)
        {
            if(p.key.equals(key)){
                value  = p.value;
                break;
            }
        }
        return value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

