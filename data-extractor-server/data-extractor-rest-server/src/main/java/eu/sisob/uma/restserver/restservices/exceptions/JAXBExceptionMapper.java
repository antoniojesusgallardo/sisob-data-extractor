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
package eu.sisob.uma.restserver.restservices.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
@Provider
public class JAXBExceptionMapper implements ExceptionMapper<WebApplicationException> {
   
    @Override
    public Response toResponse(WebApplicationException exception) {
        
        int statusCode = Status.BAD_REQUEST.getStatusCode();
        if (exception.getResponse() != null && exception.getResponse().getStatus()>0) {
           statusCode = exception.getResponse().getStatus();
        }
        return Response.status(statusCode)
                        .entity(exception.getMessage())
                        .type(MediaType.TEXT_PLAIN)
                        .build();
    }
}
