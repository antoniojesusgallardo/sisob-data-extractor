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
package eu.sisob.uma.restserver.services.gateCH;

import au.com.bytecode.opencsv.CSVReader;
import eu.sisob.uma.api.concurrent.threadpoolutils.CallbackableTaskExecutionWithResource;
import eu.sisob.uma.api.prototypetextmining.MiddleData;
import eu.sisob.uma.api.prototypetextmining.RepositoryPreprocessDataMiddleData;
import eu.sisob.uma.api.prototypetextmining.globals.DataExchangeLiterals;
import eu.sisob.uma.euParliament.FileFormat;
import eu.sisob.uma.restserver.AuthorizationManager;
import eu.sisob.uma.restserver.FileSystemManager;
import eu.sisob.uma.restserver.TheResourceBundle;
import eu.sisob.uma.npl.culturalHeritage.GateDataExtractorServiceCH;
import eu.sisob.uma.restserver.services.communications.OutputTaskOperationResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class GateTaskCH  {
    
    private static final Logger LOG = Logger.getLogger(GateTaskCH.class.getName());
 
    public final static String NAME = "TEXT ANALYZER - European Parliament - Cultural Heritage";
    
    private final static String INPUT_DATA_PREFIX   = "data-speech";         
    private final static String INPUT_DATA_EXT_CSV  = ".csv";   
    
    
     
    public static OutputTaskOperationResult launch(String user, String pass, String taskCode){   
        
        OutputTaskOperationResult rResult = new OutputTaskOperationResult();
        
        try {
            String taskDirectory = Paths.get(AuthorizationManager.TASKS_USERS_PATH, 
                                             user, 
                                             taskCode).toString();
            
            // Validation - fileName
            File csvFile = FileSystemManager.getFileIfExists(new File(taskDirectory), 
                                                             INPUT_DATA_PREFIX, 
                                                             INPUT_DATA_EXT_CSV);
            if (csvFile == null) {
                throw new Exception("You have not uploaded any file like this '"+
                                    INPUT_DATA_PREFIX + "*" + INPUT_DATA_EXT_CSV +"' file");
            }
            
            // Init directories
            List<Path> paths = new ArrayList<Path>();
            paths.add(Paths.get(taskDirectory, AuthorizationManager.middle_data_dirname));
            paths.add(Paths.get(taskDirectory, AuthorizationManager.results_dirname));
            paths.add(Paths.get(taskDirectory, AuthorizationManager.detailed_results_dirname));
            for (Path iPath : paths) {
                FileSystemManager.createFileAndIfExistsDelete(iPath.toString());
            }
            
            // Processing CSV data
            RepositoryPreprocessDataMiddleData preprocessedRep = createPreprocessRepositoryFromCSVFile(csvFile);
            if (preprocessedRep == null) {
                throw new Exception("Format error: " + csvFile.getName());
            }

            // Execute GateService
            GateDataExtractorTaskCH task = new GateDataExtractorTaskCH(preprocessedRep, user, pass, taskCode); 
            GateDataExtractorServiceCH.getInstance().addExecution((new CallbackableTaskExecutionWithResource(task)));
            
            rResult.message = TheResourceBundle.getString("Jsp Task Executed Msg");
            rResult.success = true;
            
        } catch(Exception ex) {
            rResult.message = ex.getLocalizedMessage();
            rResult.success = false;
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }       
        return rResult;
    }
    
    private static RepositoryPreprocessDataMiddleData createPreprocessRepositoryFromCSVFile(File csvFilepath)  throws IOException, Exception {   
        
        // 1. Read the CSV File, 
        char separator  = ','; 
        char quotechar  = '"';
        char escape     = '|';
//        CSVReader reader = new CSVReader(new FileReader(csvFilepath), separator, quotechar , escape);
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(csvFilepath), "UTF-8"),
                                        separator, 
                                        quotechar, 
                                        escape);
        
        // 2. Read the CSV Header and get columns index
        Map<String, Integer> indexesByColumnName = new HashMap<String, Integer>();
        for (String columns_CsvEp : FileFormat.InputCSV.getColumns_CsvEp()) {
            indexesByColumnName.put(columns_CsvEp, -1);
        } 
        String [] csvLine;
        if ((csvLine = reader.readNext()) != null){
            for(int i = 0; i < csvLine.length; i++){
                String columnName = getStringFormatted(csvLine[i]);
                for (String iColumnName : FileFormat.InputCSV.getColumns_CsvEp()) {
                    if (iColumnName.equalsIgnoreCase(columnName)) {
                        indexesByColumnName.put(iColumnName, i);
                    }
                }
            }                
        }   
        
        // 3. If an index is not valid - exit
        for (String column : indexesByColumnName.keySet()) {
            if(indexesByColumnName.get(column) == -1){
                throw new Exception("Format error: the column '"+column+"' is required.");
            }
        }
        
        // 4. Transform CSV file to RepositoryPreprocessDataMiddleData
        RepositoryPreprocessDataMiddleData preprocessedRep = new RepositoryPreprocessDataMiddleData(); 
        Integer count = 0;
        while ((csvLine = reader.readNext()) != null){   

            // Get MiddleData to CSV row
            MiddleData md = processCsvLine(csvLine, indexesByColumnName);
            
            if (md != null) {
                // Add MiddleData to RepositoryPreprocessDataMiddleData
                preprocessedRep.addData(md); 
            }
            count++;
        }
        
        LOG.info(String.format("%s documents added", count));
        
        return preprocessedRep;
    }
    
    private static MiddleData processCsvLine(String [] csvLine, 
                                             Map<String, Integer> indexesByColumnName) 
                                            throws Exception{
        MiddleData md = null;
        try {
            // 4.1. Pre-Validation - current Line
            if (csvLine.length < FileFormat.InputCSV.getColumns_CsvEp().size()) {
                printLineLog(csvLine);
                return null;
            }

            // 4.2. Get Fields
            String date             = getField(csvLine, indexesByColumnName, FileFormat.InputCSV.DATE);
            String agendaitemnr     = getField(csvLine, indexesByColumnName, FileFormat.InputCSV.AGENDA_ITEM_NR);
            String agendaitemtitle  = getField(csvLine, indexesByColumnName, FileFormat.InputCSV.AGENDA_ITEM_TITLE);
            String speechnr         = getField(csvLine, indexesByColumnName, FileFormat.InputCSV.SPEECH_NR);
            String text             = getField(csvLine, indexesByColumnName, FileFormat.InputCSV.TEXT);
            String countryName      = getField(csvLine, indexesByColumnName, FileFormat.InputCSV.COUNTRY_NAME);
            String textUri          = getField(csvLine, indexesByColumnName, FileFormat.InputCSV.TEXT_URI);

                // 4.3. Validation - current Line
            if (date.length() != 10) {
                printLineLog(csvLine);
                return null;
            }

            // 4.4. Create MiddleData
            String id = date + "_" + agendaitemnr + "_" + speechnr.trim();
            
            HashMap<String, String> extraData = new HashMap<String, String>();
            extraData.put(DataExchangeLiterals.MiddleData_ExtraDataCH.DATE, date);
            extraData.put(DataExchangeLiterals.MiddleData_ExtraDataCH.AGENDA_ITEM_NR, agendaitemnr);
            extraData.put(DataExchangeLiterals.MiddleData_ExtraDataCH.AGENDA_ITEM_TITLE, agendaitemtitle);
            extraData.put(DataExchangeLiterals.MiddleData_ExtraDataCH.SPEECH_NR, speechnr);
            extraData.put(DataExchangeLiterals.MiddleData_ExtraDataCH.COUNTRY, countryName);                
            extraData.put(DataExchangeLiterals.MiddleData_ExtraDataCH.URI, textUri); 
            
            md = new MiddleData(id,
                                DataExchangeLiterals.ID_TEXTMININGPARSER_GATE_CULTURAL_HERITAGE, 
                                DataExchangeLiterals.ID_ANNOTATION_RECOLLECTING_CULTURAL_HERITAGE, 
                                text, extraData);

        } catch (Exception e) {
            printLineLog(csvLine);
            throw new IOException(e);
        }
        
        return md;
    }
    
    private static String getField( String[] currentLine, 
                                    Map<String, Integer> mapIndexColumns, 
                                    String columnName){
        Integer index = mapIndexColumns.get(columnName);
        String rString = getStringFormatted(currentLine[index]);
        return rString;
    }
     
    private static String getStringFormatted(String pString){
        String rString = pString;
        
        // Remove ilegal characters
        String[] ilegalChars = {"\"", "“", "”"};
        for (String ilegalChar : ilegalChars) {
            rString = StringUtils.remove(rString, ilegalChar);
        }
        
        // Trim
        rString = StringUtils.trim(rString);
        
        // Remove double/tripel spaces
        rString = StringUtils.normalizeSpace(rString);
        
        // Remove initial '\n' and spaces
        rString = StringUtils.remove(rString, "^([\\s|\\n])+");
        /*while (rString.startsWith("\n") || rString.startsWith(" ")) {            
            rString = rString.replaceFirst("\n", "");
            rString = rString.trim();
        }*/
        
        return rString;
    }
    
    private static void printLineLog(String [] line){
        
        String stringError = "";
        for (int i = 0; i < line.length; i++) {
            String nextLine1 = line[i];
            stringError += nextLine1 + " --- ";
        }
        stringError = "ERROR in CURRENT LINE : " + stringError;
        LOG.info(stringError);
    }
}
