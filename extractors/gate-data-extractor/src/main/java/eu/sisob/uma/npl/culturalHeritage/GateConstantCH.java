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

package eu.sisob.uma.npl.culturalHeritage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with the Constants used in the Gate - CulturalHeritage.
 * 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class GateConstantCH {
    
    // Values used in the Gate resources.
    
    // File plugins/annie/resources/gazetteer/cultural_heritage/gaz_cultural_heritage.def
    static final String MAJOR_TYPE_CULTURAL_HERITAGE         = "Cultural_Heritage";
    private final static String CATEGORY_EUROPEAN_REGIONS_IDENTITIES= "European_Regions_Identities";
    private final static String CATEGORY_EUROPEAN_INTERNATIONAL_TIES= "European_International_Ties";
    private final static String CATEGORY_EUROPEAN_COMMON_IDENTITY   = "European_Common_Identity";
    private final static String CATEGORY_YOUTH                      = "Youth";
    private final static String CATEGORY_EDUCATION                  = "Education";
    
    
    /**
     * Function that returns all Cultural Heritage categories.
     * @return 
     */
    public static List<String> getCategoriesCH(){
        
        List<String> categories = new ArrayList<String>();
        
        categories.add(CATEGORY_EUROPEAN_REGIONS_IDENTITIES);
        categories.add(CATEGORY_EUROPEAN_INTERNATIONAL_TIES);
        categories.add(CATEGORY_EUROPEAN_COMMON_IDENTITY);
        categories.add(CATEGORY_YOUTH);
        categories.add(CATEGORY_EDUCATION);
        
        return categories;
    }
}
