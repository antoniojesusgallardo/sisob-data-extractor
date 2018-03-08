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
var Visualizations = (function(){

    return {
        init: function () {
            
            var urlIndByCategory= urlBaseJson.replace("&file=", "&file=" + "IndicatorsByCategory.json");
            var urlIndBySpeech  = urlBaseJson.replace("&file=", "&file=" + "IndicatorsBySpeech.json");

            d3.json(urlIndByCategory , function(json) {
                jsonData_ontologies = json;  

                drawLineChart(jsonData_ontologies);
            });

            d3.json(urlIndBySpeech, function(json) {
                jsonData_speeches = json;  
            });
        },
        
        changeSelector: function (){

            var selected = Util.getSelect_value('visualization-selector');
    
            if(selected === "generalIndicators"){
                drawBarChart(jsonData_ontologies);
            }
            else if (selected === "wordCloud"){
                drawKeywords(jsonData_ontologies);
            }
            else if(selected === "temporalEvolution"){
                drawLineChart(jsonData_ontologies);
            }
            else if(selected === "speechesByCountries"){
                drawBarChart_speechesByCountries(jsonData_speeches);
            }
        }        
    };
}());    