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

import eu.sisob.uma.restserver.restservices.security.AuthenticationUtils;
import java.util.logging.Logger;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class RESTSERVICEBase {
    
    @HeaderParam(HttpHeaders.AUTHORIZATION) 
    protected String token;
    
    protected static final Logger LOG = Logger.getLogger(RESTSERVICEBase.class.getName());
    
    public void validateRequired(String pString, String fieldName) throws WebApplicationException{
        
        if (pString==null || pString.trim().isEmpty()) {
            Response response = Response.status(Response.Status.BAD_REQUEST)
                                        .entity(fieldName + " is required.")
                                        .type(MediaType.TEXT_PLAIN)
                                        .build();
            throw new WebApplicationException(response);
        }
    }
}
