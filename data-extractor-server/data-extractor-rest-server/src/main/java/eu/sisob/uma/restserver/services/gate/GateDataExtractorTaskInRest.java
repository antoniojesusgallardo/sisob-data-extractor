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
package eu.sisob.uma.restserver.services.gate;

import eu.sisob.uma.npl.researchers.CVBlocks;
import eu.sisob.uma.npl.researchers.data.ViewCreator_CSVandSheets;
import eu.sisob.uma.npl.researchers.GateDataExtractorTask;
import eu.sisob.uma.api.h2dbpool.H2DBCredentials;
import eu.sisob.uma.api.prototypetextmining.MiddleData;
import eu.sisob.uma.api.prototypetextmining.RepositoryPreprocessDataMiddleData;
import eu.sisob.uma.api.prototypetextmining.globals.DataExchangeLiterals;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.managers.FeedbackManager;
import eu.sisob.uma.restserver.Mailer;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import gate.Document;
import gate.Factory;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;


public class GateDataExtractorTaskInRest extends GateDataExtractorTask
{       
    private static final Logger LOG = Logger.getLogger(GateDataExtractorTaskInRest.class.getName());
    
    String user;
    String email;
    String task_code;
    String task_code_folder;
    
    /*
     * @param document - xml document with the information     
     */    
    public GateDataExtractorTaskInRest(RepositoryPreprocessDataMiddleData taskRepPrePro, boolean user_trad_tables, H2DBCredentials cred_trad, boolean use_resolver, H2DBCredentials cred_resolver, String user, String task_code, String task_code_folder, String email)
    {        
        super(taskRepPrePro, user_trad_tables, cred_trad, use_resolver, cred_resolver);        
        this.user = user;
        this.email = email;
        this.task_code = task_code;
        this.task_code_folder = task_code_folder;       
    }           
    
    public void executeTask(){
        
        File results_data_dir = new File(task_code_folder + File.separator + AuthorizationManager.results_dirname);

        for(MiddleData md : this.taskRepPrePro.getMiddleDataList()){
            HashMap<String,String> extra_data = null;
            try{
                extra_data = (HashMap<String,String>) md.getData_extra();
                Integer block_type = Integer.parseInt(extra_data.get(DataExchangeLiterals.MIDDLE_ELEMENT_XML_EXTRADATA_BLOCK_TYPE));
                if(block_type != null){

                    String desc = CVBlocks.CVBLOCK_DESCRIPTIONS[block_type];
                    if(block_type.equals(CVBlocks.CVBLOCK_PUBLICATIONS) ||
                       block_type.equals(CVBlocks.CVBLOCK_PROFESSIONAL_ACTIVITY) ||
                       block_type.equals(CVBlocks.CVBLOCK_UNIVERSITY_STUDIES)){                                

                        String separator = "\n";
                        if(md.getData_in().toString().contains("\r\n"))
                            separator = "\r\n";
                        String data = md.getData_in().toString();
                        String[] lines = null;
                        if(data.startsWith("http") || data.startsWith("file")){
                            URL url = new URL(data);
                            Document doc = Factory.newDocument(url);
                            lines = doc.getContent().toString().split(separator);
                        }else{
                            lines = md.getData_in().toString().split(separator);
                        }

                        for(int i = 0; i < lines.length ; i++){
                            FileUtils.write(new File(results_data_dir, desc + "-lines.csv"), "\"" + md.getId_entity() + "\",\"" + lines[i].replace("\"", "'") + "\"" + "\r\n", true);
                        }
                    }
                }

            } catch(Exception ex){
                extra_data = null;
            }                    
        }
        
        super.executeTask();
    }
    
    /*
     * Callback function to manage the results
     */
    public void executeCallBackOfTask() 
    {   
        String results_path = task_code_folder + File.separator + AuthorizationManager.results_dirname;
        String middle_data_path = task_code_folder + File.separator + AuthorizationManager.middle_data_dirname;
        
        synchronized(AuthorizationManager.getLocker(email))
        {        
            super.executeCallBackOfTask();            

            if(task_code_folder == null)
            {
                LOG.log(Level.SEVERE, "Something was wrong. Temporal directory is not set.");  //FIXME
            }
            else
            {   
                try 
                {   
                    File middle_dir = new File(middle_data_path);
                    if(!middle_dir.exists())
                        middle_dir.mkdir();
                    
                    String output_filepath = middle_dir.getAbsolutePath() + File.separator + GateTask.output_data_extracted_filename_xml;                                        
                    
                    File fField = new File(output_filepath);
                    FileUtils.write(fField, this.xml_doc_results.asXML(), "UTF-8");                    
                    
                    LOG.info("Result writed in " + fField.getPath());
                                                           
                    
                    LOG.info(middle_dir.getAbsolutePath() + File.separator + GateTask.output_data_extracted_filename_xml);  //FIXME
                } 
                catch (Exception ex) 
                {
                    LOG.log(Level.SEVERE, "Error creating results (" + task_code_folder + ")", ex); //FIXME
                    TaskFileManager.notifyResultError(this.email, this.task_code, "Error creating results.");
                }
            }        
        }
        
        File results_dir = new File(results_path);
        if(!results_dir.exists())
            results_dir.mkdir();
                    
        //Generate view of results
        ViewCreator_CSVandSheets.createViewFilesFromDataExtracted(this.xml_doc_results, results_dir, true, true);
        
        
        
        //Generate feedback
        String feedback_filename = user + "-gate-feedback-task-" + task_code;
        String feedback_message = "";
        String feedback_url = FeedbackManager.createNewFeedBackDoc(user, task_code, feedback_filename);
        if(!feedback_url.equals(""))
        {
            GateDataExtractorFeedBack.initializeFeedback(user, task_code, feedback_filename);    
            TaskFileManager.updateFeedbackFile(user, task_code, feedback_url);
            feedback_message = "You can give us the feedback using this document shared with you: " + feedback_url;
        }
        else
        {
            feedback_message = "The feedback document cant be generated by you. Ask to this email for that. Thank you very much.";
            TaskFileManager.notifyResultError(this.user, this.task_code, "Error creating feedback using google docs.");
        }        
        
        Mailer.notifyResultsOfTask(user, task_code, email, "gate", feedback_message);        
        
        synchronized(AuthorizationManager.getLocker(email)){
            TaskFileManager.registerTaskFinished(this.task_code_folder);
        }
            
        setFinished(true);
    }   
    
}

