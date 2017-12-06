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

import java.util.ArrayList;
import java.util.List;

/**
 * File format used in the task: European Parliament - Cultural Heritage
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class FileFormat {
    
    /**
     * Input file in CSV format.
     */
    public static class InputCSV{
        
        public static final String DATE              = "DATE";
        public static final String AGENDA_ITEM_NR    = "AGENDA_ITEM_NR";
        public static final String AGENDA_ITEM_TITLE = "AGENDA_ITEM_TITLE";
        public static final String SPEECH_NR         = "SPEECH_NR";
        public static final String TEXT              = "SPEECH_TEXT";
        public static final String COUNTRY_NAME      = "COUNTRY";
        public static final String TEXT_URI          = "SESSION_DAY_URI";

        public static List<String> getColumns_CsvEp(){

            List<String> rList = new ArrayList<String>();

            rList.add(DATE);
            rList.add(AGENDA_ITEM_NR);
            rList.add(AGENDA_ITEM_TITLE);
            rList.add(SPEECH_NR);
            rList.add(TEXT);
            rList.add(COUNTRY_NAME);
            rList.add(TEXT_URI);

            return rList;
        }
    }
    
    /**
     * Output file in CSV format.
     */
    public static class OutputCSV{
        public final static String ID              = "ID";
        public final static String SPEECH_ID       = "SPEECH_ID";
        public final static String DATE            = "DATE";
        public final static String COUNTRY         = "COUNTRY";
        public final static String SESSION_DAY_URI = "SESSION_DAY_URI";
        public final static String KEYWORD_ROOT    = "KEYWORDS_";
    }
    
    /**
     * Middle data in XML format, XML file is XML results of a GATE document.
     */
    public static class XML{
        
        /*  XML Format:

            <blockinfo 
                speechId=..
                text=..
                date=..
                agendaItemNr=..
                agendaItemTitle=..
                speechNr=..
                country=..
                sessionDayUri=.. >
                <Keyword_CH category="" value=""/>
                <Keyword_CH category="" value=""/>
                ...
            </blockinfo>
        */
        
        public static class Root{
            public static final String SPEECH_ID       = "speechId";
            public static final String TEXT            = "text";
            public static final String DATE            = "date";
            public static final String AGENDA_ITEM_NR  = "agendaItemNr";
            public static final String AGENDA_ITEM_TITLE= "agendaItemTitle";
            public static final String SPEECH_NR       = "speechNr";
            public static final String COUNTRY         = "country";
            public static final String URI             = "sessionDayUri";

            public static final List<Keyword_CH> keywords = null;
            public static String getTagName(){
                return "blockinfo";
            }
        }
    
        public static class Keyword_CH{
            public static final String CATEGORY   = "category";
            public static final String VALUE      = "value";

            public static String getClassName(){
                return Keyword_CH.class.getSimpleName();
            }
        }
    }
}
