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
import eu.sisob.uma.restserver.TheResourceBundle;
import eu.sisob.uma.restserver.beans.UserAttributes;
import eu.sisob.uma.restserver.beans.AuthorizationResult;
import java.util.HashMap;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TASK DESCRIPCION
 * 
 * Location of task physically:
 * 
 *   TASKS_FOLDER_NAME + TASKS_USERS_PATH + user + task_number
 * 
 *   Task folder:
 *   ..
 * 
 *   task_data_file         => File that contains the task data 
 *                              - status
 *                              - kind
 *                              - date-created
 *                              - date-started
 *                              - date-finished
 * 
 *   params.flag            => File that contains the task params
 * 
 *   results_dirname        => Dir. that contains the files resulted of the task
 *      ..
 *      feedback_flag_file  => File that contains the feedback of the task given from the user
 *      error_flag_file     => File that contains the errors that appeared during the execution
 * 
 *   detailed_results_dirname => Dir. that contains the results detailed in files.
 * 
 *   middle_data_dirname    => Dir. that contains the files generated in the task process
 * 
 *   verbose_dirname        => Dir. that contains the files generated in verbose mode        
 *   
 */
public class AuthorizationManager 
{
    private static final Logger LOG = Logger.getLogger(AuthorizationManager.class.getName());
    
    static final String TASKS_USERS_PATH;
    public static final String TASKS_FOLDER_NAME = "sisob-tasks";
    
    public static final String task_data_file = "task-data.properties";
    public static final String params_flag_file = "params.flag";
    
    public static final String results_dirname = "results";
    public static final String feedback_flag_file = "feedback.flag";
    public static final String error_flag_file = "error.flag";
    
    public static final String detailed_results_dirname = "detailed_results";
    public static final String middle_data_dirname = "middle_data";
    public static final String verbose_dirname = "verbose";
    
    public static final int MAX_TASKS_PER_USER = 5;
    
    private static final HashMap<String, Object> FILE_LOCKERS = new HashMap();
    
    static
    {
        String pathTaskFolder = Paths.get(TheConfig.getProperty("server.docs.folder"), 
                                            TASKS_FOLDER_NAME).toString();
        File dir = new File(pathTaskFolder);
        if(!dir.exists()){
            dir.mkdir();
        }         
        
        TASKS_USERS_PATH = pathTaskFolder;
        
        dir = new File(pathTaskFolder + File.separator + "test-code");
        if(!dir.exists()){ 
            dir.mkdir();
        }        
    }
    
    /**
     * 
     * @param code
     * @return
     */
    public static synchronized Object getLocker(String code)
    {
        if(!FILE_LOCKERS.containsKey(code))
            FILE_LOCKERS.put(code, new Object());
        
        return FILE_LOCKERS.get(code);            
    }
    
    /**
     * 
     * @param user
     * @param pass
     * @return 
     */
    public static AuthorizationResult validateAccess(String user, String pass){
        
        AuthorizationResult result = new AuthorizationResult();
        
        if(!SystemManager.getInstance().IsRunning()){
            result.setSuccess(Boolean.FALSE);
            result.setMessage("The back system is off. Please contact with the administrator.");
            return result;
        }
        
        if (user == null || pass == null) {
            result.setSuccess(Boolean.FALSE);
            result.setMessage(TheResourceBundle.getString("Jsp Params Invalid Msg"));
            return result;
        }
        
        if(user.contains("'") || pass.contains("'")) { 
            result.setSuccess(Boolean.FALSE);
            result.setMessage("Please, insert a valid username and password");
            return result;
        }  
        
        UserAttributes userDB = DBAuthorizeUserIn(user, pass);
        if(userDB!=null){
            result.setAccountType(userDB.getAccountType());
            result.setNumTasksAllow(userDB.getNTasksAllow());
            
            File f = new File(TaskFileManager.getUserFolder(user));
            if(!f.exists()){
                f.mkdir();
            }

            result.setSuccess(Boolean.TRUE);
            result.setMessage(TheResourceBundle.getString("Jsp Auth Msg"));
        }
        else{
            result.setSuccess(Boolean.FALSE);
            result.setMessage(TheResourceBundle.getString("Jsp Unauth Msg"));
        }
        
        return result;
    }
    
    private static UserAttributes DBAuthorizeUserIn(String user, String pass){
        
        UserAttributes rUser = null;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        String query =  "SELECT "+
                        "`user_tasks_allow` as n_tasks_allow, `user_type` as account_type "+
                        "FROM USERS "+
                        "WHERE user_email = ? and user_pass = ?";
            
        try {
            conn = SystemManager.getInstance().getSystemDbPool().getConnection();
            statement = conn.prepareStatement(query);
            statement.setString(1, user);
            statement.setString(2, pass);            
            
            rs = statement.executeQuery();
            if(rs.next()){
                rUser = new UserAttributes();
                rUser.setAccountType(rs.getString("account_type"));
                rUser.setNTasksAllow((Integer) rs.getInt("n_tasks_allow"));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            if(rs != null){ 
                try {
                    rs.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                }
            }
            if(statement != null){ 
                try {                        
                    statement.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                }
            }
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage());
                }
            }
        }
        return rUser;
    }
}