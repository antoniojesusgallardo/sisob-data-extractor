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

import java.lang.Thread.UncaughtExceptionHandler;
import eu.sisob.uma.api.prototypetextmining.DataRepository;
import eu.sisob.uma.api.prototypetextmining.MiddleData;
import eu.sisob.uma.api.prototypetextmining.RepositoryProcessedDataXML;
import eu.sisob.uma.api.concurrent.threadpoolutils.CallbackableTaskWithResource;
import eu.sisob.uma.api.concurrent.threadpoolutils.ExecutorResource;
import eu.sisob.uma.api.prototypetextmining.RepositoryPreprocessDataMiddleData;
import eu.sisob.uma.api.prototypetextmining.globals.DataExchangeLiterals;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;


/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class GateDataExtractorCH implements CallbackableTaskWithResource                               
{        
    private static final Logger log = Logger.getLogger(GateDataExtractorCH.class.getName());
    
    private RepositoryPreprocessDataMiddleData taskRepPrePro;
    
    private ExecutorResource executorResource; 
    
    private Document xmlResult;        
    boolean occursUncaugthExceptionInGateProcess;

    Boolean finished;
    
    /**
     *
     * @param taskRepPrePro
     */
    public GateDataExtractorCH(RepositoryPreprocessDataMiddleData taskRepPrePro){
        this.taskRepPrePro = taskRepPrePro;
        this.finished = false;        
    }	
       
    @Override
    public void executeTask() {
        
        setFinished(false);
        GateTextMiningParserCH parser = null;
        
        try {
            //Take parser and load input repository                     
            parser = (GateTextMiningParserCH)this.executorResource.getResource(); 

            // Load input data
            String parserId = DataExchangeLiterals.ID_TEXTMININGPARSER_GATE_CULTURAL_HERITAGE;
            List<MiddleData> mds;
            while((mds = taskRepPrePro.getData(parserId, 100)).size() > 0){
                for(MiddleData md : mds){
                    ((DataRepository)parser.getRepOutput()).addData(md);
                } 
            }                   

            // Exception handler to GATE thread
            UncaughtExceptionHandler he = new UncaughtExceptionHandler() {
                public Throwable e = null;
                @Override
                public void uncaughtException(Thread t, Throwable e) {                        
                    occursUncaugthExceptionInGateProcess = true;
                    log.info(String.format("uncaughtException: %s", e.getMessage()));                        
                }
            };

            //Launch GATE parser     
            this.occursUncaugthExceptionInGateProcess = false;
            Thread th = new Thread(parser);
            th.setUncaughtExceptionHandler(he);                    
            th.start();                    
            th.join();  

            // Save the result in a XML document
            RepositoryProcessedDataXML repXML = (RepositoryProcessedDataXML)parser.getRepInput();
            this.xmlResult = (Document) repXML.getDocXML().clone(); 
        }
        catch(Exception ex) {            
            log.log(Level.SEVERE, ex.getMessage());
        }
        finally {   
            if(this.occursUncaugthExceptionInGateProcess){                        
                if(parser != null){
                    log.info("Begin reload GATE parser because succeded a uncaughtException!");        
                    try{                                          
                        parser.endActions();
                        parser.iniActions();
                    }
                    catch (Exception ex){
                        log.log(Level.SEVERE, ex.getMessage());
                    }
                    log.info("Done!"); 
                }
            }
            if(parser.getRepOutput() != null){ 
                ((DataRepository)parser.getRepOutput()).clearData();                    
            }
            if(parser.getRepInput() != null){ 
                parser.getRepInput().clearData();
            }
            
            setFinished(true);
        }   
    }   
    
    @Override
    public void executeCallBackOfTask(){
        
        if(this.xmlResult != null){
            log.info("Result success");                                                       
        }
        else{
            log.info("No results.");
        }
        setFinished(true);
    }  

    public Document getXMLResults(){
        if(this.finished)
            return xmlResult;
        else
            return null;
    }
    
    @Override
    public synchronized ExecutorResource getExecutorResource() {
        return this.executorResource;
    }

    @Override
    public synchronized void setExecutorResource(ExecutorResource executorResource){
        this.executorResource = executorResource;
    }
    
    @Override
    public boolean isFinished(){
        synchronized(finished) {
            return finished;
        }
    }

    public void setFinished(boolean b){
        synchronized(finished) {
            finished = b;
        }
    }
}
