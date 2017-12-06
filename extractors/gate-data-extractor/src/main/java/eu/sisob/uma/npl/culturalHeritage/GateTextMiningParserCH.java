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

import gate.*;
import gate.creole.SerialAnalyserController;
import java.util.TreeMap;
import eu.sisob.uma.api.prototypetextmining.AnnotatorCollector;
import eu.sisob.uma.api.prototypetextmining.RepositoryPreprocessDataMiddleData;
import eu.sisob.uma.api.prototypetextmining.RepositoryProcessedDataXML;
import eu.sisob.uma.api.prototypetextmining.gatedataextractor.TextMiningParserGate;
import eu.sisob.uma.api.prototypetextmining.globals.DataExchangeLiterals;
import eu.sisob.uma.npl.GateConstant;
import gate.util.GateException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class GateTextMiningParserCH extends TextMiningParserGate
{   
    private static final Logger log = Logger.getLogger(GateTextMiningParserCH.class.getName());
    
    /**
     *
     * @param nInfoblocks
     */
    public GateTextMiningParserCH(int nInfoblocks) {
        super(DataExchangeLiterals.ID_TEXTMININGPARSER_GATE_CULTURAL_HERITAGE, 
                new RepositoryProcessedDataXML(), 
                nInfoblocks, 
                new RepositoryPreprocessDataMiddleData(), false);
    }
    
    /**
     * Initialise the ANNIE system by default. This creates a "corpus pipeline"
     * application that can be used to run sets of documents through
     * the extraction system.
     * Especific for extract info from CV and personal web page of researchers
     * @throws Exception
     */
    @Override
    public void iniActions() throws Exception {       
        
        TextMiningParserGate.iniMutex.acquire();
        log.info(String.format("Load TextMiningParserGateCH (%s)", Gate.genSym()));
        try {
            
            String pathGateResources= Paths.get(Gate.getPluginsHome().getAbsolutePath(),
                                                GateConstant.DIRECTORY_PLUGIN_ANNIE, 
                                                "resources").toString();
            Resource res;
            
            // create a serial analyser controller to run ANNIE with
            res = Factory.createResource("gate.creole.SerialAnalyserController", 
                                            Factory.newFeatureMap(),
                                            Factory.newFeatureMap(), 
                                            "ANNIE_" + Gate.genSym() );
            annieController = (SerialAnalyserController) res;
                        
            // Delete all annotations to reset the document.
            res = Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR");
            annieController.add((ProcessingResource)res);

            // Break the document into tokens
            res = Factory.createResource("gate.creole.tokeniser.DefaultTokeniser");
            annieController.add((ProcessingResource)res);
                                    
            // Create resource HashGazetteer with the keywors 
            String pathListDefCH = Paths.get(pathGateResources, 
                                            "gazetteer", 
                                            "cultural_heritage", 
                                            "gaz_cultural_heritage.def").toString();
            FeatureMap featuresCH = Factory.newFeatureMap();
            featuresCH.put("listsURL", pathListDefCH); 
            res = Factory.createResource("com.ontotext.gate.gazetteer.HashGazetteer", featuresCH);
            annieController.add((ProcessingResource) res); 
            
        } catch (GateException ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }
        finally {
            log.info("Load SerialAnalyserController done.");
            TextMiningParserGate.iniMutex.release();
        }
    }    

   /**
    * Define annotator collector acoording index
    * I_TYPE_CONTENT_ENTIRE_WEB_PAGE => Extract info from CV and personal web page of researchers
    * @param lstAnnColl_ list of annotator collector
    */
    @Override
    protected void iniAnnotatorCollectors(TreeMap lstAnnColl_){
        
        AnnotatorCollector a = new GateAnnCollectorCH(DataExchangeLiterals.ID_ANNOTATION_RECOLLECTING_CULTURAL_HERITAGE);
        lstAnnColl_.put(a.type, a); 
    }
}
