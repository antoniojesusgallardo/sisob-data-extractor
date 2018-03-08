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

package eu.sisob.uma.restserver.services.gateCH;

import eu.sisob.uma.api.prototypetextmining.RepositoryPreprocessDataMiddleData;
import eu.sisob.uma.euParliament.beans.Speech;
import eu.sisob.uma.euParliament.FileGeneratorCSV;
import eu.sisob.uma.euParliament.FileGeneratorJSON;
import eu.sisob.uma.euParliament.FormatConversor;
import eu.sisob.uma.npl.culturalHeritage.GateConstantCH;
import eu.sisob.uma.npl.culturalHeritage.GateDataExtractorCH;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.Mailer;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
class GateDataExtractorTaskCH extends GateDataExtractorCH{
    
    private static final Logger log = Logger.getLogger(GateDataExtractorTaskCH.class.getName());
    
    protected String user;
    protected String pass;
    protected String taskCode;
    
    public GateDataExtractorTaskCH(RepositoryPreprocessDataMiddleData taskRepPrePro,                                             
                                            String user, 
                                            String pass, 
                                            String task_code) {              
        super(taskRepPrePro);        
        this.user = user;
        this.pass = pass;
        this.taskCode = task_code;
    }    
    
    @Override
    public void executeTask(){
        super.executeTask();
    }
    
    @Override
    public void executeCallBackOfTask() { 
        
        String email = this.user;
        String taskCodeFolder = TaskFileManager.getTaskFolder(user, taskCode);
        
        // Generate result files. XML, JSON and CSV files.  
        synchronized(AuthorizationManager.getLocker(email)){
            try {
                createResultFiles(this.getXMLResults(), taskCodeFolder);
                log.info(String.format("Result writed in %s", taskCodeFolder));
            } 
            catch (Exception ex) {
                log.log(Level.SEVERE, "Error creating results", ex); 
                TaskFileManager.notifyResultError(email, this.taskCode, "Error creating results.");
            }
        }
        
        // Send mail
        Mailer.notifyResultsOfTask( user, pass, taskCode, email, GateTaskCH.NAME, "");        
        
        synchronized(AuthorizationManager.getLocker(email)){
            TaskFileManager.registerTaskFinished(taskCodeFolder);
        }
        
        // Set the task as finish
        setFinished(true);
    } 
    
    
    /**
     * Create a set of files of speeches data, we create a spreadsheet file with 
     * jOpenDocument in the destination folder given, and json file with indicators
     * by categories and speeches.
     * 
     * @param document xml document with data extracted with gate (researcher cv data)
     * @param rootPath directory that contains the file generated
     */
    private void createResultFiles(Document document, String rootPath) throws Exception{
                
        List<String> categories = GateConstantCH.getCategoriesCH();
        
        // Read the data extracted and save the data in objects.
        List<Speech> listSpeeches = FormatConversor.gateDocToListSpeeches(document, categories);
        
        // Create Files
        rootPath += File.separator;
        String resultsPath          = rootPath + AuthorizationManager.results_dirname;
        String detailedResultsPath  = rootPath + AuthorizationManager.detailed_results_dirname;
        String middleDataPath       = rootPath + AuthorizationManager.middle_data_dirname;
        
        FormatConversor.gateDocToXml(middleDataPath, this.getXMLResults());
        
        FileGeneratorCSV.createCsvDataExtracted(resultsPath, listSpeeches, categories);
        
        FileGeneratorJSON.createIndicatorsByCategory(resultsPath, listSpeeches, categories);
        FileGeneratorJSON.createIndicatorsBySpeeches(resultsPath, listSpeeches, categories);
        
        FileGeneratorJSON.createDetailedResult(detailedResultsPath, listSpeeches, categories);
    }
}
