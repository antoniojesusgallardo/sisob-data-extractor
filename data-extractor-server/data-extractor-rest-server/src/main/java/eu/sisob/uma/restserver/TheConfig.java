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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


public class TheConfig
{   
    private static final Logger LOG = Logger.getLogger(TheConfig.class.getName());
    
    private static Configuration instance = null;
    
    
    public static final String SERVER_DEBUG = "server.debug";
    public static final String SERVER_URL = "server.url";    
    public static final String SERVER_DEBUG_URL = "server.debug_url";
    public static final String SERVER_REAL_PATH = "server.real_path";
    public static final String SERVICES_CRAWLER = "services.crawler";    
    public static final String SERVICES_CRAWLER_TRACEURLS =     "services.crawler_traceurls";   
    public static final String SERVICES_CRAWLER_TRACESEARCH =   "services.crawler_tracesearch";   
    public static final String SERVICES_GATE = "services.gate";        
    public static final String SERVICES_LINKEDIN_EXTRACT_PROFILES = "services.linkedin_extract_profiles"; 
    public static final String SERVICES_INTERNAL_CV_FILES = "services.internalcvfiles";      
    public static final String SERVICES_WEBSEARCHER = "services.websearcher";        
    public static final String SERVICES_EMAIL = "services.email";            
    public static final String SYSTEMEMAIL_ADDRESS = "systemmail.address";        
    public static final String SYSTEMEMAIL_PASSWORD = "systemmail.password";      

    /**
     * Edu
     */
    public static final String SERVER_DOCS_FOLDER = "server.docs.folder";      
    
    
    /**
     *
     * @return
     */
    public static Configuration getInstance() {
        return instance; 
    }
    
    public static String getProperty (String param) {
        if (instance != null) {
            return instance.getString(param);
        }
    
        LOG.log(Level.SEVERE, "Configuration object not yet instanced");        
        return null;
    }
    
    /**
     *
     * @param path
     * @throws ConfigurationException
     */
    public static void createInstance(String path) throws ConfigurationException {
        if(instance == null) {
            String filepath = path + File.separator + "WEB-INF" + File.separator + "server.properties";
            File file = new File(filepath);
            instance = new PropertiesConfiguration(file); 
            
            String debug = instance.getString(SERVER_DEBUG);
            
            if(debug.equals("true")) {
                LOG.log(Level.INFO, "Configuration in debug mode: {0}", filepath);
                instance.setProperty(TheConfig.SERVER_URL, instance.getString(SERVER_DEBUG_URL));
            }
            else {
                LOG.log(Level.INFO, "Configuration in production mode: {0}", filepath);
                instance.setProperty(TheConfig.SERVER_URL, instance.getString(SERVER_URL));
            }            
            
            instance.setProperty(SERVER_REAL_PATH, path);
            
            LOG.log(Level.INFO, "Configuration object initialized from: {0}", filepath);                        
        }
    }   
}
