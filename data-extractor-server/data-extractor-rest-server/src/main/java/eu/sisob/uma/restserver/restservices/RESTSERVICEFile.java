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

import eu.sisob.uma.restserver.managers.RestUriManager;
import eu.sisob.uma.restserver.managers.TaskManager;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import eu.sisob.uma.restserver.TheResourceBundle;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.restservices.exceptions.UnAuthorizedException;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
            
            byte[] docStream = org.apache.commons.io.FileUtils.readFileToByteArray(f);
            response = Response
                    .ok(docStream, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Type","text/html; charset=utf-8")
                    .build();

        } catch (UnAuthorizedException | InternalServerErrorException ex) {
            throw ex;
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "FIXME", ex);  
            throw new WebApplicationException(Response.Status.NOT_FOUND);           
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
        } catch (UnAuthorizedException | InternalServerErrorException ex) {
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
        } catch (UnAuthorizedException | InternalServerErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }            
    
    
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile( @FormDataParam("user") String user,
                                @FormDataParam("pass") String pass,
                                @FormDataParam("task_code") String task_code,            
                                @FormDataParam("files[]") InputStream uploadedInputStream,
                                @FormDataParam("files[]") FormDataContentDisposition fileDetail ) 
    {
        Response response = null;
        
        OutputStream out = null;
        String pathFile = "";
        
        try{ 
            
            if( user==null || pass==null || task_code==null || 
                uploadedInputStream==null || fileDetail==null){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(TheResourceBundle.getString("Upload Fail"))
                                .build();
            }
            
            RESTSERVICEUtils.validateAccess(user, pass);

            //Security
            if(fileDetail.getFileName().contains("\\") || fileDetail.getFileName().contains("/")){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .build();
            }
            
            OutputTaskStatus task = TaskManager.getTask(user, pass, task_code, false);
        
            if(!OutputTaskStatus.TASK_STATUS_TO_EXECUTE.equals(task.getStatus())){
                return Response.status(Response.Status.PRECONDITION_FAILED)
                                .entity(task.getMessage())
                                .build();
            }

            File file = TaskFileManager.getFile(user, task_code, fileDetail.getFileName(), "");
            pathFile = file.getAbsolutePath();
            
            int read;
            long size = 0;
            byte[] bytes = new byte[1024];
            out = new FileOutputStream(file);            
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                size += read;
                out.write(bytes, 0, read);
            }
            
            JSONObject jsono = new JSONObject();
            jsono.put("name", fileDetail.getFileName());
            jsono.put("size", size);
            jsono.put("url", RestUriManager.getUriFileToDownload(user, pass, task_code, fileDetail.getFileName(), ""));
            jsono.put("thumbnail_url", "");
            jsono.put("delete_url", RestUriManager.getUriFileToDelete(user, pass, task_code, fileDetail.getFileName(), ""));
            jsono.put("delete_type", "GET");
            
            JSONArray json = new JSONArray();
            json.put(jsono);

            response = Response.ok(json.toString()).build();
        } 
        catch (UnAuthorizedException | InternalServerErrorException ex) {
            throw ex;
        }                 
        catch (IOException e){
            LOG.log(Level.SEVERE, "Error uploading file ({0}): {1}", new Object[]{pathFile, e.getMessage()});
            throw new InternalServerErrorException(TheResourceBundle.getString("Upload Fail"));
        }     
        catch (JSONException ex){
            LOG.log(Level.SEVERE, "Error generatin json ({0}): {1}", new Object[]{pathFile, ex.getMessage()});
            throw new InternalServerErrorException(TheResourceBundle.getString("Upload Fail"));
        } 
        catch (Exception ex){
            LOG.log(Level.SEVERE, "Error generatin json ({0}): {1}",  ex);
            throw new InternalServerErrorException(TheResourceBundle.getString("Upload Fail"));
        }
        finally{
            try{
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
            catch (IOException e){
                LOG.log(Level.SEVERE, "Error closing file ({0}): {1}", new Object[]{pathFile, e.getMessage()});
                throw new InternalServerErrorException(TheResourceBundle.getString("Upload Fail"));
            }
        }          
        return response;
    }
}