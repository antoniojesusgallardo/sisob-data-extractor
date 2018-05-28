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
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientConfig;

/**
 * REST Client used in JSP pages, this client use Jersey resources.
 * 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class RESTClient {
    
    private static final Logger log = Logger.getLogger(RESTClient.class.getName());
    

    public static final String REST_PATH = "/resources";
    
    private String token;
    
    private static String rootPath;
    
    static {
        String serverUrl = TheConfig.getInstance().getString(TheConfig.SERVER_URL);
        rootPath = serverUrl + REST_PATH;
    }
    
    private RESTClient() {
    }

    /**
     * Constructor that initializes the REST client.
     * @param token 
     */
    public RESTClient(String token) {
        this.token = token;
    }
    
    
    /**
     * Method that executes the request and return the results.
     * @param path
     * @param outputType
     * @param params
     * @return 
     * @throws eu.sisob.uma.restserver.client.ApiErrorException
     */
    public Object get(String path, Object outputType, Map<String,String> params) 
                                                        throws ApiErrorException{
        
        Response response = getResponse(path, outputType, params);
        
        Object rObject = null;
        if (outputType instanceof Class) {
            rObject = response.readEntity((Class)outputType);
        }
        else if (outputType instanceof GenericType) {
            rObject = response.readEntity((GenericType)outputType);
        }
        
        return rObject;
    }
    
    
    public Response getResponse(String path, Object outputType, Map<String,String> params) 
                                                        throws ApiErrorException{
        
        String fullPath = rootPath + path;
        
        ClientConfig clientConfig = new ClientConfig();
        Client client =  ClientBuilder.newClient(clientConfig);
        WebTarget  webTarget = client.target(fullPath);
        
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
            }
        }
        
        // invoke service
        Invocation.Builder invocationBuilder;
        invocationBuilder = webTarget
                                .request(MediaType.APPLICATION_FORM_URLENCODED)
                                .accept(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .get();
        
        if (response.getStatus() != Status.OK.getStatusCode()) {
            String errorMessage = response.readEntity(String.class);
            log.warning(errorMessage);
            throw new ApiErrorException(response.getStatus(), errorMessage);
        }
        
        return response;
    }
    
    public Response postResponse(String path, Object data) throws ApiErrorException{
        
        String fullPath = rootPath + path;
        
        ClientConfig clientConfig = new ClientConfig();
        Client client =  ClientBuilder.newClient(clientConfig);
        WebTarget  webTarget = client.target(fullPath);
        
        // invoke service
        Invocation.Builder invocationBuilder;
        invocationBuilder = webTarget
                                .request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .post(Entity.entity(data, MediaType.APPLICATION_JSON));
        
        if (response.getStatus() != Status.OK.getStatusCode()) {
            String errorMessage = response.readEntity(String.class);
            log.warning(errorMessage);
            throw new ApiErrorException(response.getStatus(), errorMessage);
        }
        
        return response;
    }
    
}