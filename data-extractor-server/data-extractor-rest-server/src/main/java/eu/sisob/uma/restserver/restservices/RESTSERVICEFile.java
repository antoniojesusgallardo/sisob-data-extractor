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
import eu.sisob.uma.restserver.managers.RestUriManager;
import eu.sisob.uma.restserver.managers.TaskManager;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import eu.sisob.uma.restserver.TheResourceBundle;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.restservices.exceptions.UnAuthorizedException;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import eu.sisob.uma.restserver.services.communications.OutputUploadFile;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.FileUtils;

@Path("/file")
public class RESTSERVICEFile {
    
    private static final Logger LOG = Logger.getLogger(RESTSERVICEFile.class.getName());
    
    @GET
    @Path("/show")
    public Response showFile(@QueryParam("user") String user, 
                            @QueryParam("pass") String pass, 
                            @QueryParam("task_code") String task_code, 
                            @QueryParam("file") String file, 
                            @QueryParam("type") String type)  
    {   
        Response response;
        try {
            RESTSERVICEUtils.validateAccess(user, pass);
            
            //Security
            if(file.contains("\\") || file.contains("/")){
                throw new Exception();
            }

            if (type == null) {
                type = "";
            }
            
            File f = TaskFileManager.getFile(user, task_code, file, type);
            
            byte[] docStream = FileUtils.readFileToByteArray(f);
            response = Response
                    .ok(docStream, MediaType.APPLICATION_OCTET_STREAM)
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
    @Path("/download")
    public Response getFile(@QueryParam("user") String user, 
                            @QueryParam("pass") String pass, 
                            @QueryParam("task_code") String task_code, 
                            @QueryParam("file") String file, 
                            @QueryParam("type") String type)  
    {        
        Response response;
        
        try {
            RESTSERVICEUtils.validateAccess(user, pass);
            
            //Security
            if(file.contains("\\") || file.contains("/")){
                throw new Exception();
            }
            
            if (type == null) {
                type = "";
            }
            
            File f = TaskFileManager.getFile(user, task_code, file, type);
        
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
    @Path("/delete")    
    public String deleteFile(@QueryParam("user") String user, 
                                @QueryParam("pass") String pass, 
                                @QueryParam("task_code") String task_code, 
                                @QueryParam("file") String file)  
    {
        try {
            synchronized(user){

                RESTSERVICEUtils.validateAccess(user, pass);

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
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String uploadFile(@FormDataParam("user") String user,
                            @FormDataParam("pass") String pass,
                            @FormDataParam("task_code") String task_code,            
                            @FormDataParam("files[]") InputStream uploadedInputStream,
                            @FormDataParam("files[]") FormDataContentDisposition fileDetail ) 
    {
        try{ 
            
            if( user==null || pass==null || task_code==null || 
                uploadedInputStream==null || fileDetail==null){
                
                Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                            .entity(TheResourceBundle.getString("Upload Fail"))
                                            .build();
                throw new WebApplicationException(response);
            }
            
            RESTSERVICEUtils.validateAccess(user, pass);

            //Security
            if(fileDetail.getFileName().contains("\\") || fileDetail.getFileName().contains("/")){
                throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
            }
            
            OutputTaskStatus task = TaskManager.getTask(user, pass, task_code, false);
        
            if(!OutputTaskStatus.TASK_STATUS_TO_EXECUTE.equals(task.getStatus())){
                Response response = Response.status(Response.Status.PRECONDITION_FAILED)
                                            .entity(task.getMessage())
                                            .build();
                throw new WebApplicationException(response);
            }

            File file = TaskFileManager.getFile(user, task_code, fileDetail.getFileName(), "");
            
            FileUtils.copyInputStreamToFile(uploadedInputStream, file);
            
            String url = RestUriManager.getUriFileToDownload(user, pass, task_code, fileDetail.getFileName(), "");
            String deleteUrl = RestUriManager.getUriFileToDelete(user, pass, task_code, fileDetail.getFileName(), "");
            
            OutputUploadFile outputUploadFile = new OutputUploadFile();
            outputUploadFile.setName(fileDetail.getFileName());
            outputUploadFile.setSize(String.valueOf(file.length()));
            outputUploadFile.setUrl(url);
            outputUploadFile.setThumbnail_url("");
            outputUploadFile.setDelete_url(deleteUrl);
            outputUploadFile.setDelete_type("GET");
            
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