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

sisob.euParliament.visualizations.clickDownload = function(){
    
    var conf = null;

    var selected = sisob.util.getSelect_value('visualization-selector');
    if(selected !== "wordCloud"){
        conf = {
            width : $("#chart1").width(),
            height: $("#chart1").height()
        }
    }

    // SVG-to-PDFKit is not compatible with NVD3. Add attributes in svg
    svgAddAttr(selected);

    let svg = document.querySelector('#chart1 > svg');
    sisob.util.svgToPdf(svg, conf);

    // SVG-to-PDFKit is not compatible with NVD3. Undo - Add attributes in svg
    svgUndoAddAttr(selected);
    
    function svgAddAttr(visualizationSelected){

        if(visualizationSelected !== "wordCloud"){
            $("#chart1 text").attr("font-size", "12"); 
            $("g.tick > line").attr("stroke", "#e5e5e5");
            $("path.domain").attr("stroke", "black"); 
            $("g.nv-disabled circle").attr("fill-opacity","0"); 

            if(visualizationSelected === "temporalEvolution"){
                $("path.nv-line").attr("fill", "none");
                $("path.nv-point")
                    .attr("fill-opacity","0")
                    .attr("stroke-opacity","0");

                // if uses a range of time
                $("g.nv-point-paths path")
                    .attr("stroke","#aaa")
                    .attr("stroke-opacity","0")
                    .attr("fill","#eee")
                    .attr("fill-opacity","0");

                $("g.nv-brushBackground rect.left, g.nv-brushBackground rect.right")
                    .attr("stroke","#000")
                    .attr("stroke-width",".4")
                    .attr("fill","#fff")
                    .attr("fill-opacity",".7");

                $("g.nv-x.nv-brush rect.extent")
                    .attr("fill-opacity","0");

                $("g.resize.e path, g.resize.w path")
                    .attr("fill", "#eee")
                    .attr("stroke", "#666");
            }
        }
    }

    function svgUndoAddAttr(visualizationSelected){

        if(visualizationSelected !== "wordCloud"){
            $("#chart1 text").removeAttr("font-size");
            $("g.tick > line").removeAttr("stroke");
            $("path.domain").removeAttr("stroke");
            $("g.nv-disabled circle").removeAttr("fill-opacity");

            if(visualizationSelected === "temporalEvolution"){
                $("path.nv-line").removeAttr("fill")
                $("path.nv-point")
                    .removeAttr("fill-opacity")
                    .removeAttr("stroke-opacity");

                // if uses a range of time
                $("g.nv-point-paths path")
                    .removeAttr("stroke")
                    .removeAttr("stroke-opacity")
                    .removeAttr("fill")
                    .removeAttr("fill-opacity");

                $("g.nv-brushBackground rect.left, g.nv-brushBackground rect.right")
                    .removeAttr("stroke")
                    .removeAttr("stroke-width")
                    .removeAttr("fill")
                    .removeAttr("fill-opacity");

                $("g.nv-x.nv-brush rect.extent")
                    .removeAttr("fill-opacity"); 

                $("g.resize.e path, g.resize.w path")
                    .removeAttr("fill")
                    .removeAttr("stroke");
            }
        }
    }
};      
