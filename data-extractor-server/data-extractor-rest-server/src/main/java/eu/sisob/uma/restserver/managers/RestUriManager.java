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

import eu.sisob.uma.restserver.TheConfig;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class RestUriManager {
    
    
    /**
     * Return the url to show the file in browser
     * @param user 
     * @param pass 
     * @param task_code
     * @param type 
     * @param file_name 
     * @return
     */
    public static String getUriFileToShow(String user, String pass, 
                                            String task_code, String file_name, 
                                            String type){
        
        String url = getUri(user, pass, task_code, file_name, type, "show");
        return url;
    } 
    
    /**
     * Return the url to donwload the file in browser
     * @param user 
     * @param pass 
     * @param task_code
     * @param type 
     * @param file_name 
     * @return
     */
    public static String getUriFileToDownload(String user, String pass, 
                                        String task_code, String file_name, 
                                        String type){
        
        String url = getUri(user, pass, task_code, file_name, type, "download");
        return url;
    }  
    
    /**
     * Return the url to delete the file in the task folder
     * @param user 
     * @param pass 
     * @param task_code
     * @param type 
     * @param file_name 
     * @return
     */
    public static String getUriFileToDelete(String user, String pass, 
                                            String task_code, String file_name, 
                                            String type){
        
        String url = getUri(user, pass, task_code, file_name, type, "delete");
        return url;
    }
    
    private static String getUri(String user, String pass, 
                                String task_code, String file_name, 
                                String type, String operation){
        
        String url = TheConfig.getInstance().getString(TheConfig.SERVER_URL) + 
                        "/resources/file/" + 
                        operation + 
                        "?user=" + user + 
                        "&pass=" + pass + 
                        "&task_code=" + task_code + 
                        "&file=" + file_name + 
                        "&type=" + type;
        return url;
    }
    
    
    public static String getUriRoot(String user, String pass, String taskCode, String method){
    
        String rUri = TheConfig.getInstance().getString(TheConfig.SERVER_URL) +
                        "/resources/file/" +
                        method +
                        "?user=" + user + 
                        "&pass=" + pass + 
                        "&task_code=" + taskCode + 
                        "&file=";
        return rUri;
    }
}
