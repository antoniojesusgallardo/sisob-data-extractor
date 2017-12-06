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

package eu.sisob.uma.npl.culturalHeritage;

import eu.sisob.uma.api.concurrent.threadpoolutils.CallbackableTaskExecutionWithResource;
import eu.sisob.uma.api.concurrent.threadpoolutils.CallbackableTaskPoolExecutorWithResources;
import eu.sisob.uma.npl.GateConstant;
import gate.Gate;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class GateDataExtractorServiceCH 
{    
    private static final Logger log = Logger.getLogger(GateDataExtractorServiceCH.class.getName());
    
    private static GateDataExtractorServiceCH instance = null;
    
    // Config params - default values
    private String gatePath;  
    private int blockSpeed = 5;  // Number of documents that contains corpus (block of documents processed by GATE)  
    
    private CallbackableTaskPoolExecutorWithResources ctpe; 
    
    @SuppressWarnings("CallToPrintStackTrace")
    private GateDataExtractorServiceCH(String pGatePath) {
        
        try{   
            gatePath = pGatePath;
            ctpe = null;
            
            // Init Gate
            if (!Gate.isInitialised()) {
                log.info("Starting GATE initialization ...");      
            
                if(!new File(gatePath, "gate.xml").exists()){
                    throw new FileNotFoundException("Could not locate the gate.xml "
                                        + "configuration file! (" + gatePath + ")");            
                }
                
                Gate.setGateHome(new File(gatePath));
                Gate.setPluginsHome(new File(gatePath, GateConstant.DIRECTORY_PLUGINS));
                Gate.init();            
                
                File fileANNIE = new File(Gate.getPluginsHome(), GateConstant.DIRECTORY_PLUGIN_ANNIE);
                Gate.getCreoleRegister().registerDirectories(fileANNIE.toURI().toURL());
                
                File fileTools = new File(Gate.getPluginsHome(), GateConstant.DIRECTORY_PLUGIN_TOOLS);
                Gate.getCreoleRegister().registerDirectories(fileTools.toURI().toURL());
                
                log.info("GATE initialization done!"); 
            }

            // Init parser
            GateTextMiningParserCH parser = new GateTextMiningParserCH(blockSpeed);
            parser.iniActions();
            
            List<GateTextMiningParserCH> parsers = new ArrayList<GateTextMiningParserCH>();
            parsers.add(parser);
            
            ctpe = new CallbackableTaskPoolExecutorWithResources(parsers.toArray(), 
                                                                 parsers.size()+100, 
                                                                 60);
        }
        catch(Exception ex){
            log.severe("ERROR: GateDataExtractorServiceCH - Initialitation");  
            ex.printStackTrace();
            ctpe = null;
        }        
    } 
    
    /**
     *
     * @param cte
     * @throws Exception
     */
    public static void addExecution(CallbackableTaskExecutionWithResource cte) throws Exception{ 
        try {
            CallbackableTaskPoolExecutorWithResources refctpe = GateDataExtractorServiceCH.getInstance().ctpe;
            if (refctpe != null) {
                refctpe.runTask(cte);
            }
            else{            
                throw new Exception("Gate Data Extractor Service is not running");
            }    
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }  
    
    public synchronized static void createInstance(String gatePath) {
        if (instance == null) { 
            instance = new GateDataExtractorServiceCH(gatePath);
        }
    }
    
    public static GateDataExtractorServiceCH getInstance() throws Exception{
        if (instance == null) {
            throw new Exception("Instance has not been created yet. First you have to create it");
        }
        return instance;
    }
    
    
    public static void releaseInstance() {
        if (instance != null) {
            instance.releaseResources();
            instance = null;
        }        
    }
    
    private void releaseResources(){
        if (ctpe != null){
            ctpe.shutDown();
        }                      
    }
}
