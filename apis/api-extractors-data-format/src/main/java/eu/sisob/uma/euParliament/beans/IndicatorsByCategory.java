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
package eu.sisob.uma.euParliament.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Indicators related to Cultural Heritage category
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class IndicatorsByCategory {
    
    // -------------------------------------------------------------------------
    // ---------------------------- Data Fields
    // -------------------------------------------------------------------------
    // Category Name
    private String name;
    
    // Map keywords by value of the keyword
    private List<Keyword> keywords = new ArrayList<Keyword>();
    
    // -------------------------------------------------------------------------
    // ---------------------------- Indicator Fields
    // -------------------------------------------------------------------------
    // Number of keywords appearanced, the keywords can be repeat 
    private Integer numKeywords;
    
    // Number of distint keywords appearanced, the keywords can NOT be repeat 
    private Integer numDistintKeywords;
    
    // Number of speech that contains this keyword
    private Integer numSpeech;

    private static class Keyword {
        private String value;
        private Speech speech;
        
        static class Speech {
            private String id;
            private String date;
            private String country;
            private String textURI;
        }
    }
    
    
    public void addKeyword( String pKeyword,
                            String id, 
                            String date, 
                            String country, 
                            String textURI){
        
        Keyword keyword = new Keyword();
        keyword.value = pKeyword;
        
        keyword.speech          = new Keyword.Speech();
        keyword.speech.id       = id;
        keyword.speech.date     = date;
        keyword.speech.country  = country;
        keyword.speech.textURI  = textURI;
        
        keywords.add(keyword);
    }
    
    public void calculateIndicators(){
        numKeywords         = calculateNumKeywords();
        numDistintKeywords  = calculateNumDistintKeywords();
        numSpeech           = calculateNumSpeech();
    }
    
    private Integer calculateNumKeywords() {
        Integer rValue = keywords.size();
        return rValue;
    }

    private Integer calculateNumDistintKeywords(){
        
        Map<String, String> mapKeywords = new HashMap<String, String>();
        for (Keyword iKeyword : keywords) {
            mapKeywords.put(iKeyword.value, iKeyword.value);
        }
        
        Integer rValue = mapKeywords.size();
        return rValue;
    }

    private Integer calculateNumSpeech() {
        
        Map<String, String> mapSpeeches = new HashMap<String, String>();
        for (Keyword iKeyword : keywords) {
            mapSpeeches.put(iKeyword.speech.id, iKeyword.speech.id);
        }
        
        Integer rValue = mapSpeeches.size();
        return rValue;
    }
    
    // GET AND SET 
    public void setName(String name) {
        this.name = name;
    }
}
