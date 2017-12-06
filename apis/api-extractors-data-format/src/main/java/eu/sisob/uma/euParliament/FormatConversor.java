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

import eu.sisob.uma.euParliament.beans.Keyword;
import eu.sisob.uma.euParliament.beans.Speech;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;


/**
 * Format conversor used in the task: European Parliament - Cultural Heritage
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class FormatConversor {
    
    private static final Logger log = Logger.getLogger(FormatConversor.class.getName());
    
    
    /**
     * Convert the GATE document to list of Speech object.
     * @param document
     * @param categories
     * @return 
     */
    public static List<Speech> gateDocToListSpeeches(Document document, List<String> categories){
        
        /*
        Document Format:
        
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
        
        List<Speech> listSpeeches = new ArrayList<Speech>();
        Element root = document.getRootElement();
        // Processs Document - Each blockinfo contains a speech data.
        for ( Iterator i = root.elementIterator(FileFormat.XML.Root.getTagName()); i.hasNext(); ){   
            Element elBlockInfo = (Element) i.next();   
            
            String speechId         = elBlockInfo.attributeValue(FileFormat.XML.Root.SPEECH_ID);
            String text             = elBlockInfo.attributeValue(FileFormat.XML.Root.TEXT);
            String date             = elBlockInfo.attributeValue(FileFormat.XML.Root.DATE);            
            String country          = elBlockInfo.attributeValue(FileFormat.XML.Root.COUNTRY);            
            String textUri          = elBlockInfo.attributeValue(FileFormat.XML.Root.URI);
            String agendaItemNr     = elBlockInfo.attributeValue(FileFormat.XML.Root.AGENDA_ITEM_NR);            
            String agendaItemTitle  = elBlockInfo.attributeValue(FileFormat.XML.Root.AGENDA_ITEM_TITLE);            
            String speechNr         = elBlockInfo.attributeValue(FileFormat.XML.Root.SPEECH_NR);

            Speech newSpeech = new Speech();
            newSpeech.setId(speechId);
            newSpeech.setText(text);
            newSpeech.setDate(date);
            newSpeech.setCountryName(country);
            newSpeech.setTextURI(textUri);
            newSpeech.setAgendaItemNr(agendaItemNr);
            newSpeech.setAgendaItemTitle(agendaItemTitle);
            newSpeech.setSpeechNr(speechNr);
            
            // Processs annotation: CULTURAL_HERITAGE_ONT
            for(Object oKeyword : elBlockInfo.elements()) {
                
                Element element = (Element)oKeyword;
                
                if(FileFormat.XML.Keyword_CH.getClassName().equals(element.getName())){
                    
                    String category = element.attributeValue(FileFormat.XML.Keyword_CH.CATEGORY);
                    String keyword  = element.attributeValue(FileFormat.XML.Keyword_CH.VALUE);

                    if (categories.contains(category)){
                        Keyword newKeyword = new Keyword(keyword, category);
                        newSpeech.addKeyword(newKeyword);
                    } 
                }
                
            }// END - Processs annotation: CULTURAL_HERITAGE_ONT
            
            // Add the speech, if the speech contains keywords
            if (!newSpeech.getKeywords().isEmpty()) {
                listSpeeches.add(newSpeech);
            }
        }
        // END - Processs Document
        
        return listSpeeches;
    }

    /**
     * Generate a xml file with the GATE Document.
     * @param rootPath
     * @param document
     * @throws Exception 
     */
    public static void gateDocToXml(String rootPath, Document document) throws Exception{
        String xmlPath = "";
        try {
            xmlPath = Paths.get(rootPath, FileName.MIDDLE_DATA_XML_FILE).toString();

            File fField = new File(xmlPath);
            FileUtils.write(fField, document.asXML(), "UTF-8");
        }
        catch (IOException ex) {
            log.log(Level.SEVERE, "Error creating " + xmlPath, ex);  //FIXME                
            throw new Exception(ex);
        }
    } 
}
