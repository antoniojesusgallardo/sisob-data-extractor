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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.sisob.uma.euParliament.beans.IndicatorsByCategory;
import eu.sisob.uma.euParliament.beans.IndicatorsBySpeech;
import eu.sisob.uma.euParliament.beans.Keyword;
import eu.sisob.uma.euParliament.beans.Speech;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Generator of JSON files, as a result/output of the task.
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class FileGeneratorJSON {
    
    private static final Logger LOG = Logger.getLogger(FileGeneratorJSON.class.getName());
    
    private final static String JSON_DATA                    = "data";
    private final static String JSON_CATEGORIES              = "categories";
    
    
    /**
     * Create a JSON file that contains indicators related to all speeches by categories.
     * The indicators are: 
     *  - Number of speech that contains this keyword
     *  - Number of keywords appearanced, the keywords can be repeat 
     *  - Number of distinct keywords appearanced, the keywords can NOT be repeat 
     * 
     * @param rootPath
     * @param listSpeeches
     * @param categories 
     */
    public static void createIndicatorsByCategory( String rootPath, 
                                                    List<Speech> listSpeeches, 
                                                    List<String> categories) {
        
        /* JSON FORMAT
        json: Object
            categories: Array[5]
                0: "European_Regions_Identities"
                1: "European_International_Ties"
                2: "European_Common_Identity"
                3: "Youth"
                4: "Education"            
            data: Array[5]
                0: Object
                    name: "Education"
                    numKeywords: 3
                    numDistintKeywords: 1
                    numSpeech: 3
                    keywords: Array[n]
                        0: Object
                            value: "culture"
                            speech: Object
                                id: "1999-07-20_1_1"
                                country: "Italy"
                                date: "1999-07-20"
                                textURI: "..."
                        ...
                ...
        */
        
        try {
            
            // Inicialize a map, with an indicator for each category.
            Map<String, IndicatorsByCategory> mapIndicatorsCategory = new TreeMap<String, IndicatorsByCategory>();
            for (String category : categories) {
                IndicatorsByCategory indCategory = new IndicatorsByCategory();
                indCategory.setName(category);
                mapIndicatorsCategory.put(category, indCategory);
            }

            // For each speech and keyword, update the indicator by category.
            for (Speech speech : listSpeeches) {
                for (Keyword keyword : speech.getKeywords()) {

                    String strCategory  = keyword.getCategory();
                    String strKeyword   = keyword.getKeyword();
                    
                    IndicatorsByCategory indicatorCategory =  mapIndicatorsCategory.get(strCategory);
                    
                    // Add Keywor and speech data
                    indicatorCategory.addKeyword(strKeyword,
                                                speech.getId(), 
                                                speech.getDate(), 
                                                speech.getCountryName(), 
                                                speech.getTextURI());
                }
            }
            
            // Calculate the indicators
            for (IndicatorsByCategory iIndCat : mapIndicatorsCategory.values()) {
                iIndCat.calculateIndicators();
            }

            Map rMapJson = new HashMap();
            rMapJson.put(JSON_DATA, mapIndicatorsCategory.values());
            rMapJson.put(JSON_CATEGORIES, categories);

            // Write JSON File
            writeJsonFile(rMapJson, rootPath, FileName.JSON_INDICATORS_BY_CATEGORY);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE,ex.getMessage(), ex);
        }
    }
    
    
    
    /**
     * Create a JSON file that contains all data related to the Speech. 
     * 
     * @param rootPath
     * @param listSpeeches
     * @param categories 
     */
    public static void createIndicatorsBySpeeches( String rootPath, 
                                                    List<Speech> listSpeeches, 
                                                    List<String> categories) {
        /*
        json: Object
            categories: Array[5]
                0: "European_Regions_Identities"
                1: "European_International_Ties"
                2: "European_Common_Identity"
                3: "Youth"
                4: "Education"
            data: Array[n]
                0: Object
                    id: "1999-07-20_1_1"
                    date: "1999-07-20"
                    country: "Italy"
                    textURI: "..."
                    categories: Object
                        Education: Object
                            name: "Education"
                            keywords: Object
                                culture: "culture"
                                ...    
                        ...
                ....
        */
        try {
         
            List<IndicatorsBySpeech> rSpeeches = new ArrayList<IndicatorsBySpeech>();

            for (Speech speech : listSpeeches) {
                
                // Transform SpeechData to IndicatorsBySpeech
                IndicatorsBySpeech indicatorSpeech = new IndicatorsBySpeech( speech.getId(), 
                                                                        speech.getDate(), 
                                                                        speech.getCountryName(), 
                                                                        speech.getTextURI());

                // Add all keywords in IndicatorsBySpeech grouped by category
                for (Keyword keywordData : speech.getKeywords()) {

                    String category = keywordData.getCategory();
                    String keyword = keywordData.getKeyword();

                    indicatorSpeech.saveCategoryAndKeyword(category, keyword);
                }

                rSpeeches.add(indicatorSpeech);
            }

            Map rMapJson = new HashMap();
            rMapJson.put(JSON_DATA, rSpeeches);
            rMapJson.put(JSON_CATEGORIES, categories);

            // Write JSON File
            writeJsonFile(rMapJson, rootPath, FileName.JSON_INDICATORS_BY_SPEECH);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE,ex.getMessage(), ex);
        }
    }
    
        
    /**
     * Create a JSON file with the relevant data.
     * @param rootPath
     * @param listSpeeches
     * @param categories 
     */
    public static void createDetailedResult( String rootPath, 
                                                List<Speech> listSpeeches, 
                                                List<String> categories){
        /* JSON FORMAT
        json: Object
            categories: Array[5]
                0: "European_Regions_Identities"
                1: "European_International_Ties"
                2: "European_Common_Identity"
                3: "Youth"
                4: "Education"  
            data: Object
            	id: "2009-01-12_17_154"
            	text: "-I would like to .. "
            	date: "2009-01-12"
                agendaItemNr: "17"
                speechNr: "154"
                agendaItemTitle: "Trade and econo.."
                countryName: "Romania"
                textURI: "http://www.europarl.."
                keywords: Array[n]
                    0: Object
                        category: "Education"
                        keyword: "culture"
                    ...
        */
        File jsonFile = null;
        try {
            
            for (Speech iSpeech : listSpeeches) {
                
                Map rMapJson = new HashMap();
                rMapJson.put(JSON_DATA, iSpeech);
                rMapJson.put(JSON_CATEGORIES, categories);

                // Transform object to Json.
                Gson gson = new Gson();
                String jsonContent = gson.toJson(rMapJson);

                // Create JSON file
                String fileName = iSpeech.getId() + ".json";
                jsonFile = new File(rootPath, fileName);
                FileUtils.write(jsonFile, jsonContent, "UTF-8");
            }
        } 
        catch (IOException ex) {
            String path = "";
            if (jsonFile != null) {
                path = jsonFile.getPath();
            }
            LOG.log(Level.SEVERE, "The results detailed file can not be created " + path, ex);
        }
        catch(Exception ex){
            LOG.log(Level.SEVERE, "Error writing results detailed. " + ex.toString(), ex);
        }  
    }
    

    /**
     * Auxiliary method to write a JSON file, with encoding UTF-8.
     * 
     * @param data
     * @param dest
     * @param fileName 
     */
    private static void writeJsonFile(Object data, String rootPath, String fileName) {
        
        try {
            
            GsonBuilder gsonBuilder = new GsonBuilder();  
            gsonBuilder.serializeNulls(); 
            
            Gson gson = gsonBuilder.create();
            String jsonContent = gson.toJson(data);

            File jsonFile = new File(rootPath, fileName);
            FileUtils.write(jsonFile, jsonContent, "UTF-8");
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE,ex.getMessage(), ex);
        }
    }   
}
