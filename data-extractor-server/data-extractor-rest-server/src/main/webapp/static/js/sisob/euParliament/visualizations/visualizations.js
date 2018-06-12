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

sisob.euParliament.visualizations.data = {
    ontologies: null,
    speeches: null,
    taskCode: null
};

sisob.euParliament.visualizations.init = function(taskCode) {
    
    var visualizations = sisob.euParliament.visualizations;
    var data = sisob.euParliament.visualizations.data;
    
    data.ontologies = null;
    data.speeches = null;
    data.taskCode = taskCode;

    var urlBase =   "resources/file" +
                    "?task_code=" + taskCode + 
                    "&type=result" +
                    "&file=";
            
    var urlIndByCategory= urlBase + "IndicatorsByCategory.json";
    var urlIndBySpeech  = urlBase + "IndicatorsBySpeech.json";

    d3.json(urlIndByCategory)
        .header(sisob.security.AUTHORIZATION_PROPERTY(), sisob.security.getToken())
        .get(function(error, pData) {

        if(error != null){
            console.log(error.responseText);
            return;
        }

        data.ontologies = pData; 
        visualizations.keywordsEvolution(data.ontologies);
    });

    d3.json(urlIndBySpeech)
        .header(sisob.security.AUTHORIZATION_PROPERTY(), sisob.security.getToken())
        .get(function(error, pData) {

        if(error != null){
            console.log(error.responseText);
            return;
        }

        data.speeches = pData; 
        visualizations.keywordsEvolution(data.ontologies);
    });
};
        
sisob.euParliament.visualizations.changeSelector = function (){

    var visualizations = sisob.euParliament.visualizations;
    var data = sisob.euParliament.visualizations.data;

    var selected = sisob.util.getSelect_value('visualization-selector');

    if(selected === "generalIndicators"){
        visualizations.generalIndicators(data.ontologies);
    }
    else if (selected === "wordCloud"){
        visualizations.wordCloud(data.ontologies, data.taskCode);
    }
    else if(selected === "temporalEvolution"){
        visualizations.keywordsEvolution(data.ontologies);
    }
    else if(selected === "speechesByCountries"){
        visualizations.speechesByCountries(data.speeches);
    }
};   
