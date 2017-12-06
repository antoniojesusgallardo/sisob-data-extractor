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
package eu.sisob.uma.api.prototypetextmining.globals;

import java.util.ArrayList;
import java.util.List;

public class DataExchangeLiterals
{
        public static final String MIDDLE_ELEMENT_XML_ID_ENTITY_ATT = "id_entity";
    public static final String MIDDLE_ELEMENT_XML_ID_TEXTMININGPARSER_ATT = "id_textminingparser";
    public static final String MIDDLE_ELEMENT_XML_ID_ANNOTATIONRECOLLECTING = "id_annotationrecollecting";

    public static final String ID_TEXTMININGPARSER_GATEGUESSER = "GATEGUESSER";
    public static final String ID_TEXTMININGPARSER_GATERESEARCHER = "GATERESEARCHER";
    public static final String ID_TEXTMININGPARSER_GATE_CULTURAL_HERITAGE = "GATE_CULTURAL_HERITAGE";

    public static final String ID_TEXTMININGPARSER_GATEGUESSER_DEFAULTANNREC = "default";
    public static final String ID_TEXTMININGPARSER_GATERESEARCHER_DEFAULTANNREC = "default";
    public static final String ID_TEXTMININGPARSER_GATERESEARCHER_DEFAULTANNREC_1 = "default_1";    
    public static final String ID_ANNOTATION_RECOLLECTING_CULTURAL_HERITAGE  = "CULTURAL_HERITAGE"; 
    
    public static final String MIDDLE_ELEMENT_XML_EXTRADATA_INITIALS = "INITIALS";
    public static final String MIDDLE_ELEMENT_XML_EXTRADATA_LASTNAME = "LASTNAME";
    public static final String MIDDLE_ELEMENT_XML_EXTRADATA_NAME = "NAME";
    public static final String MIDDLE_ELEMENT_XML_EXTRADATA_FIRSTNAME = "FIRSTNAME";
    public static final String MIDDLE_ELEMENT_XML_EXTRADATA_BLOCK_TYPE = "BLOCK_TYPE";
    public static final String MIDDLE_ELEMENT_XML_EXTRADATA_DOCUMENT_NAME = "DOCUMENT_NAME";    
    
    /**
     * Format to the extraData attribute, in the class MiddleData in the task: 
     * European Parliament - Cultural Heritage.
     */
    public static class MiddleData_ExtraDataCH{
        public static final String DATE             = "date";
        public static final String AGENDA_ITEM_NR   = "agendaItemNr";
        public static final String AGENDA_ITEM_TITLE= "agendaItemTitle";
        public static final String SPEECH_NR        = "speechNr";
        public static final String COUNTRY          = "country";
        public static final String URI              = "sessionDayUri";
        
        public static List<String> getFields(){
            List<String> rList = new ArrayList<String>();
            rList.add(DATE);
            rList.add(AGENDA_ITEM_NR);
            rList.add(AGENDA_ITEM_TITLE);
            rList.add(SPEECH_NR);
            rList.add(COUNTRY);
            rList.add(URI);

            return rList;
        }
    }
}      
