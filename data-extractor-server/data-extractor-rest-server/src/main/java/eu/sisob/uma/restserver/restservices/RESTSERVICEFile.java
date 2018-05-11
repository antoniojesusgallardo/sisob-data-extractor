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
package eu.sisob.uma.restserver.restservices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.sisob.uma.restserver.managers.TaskManager;
import eu.sisob.uma.restserver.TheResourceBundle;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.restservices.exceptions.UnAuthorizedException;
import eu.sisob.uma.restserver.restservices.security.AuthenticationUtils;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import eu.sisob.uma.restserver.services.communications.OutputUploadFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/file")
public class RESTSERVICEFile {
    
    private static final Logger LOG = Logger.getLogger(RESTSERVICEFile.class.getName());
    
    @Context 
    HttpHeaders headers;
    
    @GET
    @RolesAllowed("user")
    @Path("/show")
    public Response showFile(@QueryParam("task_code") String task_code, 
                            @QueryParam("file") String file, 
                            @QueryParam("type") String type)  
    {   
        Response response;
        
        String user = AuthenticationUtils.getCurrentUser(headers);
        
        try {
            
            //Security
            if(file.contains("\\") || file.contains("/")){
                throw new Exception();
            }

            File f = TaskFileManager.getFile(user, task_code, file, type);
            
            if (!f.exists()) {
                throw new InternalServerErrorException("File not found");
            }
            
            response =  Response.ok(new FileInputStream(f))
                                .header("Content-Type","text/html; charset=utf-8")
                                .build();
            
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "FIXME", ex);
            throw new InternalServerErrorException();
        }        
        
        return response;
    }
        
    @GET
    @RolesAllowed("user")
    @Path("/download")
    public Response getFile(@QueryParam("task_code") String task_code, 
                            @QueryParam("file") String file, 
                            @QueryParam("type") String type)  
    {        
        Response response;
        
        String user = AuthenticationUtils.getCurrentUser(headers);
        
        try {
            //Security
            if(file.contains("\\") || file.contains("/")){
                throw new Exception();
            }
            
            File f = TaskFileManager.getFile(user, task_code, file, type);
            
            if (!f.exists()) {
                throw new InternalServerErrorException("File not found");
            }
        
            response =  Response.ok(f)
                        .header("Content-Disposition", "attachment; filename=\"" + f.getName() + "\"")
                        .build();           
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "FIXME", ex);
            throw new InternalServerErrorException();
        }
        // Alternative http://www.mkyong.com/webservices/jax-rs/download-pdf-file-from-jax-rs/
        // http://stackoverflow.com/questions/7106775/how-to-download-large-files-without-memory-issues-in-java
        
        return response;
    }
    
    @GET
    @RolesAllowed("user")
    @Path("/delete")    
    public String deleteFile(@QueryParam("token") String token,
                            @QueryParam("task_code") String task_code, 
                            @QueryParam("file") String file)  
    {
        String user = AuthenticationUtils.getCurrentUser(headers);
        
        try {
            
            synchronized(user){

                //Security
                if(file.contains("\\") || file.contains("/")){
                    throw new UnAuthorizedException("Unautorized access");
                }
                
                File f = TaskFileManager.getFile(user, task_code, file, "");
                
                if(f.exists()){
                    f.delete();
                }
                else{
                    throw new Exception("File not found.");
                }
                
                return "File deleted successfully";
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }            
    
    @POST
    @RolesAllowed("user")
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String uploadFile(@FormDataParam("task_code") String task_code,            
                            @FormDataParam("files[]") InputStream uploadedInputStream,
                            @FormDataParam("files[]") FormDataContentDisposition fileDetail ) 
    {
        String user = AuthenticationUtils.getCurrentUser(headers);
        
        try{ 
            
            if(task_code==null || uploadedInputStream==null || fileDetail==null){
                
                Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                            .entity(TheResourceBundle.getString("Upload Fail"))
                                            .build();
                throw new WebApplicationException(response);
            }
            
            //Security
            String fileName = fileDetail.getFileName();
            if(fileName.contains("\\") || fileName.contains("/")){
                throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
            }
            
            OutputTaskStatus task = TaskManager.getTask(user, task_code, false);
        
            if(!OutputTaskStatus.TASK_STATUS_TO_EXECUTE.equals(task.getStatus())){
                Response response = Response.status(Response.Status.PRECONDITION_FAILED)
                                            .entity(task.getMessage())
                                            .build();
                throw new WebApplicationException(response);
            }

            File file = TaskFileManager.getFile(user, task_code, fileName, "");
            
            FileUtils.copyInputStreamToFile(uploadedInputStream, file);
            
            OutputUploadFile outputUploadFile = new OutputUploadFile();
            outputUploadFile.setName(fileName);
            outputUploadFile.setSize(String.valueOf(file.length()));
            
            OutputUploadFile[] arrayOutputUploadFile = {outputUploadFile};
            
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            
            String jsonTextPlain = gson.toJson(arrayOutputUploadFile);
            
            return jsonTextPlain;
        } 
        catch (WebApplicationException ex) {
            throw ex;
        }
        catch (Exception ex){
            LOG.log(Level.SEVERE, "Error uploading file", ex);
            throw new InternalServerErrorException(TheResourceBundle.getString("Upload Fail"));
        }
    }
}