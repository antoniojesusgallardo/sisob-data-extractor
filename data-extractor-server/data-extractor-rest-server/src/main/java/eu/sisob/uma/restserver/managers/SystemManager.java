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
package eu.sisob.uma.restserver.managers;

import eu.sisob.uma.api.h2dbpool.H2DBCredentials;
import eu.sisob.uma.api.h2dbpool.H2DBPool;
import eu.sisob.uma.crawler.ResearchersCrawlerService;
import eu.sisob.uma.extractors.adhoc.cvfilesinside.InternalCVFilesExtractorService;
import eu.sisob.uma.extractors.adhoc.email.EmailExtractorService;
import eu.sisob.uma.extractors.adhoc.websearchers.WebSearchersExtractorService;
import eu.sisob.uma.npl.culturalHeritage.GateDataExtractorServiceCH;
import eu.sisob.uma.npl.researchers.GateDataExtractorService;
import eu.sisob.uma.restserver.TheConfig;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
/**
 * This class setups all the system and its components and then manage the releasing of all resources and components.
 */
public class SystemManager  {
    
    private static final Logger LOG = Logger.getLogger(SystemManager.class.getName());
    
    private static SystemManager instance = null;       
    private boolean is_running = false;  
    H2DBPool systemdbpool;
    private String version; 
    
    /**
     *
     * @return
     */
    public static SystemManager getInstance() {
        if(instance == null) {
            init();
        }
        return instance;
    }
    
    /**
     *
     * @return
     */
    public static void init() {
        instance = new SystemManager();
        try {
            instance.setup();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "System Manager cannot be initialized", ex);
        }
    }
    
    public static void stop() {
        if (instance  != null) {
            instance.shutdown();
        }
    }
    
    /**
     *
     */
    protected SystemManager(){       
        this.is_running = false;
        this.systemdbpool = null;
    }    
    
    /**
     *
     * @return
     */
    public H2DBPool getSystemDbPool(){
        return this.systemdbpool;
    }
    
    /**
     * Intialize thre system
     * @throws SQLException 
     */
    protected void setup() throws SQLException {       
        if(!this.is_running) {   
            
            Configuration config = TheConfig.getInstance();
            
            try {
                String serverFolder = TheConfig.getProperty("server.docs.folder");
                String dbFolder = serverFolder + File.separator + "db";
                
                H2DBCredentials credentials_systemdb = new H2DBCredentials(dbFolder,
                                                                           "system", 
                                                                           "sa", 
                                                                           "sa");
                this.systemdbpool = new H2DBPool(credentials_systemdb);
                
                if("enabled".equals(config.getString(TheConfig.SERVICES_CRAWLER))){
                    
                    String pathCrawler = serverFolder + File.separator + "crawler-data-service";
                    boolean traceUrls   = "true".equals(config.getString(TheConfig.SERVICES_CRAWLER_TRACEURLS));
                    boolean traceSearch = "true".equals(config.getString(TheConfig.SERVICES_CRAWLER_TRACESEARCH));
                    
                    ResearchersCrawlerService.setServiceSettings(pathCrawler, 
                                                                Thread.currentThread().getContextClassLoader().getResource("eu/sisob/uma/crawler/keywords"), 
                                                                traceUrls, 
                                                                traceSearch);
                    
                    ResearchersCrawlerService.createInstance();    
                    LOG.info("Crawler service initialized.");
                }
                
                if("enabled".equals(config.getString(TheConfig.SERVICES_WEBSEARCHER))){
                    WebSearchersExtractorService.createInstance();                    
                }        
                
                if("enabled".equals(config.getString(TheConfig.SERVICES_INTERNAL_CV_FILES))){
                    InternalCVFilesExtractorService.createInstance();
                }
                
                if("enabled".equals(config.getString(TheConfig.SERVICES_EMAIL))){
                    EmailExtractorService.createInstance();
                }
                
                if("enabled".equals(config.getString(TheConfig.SERVICES_GATE))){
                    //Gate
                    String pathRootGate = serverFolder + File.separator + "gate-data-extractor-service";
                    File local_gate_path = new File(pathRootGate);                                                                    
                    
                    if (!local_gate_path.exists()) {
                        LOG.log(Level.SEVERE, "Local path of gate files does not exist. Current path: {0}", pathRootGate);
                    }
                    
                    H2DBCredentials credentials_resolver = new H2DBCredentials(dbFolder,
                                                                               "locations", 
                                                                               "sa", 
                                                                               "sa");                        

                    H2DBCredentials credentials_trad_tables_academic = new H2DBCredentials(dbFolder,
                                                                               "academic_tables_traductions", 
                                                                               "sa", 
                                                                               "sa");
                    String pathGate = pathRootGate + File.separator + "GATE-6.0";
                    
                    GateDataExtractorService.setServiceSettings(pathGate, 
                                                                pathRootGate + File.separator + "KEYWORDS", 
                                                                1, 5, credentials_trad_tables_academic, credentials_resolver);
                    GateDataExtractorService.createInstance(); 
                    
                    GateDataExtractorServiceCH.createInstance(pathGate); 

                    LOG.info("Gate service initialized.");
                }
                
                this.version = initVersion();
                
                is_running = true;                
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "An exception ocurred while initializing directory and database structure: ", ex);
            
            } finally {               
                if(!is_running){
                    
                    LOG.log(Level.SEVERE, "Something has failed in initialization. Proceeed to object releasing.");
                    
                    if("enabled".equals(config.getString(TheConfig.SERVICES_CRAWLER))){
                        //Crawler
                        ResearchersCrawlerService.releaseInstance();
                        LOG.info("Crawler service initialized.");
                    }

                    if("enabled".equals(config.getString(TheConfig.SERVICES_GATE))){
                        //Gate                
                        GateDataExtractorService.releaseInstance();
                        GateDataExtractorServiceCH.releaseInstance();
                        LOG.info("Gate service release.");                        
                    }
                    
                    //DB
                    this.systemdbpool = null;
                    LOG.info("System db pool initialized.");
                    
                    this.is_running = false;
                }
                else{
                    LOG.info("System initialized!");
                }
            }
        }
    }
    
    /**
     * 
     */
    protected void shutdown()
    {    
        if(is_running){
            
            Configuration config = TheConfig.getInstance();
            
            if("enabled".equals(config.getString(TheConfig.SERVICES_CRAWLER))){
                //Crawler
                ResearchersCrawlerService.releaseInstance();
                LOG.info("Crawler service released.");
            }

            if("enabled".equals(config.getString(TheConfig.SERVICES_GATE))){
                //Gate                
                GateDataExtractorService.releaseInstance();
                GateDataExtractorServiceCH.releaseInstance();
                LOG.info("Gate service released.");                        
            }        
            
            //DB
            this.systemdbpool = null;
            LOG.info("DB Pool service release.");

            this.is_running = false;
            LOG.info("System going off!");
        }
    }  
    
    private static String initVersion(){
        String rVersion = "";
        
        try {
            
            final Properties properties = new Properties();            
            InputStream inputStream =  TheConfig.class.getClassLoader().getResourceAsStream("project.properties");
            properties.load(inputStream);
            rVersion = properties.getProperty("version");
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        return rVersion;
    }
    
    /**
     *
     * @return
     */
    public boolean IsRunning(){
        return is_running;
    }

    public String getVersion() {
        return version;
    }
}
