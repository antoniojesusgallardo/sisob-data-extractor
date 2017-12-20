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

package eu.sisob.uma.euParliament;

import au.com.bytecode.opencsv.CSVWriter;
import eu.sisob.uma.euParliament.beans.Keyword;
import eu.sisob.uma.euParliament.beans.Speech;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * Generator of CSV files, as a result/output of the task.
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class FileGeneratorCSV {
    
    private static final Logger LOG = Logger.getLogger(FileGeneratorCSV.class.getName());
    
    
    /**
     * Create a CSV file with all speeches filtered with the keywords.
     * @param rootPath
     * @param listSpeeches
     * @param categories 
     */
    public static void createCsvDataExtracted( String rootPath, 
                                                List<Speech> listSpeeches, 
                                                List<String> categories){
        /* CSV columns:
            - SPEECH_ID
            - DATE
            - COUNTRYNAME
            - TEXTURI
            - KEYWORDS
            - KEYWORDS_CATEGORY 1
            - KEYWORDS_CATEGORY 2
            ... */
        
        try {
            
            String filePath = rootPath + File.separator + FileName.CSV_DATA;
            
            // Write CSV File
            CSVWriter writer = new CSVWriter(new FileWriter(filePath), ',');
            
            // Create the first row, the header.
            List<String> listHeader = new ArrayList<String>();
            listHeader.add(FileFormat.OutputCSV.ID);
            listHeader.add(FileFormat.OutputCSV.DATE);
            listHeader.add(FileFormat.OutputCSV.COUNTRY);
            listHeader.add(FileFormat.OutputCSV.SESSION_DAY_URI);
            
            for (String category : categories) {
                // Create a column for each category
                String strCategoryHeader = category.toUpperCase();
                strCategoryHeader = strCategoryHeader.replaceAll(" ", "_");
                String strHeaderCategory = FileFormat.OutputCSV.KEYWORD_ROOT + strCategoryHeader;
                listHeader.add(strHeaderCategory);
            }
            writer.writeNext(listHeader.toArray(new String[0]));
            
            String SEPARATOR = " - ";
            
            Long count = new Long(1);
            // Create a row for each speech 
            for (Speech speech : listSpeeches) {
                
                // Inicialize a map, with keywords by category
                Map<String,String> mapKeywordsByCategory = new HashMap<String, String>();
                for (String category : categories) {
                    mapKeywordsByCategory.put(category, "");
                }
                
                // Add the keywords in the category.
                for (Keyword keywordData : speech.getKeywords()) {
                    
                    if (categories.contains(keywordData.getCategory())) {
                        String content = mapKeywordsByCategory.get(keywordData.getCategory());
                        
                        content += keywordData.getKeyword() + SEPARATOR;
                        
                        mapKeywordsByCategory.put(keywordData.getCategory(), content);
                    }
                }
                
                // Remove the separator if the separator is the last character.
                for (String category : categories) {
                    String content = mapKeywordsByCategory.get(category);
                    content = StringUtils.removeEnd(content, SEPARATOR);

                    mapKeywordsByCategory.put(category, content);
                }
                
                List<String> listRowData = new ArrayList<String>();
                listRowData.add(speech.getId());
                listRowData.add(speech.getDate());
                listRowData.add(speech.getCountryName());
                listRowData.add(speech.getTextURI());
                
                for (String category : categories) {
                    String content = mapKeywordsByCategory.get(category);
                    listRowData.add(content);
                }
                
                writer.writeNext(listRowData.toArray(new String[0]));
                
                count++;
            }
            writer.close();
        } 
        catch (Exception ex) {
            LOG.log(Level.SEVERE,ex.getMessage(), ex);
        }
    }      
}
