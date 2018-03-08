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

package eu.sisob.uma.restserver;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * REST Client used in JSP pages, this client use Jersey resources.
 * 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class RESTClient {

    private static String rootPath;
    
    private String path;
    
    private MultivaluedMap params;
    
    private Class outputClass;
    
    private GenericType genericType;
    
    static {
        rootPath = TheConfig.getInstance().getString(TheConfig.SERVER_URL) + "/resources";
    }
    
    private RESTClient() {
    }
    
    /**
     * Constructor that initializes the REST client.
     * @param path
     * @param outputClass
     * @param params 
     */
    public RESTClient(String path, Map<String,String> params, Class outputClass) {
        
        initialize(path, params, outputClass, null);
    }
    
    /**
     * Constructor that initializes the REST client.
     * @param path
     * @param params 
     * @param genericType
     */
    public RESTClient(String path, Map<String,String> params, GenericType genericType) {
        
        initialize(path, params, null, genericType);
    }
    
    private void initialize(String path, Map<String,String> params, Class outputClass, GenericType genericType){
    
        if(outputClass != null){
            this.outputClass = outputClass;
        }
        else if (genericType != null) {
            this.genericType = genericType;
        }
        
        this.path = path;
        
        this.params = new MultivaluedMapImpl();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                this.params.add(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * Method that executes the request and return the results.
     * @return 
     */
    public Object get(){
        
        String fullPath = rootPath + path;
        
        Client client = Client.create();
        WebResource webResource = client.resource(fullPath); 
        
        Object rObject = null;
        
        if(outputClass != null){
            rObject = webResource.queryParams(this.params)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .get(outputClass);
        }
        else if (genericType != null) {
            rObject = Client.create().resource(fullPath).
                                        queryParams(this.params)
                                        .get(genericType);
        }
        
        return rObject;
    }
}
