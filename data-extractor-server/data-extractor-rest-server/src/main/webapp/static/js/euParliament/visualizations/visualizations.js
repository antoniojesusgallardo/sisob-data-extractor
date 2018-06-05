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

/*
 * Author: Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
var visualizationsCH = (function(){

    return {
        dataOntologies: null, 
        dataSpeeches: null,
        taskCode: null,
        init: function (taskCode) {
            
            visualizationsCH.dataOntologies = null;
            visualizationsCH.dataSpeeches = null;
            visualizationsCH.taskCode = taskCode;
            
            var urlBase =   "resources/file" +
                            "?task_code=" + taskCode + 
                            "&type=result" +
                            "&file=";  
            var urlIndByCategory= urlBase + "IndicatorsByCategory.json";
            var urlIndBySpeech  = urlBase + "IndicatorsBySpeech.json";

            d3.json(urlIndByCategory)
                .header(security.AUTHORIZATION_PROPERTY, security.getToken())
                .get(function(error, data) {
                
                if(error != null){
                    console.log(error.responseText);
                    return;
                }
                            
                visualizationsCH.dataOntologies = data; 
                keywordsEvolutionCH.draw(visualizationsCH.dataOntologies);
            });
            
            d3.json(urlIndBySpeech)
                .header(security.AUTHORIZATION_PROPERTY, security.getToken())
                .get(function(error, data) {
                
                if(error != null){
                    console.log(error.responseText);
                    return;
                }
                
                visualizationsCH.dataSpeeches = data; 
                keywordsEvolutionCH.draw(visualizationsCH.dataOntologies);
            });
        },
        
        changeSelector: function (){

            var selected = Util.getSelect_value('visualization-selector');
    
            if(selected === "generalIndicators"){
                generalIndicatorsCH.draw(visualizationsCH.dataOntologies);
            }
            else if (selected === "wordCloud"){
                wordCloudCH.draw(visualizationsCH.dataOntologies, visualizationsCH.taskCode);
            }
            else if(selected === "temporalEvolution"){
                keywordsEvolutionCH.draw(visualizationsCH.dataOntologies);
            }
            else if(selected === "speechesByCountries"){
                speechesByCountriesCH.draw(visualizationsCH.dataSpeeches);
            }
        }        
    };
}());    
