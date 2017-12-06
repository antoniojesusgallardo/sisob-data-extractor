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

import eu.sisob.uma.api.prototypetextmining.AnnotatorCollector;
import eu.sisob.uma.api.prototypetextmining.MiddleData;
import eu.sisob.uma.api.prototypetextmining.globals.DataExchangeLiterals;
import eu.sisob.uma.euParliament.FileFormat;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.FeatureMap;
import java.util.HashMap;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

/**
 * Annotator Collector used with: Gate - Cultural Heritage Ontologies.
 * 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class GateAnnCollectorCH extends AnnotatorCollector {
    
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(GateAnnCollectorCH.class.getName());
    
    /**
     *
     * @param type
     */
    public GateAnnCollectorCH(String type){
        super(type);
    }
    
    /**
     * Method that collect the info extracted and save in MiddleData object, 
     * and create a JSON file if we recover info.
     * 
     * @param doc
     * @param aoData
     */
    @Override
    public void collect(Object doc, MiddleData aoData){
        
        Document docGate = (Document) doc;  
        AnnotationSet anns = docGate.getAnnotations().get("Lookup"); // Specify the expression to collect
        String textContent = docGate.toXml(anns, true);
        
        // Create a XML element to the speech, with all the speech attributes.
        Element elRoot = createRootNode(aoData, textContent);
        aoData.setData_out(elRoot);
        
        // Create a XML element for each annotation recovered                     
        for(Annotation an : anns){
            
            FeatureMap featuresMap = an.getFeatures();
            
            if (!featuresMap.containsKey("majorType") || 
                !featuresMap.containsKey("minorType") ) {
                continue;
            }
            
            String majorType = featuresMap.get("majorType").toString();
            String minorType = featuresMap.get("minorType").toString();

            if (GateConstantCH.MAJOR_TYPE_CULTURAL_HERITAGE.equals(majorType) &&
                GateConstantCH.getCategoriesCH().contains(minorType)){
                
                Element elKeyword = createElementKeyword(docGate, an, minorType);
                elRoot.add(elKeyword);
            }            
        }
         
        LOG.info(String.format("%3d expressions in %s : ", elRoot.elements().size(), 
                                                           docGate.getName()));
    }
    
    /**
     * Create a root node with the speech data
     * @param middleData
     * @param text
     * @return 
     */
    private Element createRootNode(MiddleData middleData, String text){
        
        String rootName = FileFormat.XML.Root.getTagName();
        
        Element elRoot = DocumentFactory.getInstance().createElement(rootName);
        elRoot.addAttribute(FileFormat.XML.Root.SPEECH_ID, middleData.getId_entity());
        elRoot.addAttribute(FileFormat.XML.Root.TEXT, text);
        
        HashMap<String,String> extraData = (HashMap<String,String>) middleData.getData_extra();
        
        for (String iExtraData : DataExchangeLiterals.MiddleData_ExtraDataCH.getFields()) {
            elRoot.addAttribute(iExtraData, extraData.get(iExtraData));
        }
        
        return elRoot;
    }
    
    /**
     * Create a keyword node with the text and the category
     * @param docGate
     * @param an
     * @param minorType
     * @return 
     */
    private Element createElementKeyword(Document docGate, Annotation an, String minorType){
        
        String text = gate.Utils.stringFor(docGate, an);
        String elementName = FileFormat.XML.Keyword_CH.getClassName();
        
        Element rElement = new DocumentFactory().createElement(elementName);
        rElement.addAttribute(FileFormat.XML.Keyword_CH.CATEGORY, minorType);
        rElement.addAttribute(FileFormat.XML.Keyword_CH.VALUE, text);
        
        return rElement;
    }
}

    
