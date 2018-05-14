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
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.restservices.security.AuthenticationUtils;
import eu.sisob.uma.restserver.services.communications.Task;
import eu.sisob.uma.restserver.services.communications.FileDetail;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/file")
public class RESTSERVICEFile extends RESTSERVICEBase{
    
    public static final String FILE_TYPE_SOURCE             = "source";
    public static final String FILE_TYPE_VERBOSE            = "verbose";
    public static final String FILE_TYPE_RESULT             = "result";
    public static final String FILE_TYPE_DETAILED_RESULT    = "detailed-result";
    
    @GET
    @RolesAllowed("user")
    public Response getFile(@QueryParam("task_code") String task_code, 
                            @QueryParam("file") String file, 
                            @QueryParam("type") String type)  
    {        
        Response response;
        
        String user = AuthenticationUtils.getUser(token);
        
        try {
            validateRequired(task_code, "Task Code");
            validateRequired(file, "File");
            validateRequired(type, "File Type");
            
            validateFileType(type);
            
            String fileType = getFileType(type);
            
            //Security
            if(file.contains("\\") || file.contains("/")){
                throw new WebApplicationException("Incorrect file name.", Response.Status.BAD_REQUEST);
            }
            
            final File f = TaskFileManager.getFile(user, task_code, file, fileType);
            
            if (!f.exists()) {
                throw new WebApplicationException("File not found", Response.Status.NOT_FOUND);
            }
            
            StreamingOutput fileStream =  new StreamingOutput() 
            {
                @Override
                public void write(java.io.OutputStream output) throws IOException, WebApplicationException 
                {
                    try {
                        java.nio.file.Path path = Paths.get(f.getAbsolutePath());
                        byte[] data = Files.readAllBytes(path);
                        output.write(data);
                        output.flush();
                    } 
                    catch (Exception e){
                        throw new WebApplicationException("File not found", Response.Status.NOT_FOUND);
                    }
                }
            };
        
            response =  Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "attachment; filename='"+f.getName()+"'")
                        .build();
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            throw new InternalServerErrorException();
        }
        
        return response;
    }
    
    @DELETE
    @RolesAllowed("user")
    public String deleteFile(@QueryParam("task_code") String task_code, 
                            @QueryParam("file") String file)  
    {
        String user = AuthenticationUtils.getUser(token);
        
        try {
            
            validateRequired(task_code, "Task Code");
            validateRequired(file, "File");
            
            synchronized(user){

                //Security
                if(file.contains("\\") || file.contains("/")){
                    throw new WebApplicationException("Incorrect file name.", Response.Status.BAD_REQUEST);
                }
                
                // Only delete the input data.
                File f = TaskFileManager.getFile(user, task_code, file, "");
                
                if (!f.exists()) {
                    throw new WebApplicationException("File not found.", Response.Status.NOT_FOUND);
                }
                
                f.delete();
                
                return "File deleted successfully";
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            throw new InternalServerErrorException(ex.getMessage());
        }
    }            
    
    @POST
    @RolesAllowed("user")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String uploadFile(@FormDataParam("task_code") String task_code,            
                            @FormDataParam("files[]") InputStream uploadedInputStream,
                            @FormDataParam("files[]") FormDataContentDisposition fileDetails ) 
    {
        String user = AuthenticationUtils.getUser(token);
        
        try{ 
            validateRequired(task_code, "Task Code");
            
            if(uploadedInputStream==null || fileDetails==null){
                throw new WebApplicationException(TheResourceBundle.getString("Upload Fail"), Response.Status.INTERNAL_SERVER_ERROR);
            }
            
            //Security
            String fileName = fileDetails.getFileName();
            if(fileName.contains("\\") || fileName.contains("/")){
                throw new WebApplicationException("Incorrect file name.", Response.Status.BAD_REQUEST);
            }
            
            Task task = TaskManager.getTask(user, task_code, false);
        
            if(!Task.STATUS_TO_EXECUTE.equals(task.getStatus())){
                throw new WebApplicationException(task.getMessage(), Response.Status.PRECONDITION_FAILED);
            }

            File file = TaskFileManager.getFile(user, task_code, fileName, "");
            
            FileUtils.copyInputStreamToFile(uploadedInputStream, file);
            
            FileDetail fileDetail = new FileDetail();
            fileDetail.setName(fileName);
            fileDetail.setSize(String.valueOf(file.length()));
            
            FileDetail[] arrayFileDetail = {fileDetail};
            
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            
            String jsonTextPlain = gson.toJson(arrayFileDetail);
            
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
    
    private void validateFileType(String fileType){
        
        List<String> fileTypes = new ArrayList();
        fileTypes.add(FILE_TYPE_SOURCE);
        fileTypes.add(FILE_TYPE_VERBOSE);
        fileTypes.add(FILE_TYPE_RESULT);
        fileTypes.add(FILE_TYPE_DETAILED_RESULT);
        
        if (!fileTypes.contains(fileType)) {
            Response response = Response.status(Response.Status.BAD_REQUEST)
                                        .entity("Wrong File Type.")
                                        .type(MediaType.TEXT_PLAIN)
                                        .build();
            throw new WebApplicationException(response);
        }
    }

    private String getFileType(String fileType){
        
        String rFileType = "";
        
        if (null != fileType) switch (fileType) {
            case FILE_TYPE_SOURCE:
                rFileType = null;
                break;
            case FILE_TYPE_VERBOSE:
                rFileType = AuthorizationManager.verbose_dirname;
                break;
            case FILE_TYPE_RESULT:
                rFileType = AuthorizationManager.results_dirname;
                break;
            case FILE_TYPE_DETAILED_RESULT:
                rFileType = AuthorizationManager.detailed_results_dirname;
                break;
        }
        
        return rFileType;
    }
}