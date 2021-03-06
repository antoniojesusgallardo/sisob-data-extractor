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
package eu.sisob.uma.restserver.services.email;

import eu.sisob.uma.api.concurrent.threadpoolutils.CallbackableTaskExecution;
import eu.sisob.uma.crawlerWorks.webpagesofuniversities.Format.FileFormatConversor;
import eu.sisob.uma.extractors.adhoc.email.EmailExtractorService;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.managers.FileSystemManager;
import eu.sisob.uma.restserver.TheResourceBundle;
import eu.sisob.uma.footils.File.ZipUtil;
import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EmailTask 
{
    private static final Logger LOG = Logger.getLogger(EmailTask.class.getName());
    
    public final static String input_data_source_filename_prefix_csv = "data-researchers-documents-urls";         
    public final static String input_data_source_filename_ext_csv = ".csv";   
    
    public final static String input_data_documents_in_zip = "documents.zip";
    
    public final static String output_data_source_norepeat_filename_prefix_csv = "norepeat.data-researchers-documents-urls-emails";     
    public final static String output_data_source_norepeat_ext_csv = ".csv";       
    
    public final static String output_data_source_filename_prefix_csv = "data-researchers-documents-urls-emails";     
    public final static String output_data_source_filename_ext_csv = ".csv";   
    
    public final static String output_data_source_nofound_filename_prefix_csv = "notfound.data-researchers-documents-urls";     
    public final static String output_data_source_nofound_filename_ext_csv = ".csv";       
    
    public final static String output_data_source_nofound_norepeat_filename_prefix_csv = "notfound.norepeat.data-researchers-documents-urls-emails";     
    public final static String output_data_source_nofound_norepeat_ext_csv = ".csv";   
    
    //TODO, solve parameters issue, in web side we dont know wich is wich param
    public final static String PARAM_VERBOSE = "verbose";
    public final static String PARAM_VERBOSE_TRUE = "true";
    public final static String PARAM_VERBOSE_FALSE = "false";
    
    public static boolean launch(String user, String task_code, String code_task_folder, String email, List<String> filters, StringWriter message)
    {   
        if(message == null)
        {
            return false;
        }
        
        message.getBuffer().setLength(0);
        File code_task_folder_dir = new File(code_task_folder);
        
        File documents_dir = code_task_folder_dir;        
        
        File csv_data_source_file = FileSystemManager.getFileIfExists(code_task_folder_dir, input_data_source_filename_prefix_csv, input_data_source_filename_ext_csv);        
        boolean validate = csv_data_source_file != null;      
        
        if(!validate) {            
            message.write("You have not uploaded any file like this '" + input_data_source_filename_prefix_csv + "*" + input_data_source_filename_ext_csv +"' file");
            return false;
        }
        
        try {                    
             validate = FileFormatConversor.checkResearchersCSV(csv_data_source_file, true);
        } catch(Exception ex) {              
            LOG.log(Level.WARNING, message.toString(), ex);
            validate = false;
        }
            
        if(!validate) {            
            message.write("The format of '" + csv_data_source_file.getName() + "' does not seems be correct"); //FIXME
            return false;
        }
            
        String middle_data_folder = code_task_folder + File.separator + AuthorizationManager.middle_data_dirname;
        File middle_data_dir = null;
        try {
            middle_data_dir = FileSystemManager.createFileAndIfExistsDelete(middle_data_folder);
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);           
            message.append("The file couldn't be created " + middle_data_dir.getName() + "\r\n");
            return false;
        }  

        String results_data_folder = code_task_folder + File.separator + AuthorizationManager.results_dirname;
        File results_data_dir = null;
        try {
            results_data_dir = FileSystemManager.createFileAndIfExistsDelete(results_data_folder);
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
            message.append("The file couldn't be created " + results_data_dir.getName() + "\r\n");
            return false;
        }  

        File zip_file = new File(code_task_folder_dir, input_data_documents_in_zip);                        

        if(zip_file.exists())
        {
            documents_dir = new File(code_task_folder_dir, AuthorizationManager.middle_data_dirname);
            if(!ZipUtil.unZipItToSameFolder(zip_file, documents_dir))                
            {
                message.write(input_data_documents_in_zip + " cannot bet unziped"); //FIXME
                return false;
            }
        }

        
        String out_filename = csv_data_source_file.getName().replace(input_data_source_filename_prefix_csv, output_data_source_filename_prefix_csv);                
        File csv_data_output_file = new File(results_data_dir, out_filename);            

        String out_filename_unfounded = csv_data_source_file.getName().replace(input_data_source_filename_prefix_csv, output_data_source_nofound_filename_prefix_csv);
        File csv_data_output_file_unfounded = new File(results_data_dir, out_filename_unfounded);         
        
        String out_filename_wor = csv_data_source_file.getName().replace(input_data_source_filename_prefix_csv, output_data_source_norepeat_filename_prefix_csv);
        File csv_data_output_file_wor = new File(results_data_dir, out_filename_wor);       
        
        String out_filename_wor_notfound = csv_data_source_file.getName().replace(input_data_source_filename_prefix_csv, output_data_source_nofound_norepeat_filename_prefix_csv);
        File csv_data_output_file_notfound_wor = new File(results_data_dir, out_filename_wor_notfound);       

        EmailExtractorTaskInRest task = new EmailExtractorTaskInRest(user, task_code, code_task_folder, email, csv_data_source_file, documents_dir, csv_data_output_file, csv_data_output_file_wor, csv_data_output_file_unfounded, csv_data_output_file_notfound_wor, filters);
        
        try 
        {
            EmailExtractorService.getInstance().addExecution((new CallbackableTaskExecution(task)));
            
            message.write(TheResourceBundle.getString("Jsp Task Executed Msg"));
        } 
        catch (Exception ex) 
        {
            message.write(TheResourceBundle.getString("Jsp Task Executed Error Msg"));
            LOG.log(Level.SEVERE, message.toString(), ex);
            return false;
        }   
        
        return true;
    }
}
