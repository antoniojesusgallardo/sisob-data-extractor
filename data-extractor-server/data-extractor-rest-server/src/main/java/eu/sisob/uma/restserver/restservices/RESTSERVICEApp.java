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

package eu.sisob.uma.restserver.restservices;

import eu.sisob.uma.restserver.restservices.exceptions.JAXBExceptionMapper;
import eu.sisob.uma.restserver.restservices.security.AuthenticationFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class RESTSERVICEApp extends ResourceConfig
{
    public RESTSERVICEApp()
    {
        packages("eu.sisob.uma.restserver.restservices");
        
        // Required to upload files
        register(MultiPartFeature.class);
        
        // Register Auth Filter
        register(AuthenticationFilter.class);
        
        // Register exception mapper
        register(JAXBExceptionMapper.class);
    }
}
