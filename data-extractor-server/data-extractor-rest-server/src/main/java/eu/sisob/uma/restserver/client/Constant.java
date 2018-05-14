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

import eu.sisob.uma.restserver.restservices.RESTSERVICEFile;
import eu.sisob.uma.restserver.services.gateCH.GateTaskCH;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class Constant {
    
    public static final String FILE_TYPE_SOURCE             = RESTSERVICEFile.FILE_TYPE_SOURCE;
    public static final String FILE_TYPE_VERBOSE            = RESTSERVICEFile.FILE_TYPE_VERBOSE;
    public static final String FILE_TYPE_RESULT             = RESTSERVICEFile.FILE_TYPE_RESULT;
    public static final String FILE_TYPE_DETAILED_RESULT    = RESTSERVICEFile.FILE_TYPE_DETAILED_RESULT;
    
    public static final String TASK_NAME_CH = GateTaskCH.NAME;
}
