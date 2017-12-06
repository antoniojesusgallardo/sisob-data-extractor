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

import java.util.Map;
import java.util.TreeMap;

/**
 * Speech with categories of Cultural Heritage 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class IndicatorsBySpeech {
    
    private final String id;
    private final String date;
    private final String country;
    private final String textURI;
    
    private final  Map<String, Category> categories = new TreeMap<String, Category>();
    
    public static class Category
    {
        private final String name;
        private final Map<String, String> keywords = new TreeMap<String, String>();
        
        public Category(String name)
        {
            this.name = name;
        }

        private void addKeyword(String pKeyword){
            keywords.put(pKeyword, pKeyword);
        }
    }
    

    public IndicatorsBySpeech(String id, String date, String country, String textURI){
        this.id = id;
        this.date = date;
        this.country = country;
        this.textURI = textURI;
    }
    
    public void saveCategoryAndKeyword(String pCategory, String pKeyword){
        
        Category oCategory = putCategory(pCategory);
        
        oCategory.addKeyword(pKeyword);
    }
    
    private Category putCategory(String pName){
        
        if (!categories.containsKey(pName)) {
            categories.put(pName, new Category(pName));
        }
        
        Category rSpeechOntology = categories.get(pName);
        
        return rSpeechOntology;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCountry() {
        return country;
    }

    public String getTextURI() {
        return textURI;
    }   
}
