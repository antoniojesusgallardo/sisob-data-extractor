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
package eu.sisob.uma.restserver.services.websearchers;

import eu.sisob.uma.extractors.adhoc.websearchers_cv.WebSearchersCVExtractorTask;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.Mailer;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import java.io.File;
import java.util.logging.Logger;


public class WebSearcherExtractorCVTaskInRest extends WebSearchersCVExtractorTask
{
    private static final Logger LOG = Logger.getLogger(WebSearcherExtractorCVTaskInRest.class.getName());
    
    String user;    
    String email;    
    String task_code;
    String task_code_folder;         
    
    /*
     * @param document - xml document with the information     
     */    
    public WebSearcherExtractorCVTaskInRest(String user, String task_code, String task_code_folder, String email, File csv_reseacher_input_file, File csv_reseacher_output_file, File csv_researchers_output_file_unfounded, File folder_results_path, File zip_output_file, File output_file_2)
    {   
        super(csv_reseacher_input_file, csv_reseacher_output_file, csv_researchers_output_file_unfounded, folder_results_path, zip_output_file, output_file_2);
        
        this.user = user;
        this.email = email;
        this.task_code = task_code;
        this.task_code_folder = task_code_folder;  
        
    }          
    
    /*
     * Notify in email to the user and create the end_flag
     */
    @Override
    public void executeCallBackOfTask() 
    {    
        if(this.error_sw != null){
            if(!this.error_sw.toString().equals("")){
                TaskFileManager.notifyResultError(this.user, this.task_code, error_sw.toString());
            }
        }
        Mailer.notifyResultsOfTask(user, task_code, email, "websearcher_cv", "This kind of task has not feedback document associated, please report any problem using this email.");  
        
        synchronized(AuthorizationManager.getLocker(user)){
            TaskFileManager.registerTaskFinished(this.task_code_folder);
        }
            
        super.executeCallBackOfTask();
    }      
}

