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
package eu.sisob.uma.restserver.services.communications;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Task 
{
    
    public static final String STATUS_TO_EXECUTE = "TO EXECUTE";
    
    public static final String STATUS_EXECUTING = "EXECUTING";
    
    public static final String STATUS_EXECUTED = "EXECUTED";
    
    
    public Task()
    {
        status = "";      
        message = "";
        task_code = "";   
        name = "";   
        kind = "";

        date_created = "";
        date_started = "";
        date_finished = "";

        results = new ArrayList();      
        source = new ArrayList();
        verbose = new ArrayList();
        params = new ArrayList(); 
        errors = "";

        feedback = "";   
    }
    
    
    private String status;      
    
    private String message;
    
    private String task_code;   
    
    private String name;   
    
    private String kind;
    
    private List<TaskParameter> params;

    
    private String date_created;
    
    private String date_started;
    
    private String date_finished;

    
    private List<String> results;
    
    private List<String> source;  
    
    private List<String> verbose;  
    
    private String errors;
    
    private String feedback;   

    
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTask_code() {
        return task_code;
    }

    public void setTask_code(String task_code) {
        this.task_code = task_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<TaskParameter> getParams() {
        return params;
    }

    public void setParams(List<TaskParameter> params) {
        this.params = params;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
    
    public String getDate_started() {
        return date_started;
    }

    public void setDate_started(String date_started) {
        this.date_started = date_started;
    }

    public String getDate_finished() {
        return date_finished;
    }

    public void setDate_finished(String date_finished) {
        this.date_finished = date_finished;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public List<String> getSource() {
        return source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<String> getVerbose() {
        return verbose;
    }

    public void setVerbose(List<String> verbose) {
        this.verbose = verbose;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
    
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}