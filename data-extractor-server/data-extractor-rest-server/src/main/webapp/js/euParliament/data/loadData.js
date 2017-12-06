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
function loadData(){
       
    d3.json(urlJson, function(json) {
        processJson(json);
    });

    /*
     * Function that processes json object, to draw the information.
     * @param {Object} jsonContent
     */
    function processJson(jsonContent){
        
        var categories  = jsonContent["categories"];
        var data        = jsonContent["data"];
        
        var colours = ( (categories.length <= 10) ? d3.scale.category10() : d3.scale.category20());
        
        drawData(data);
        drawLegend(categories, colours);
        drawText(data.text, categories, colours);
    }

    /**
     * Function that draws the header with the speech data.
     * @param {Object} pData
     */
    function drawData(pData){

        try{
            var fields = [
                {label: "ID"                , value: pData.id},
                {label: "Date"              , value: pData.date},
                {label: "Agenda Item Nr"    , value: pData.agendaItemNr},
                {label: "Agenda Item Title" , value: pData.agendaItemTitle},
                {label: "Speech Nr"         , value: pData.speechNr},
                {label: "Country"           , value: pData.countryName},
                {label: "URI"               , value: "<a href='"+pData.textURI+"' target='_blank'>"+pData.textURI+"</a>"}
            ];

            var htmlData = "";
            $.each(fields, function (indexField, iField) {
                var htmlField ="<B>#LABEL# : </B> #VALUE# "+
                                 "<br/>";

                htmlField = htmlField.replace("#LABEL#", iField.label);    
                htmlField = htmlField.replace("#VALUE#", iField.value);  

                htmlData += htmlField;
            });

            $("#contentData").html(htmlData);
        }
        catch(e){
            console.log(e);
        }
    }

    /**
     * Function that draws the the legend in the header. This legend contains 
     * the keyword colours, each keyword category had a colour.
     * @param {array[string]} pCategories
     * @param {d3.scale} colours
     */
    function drawLegend(pCategories, colours){

        try{
            var layoutHtmlItemLegend = "" +
                "<div> " + 
                    "|#INDEX#| <span style='background-color: #COLOR#;'>#CATEGORY#</span>"+
                "</div>";

            var htmlLegend = "";

            $.each(pCategories, function (indexCategory, iCategory) {
                var indexLegend = indexCategory + 1;

                var htmlItemLegend = layoutHtmlItemLegend;
                htmlItemLegend = htmlItemLegend.replace("#INDEX#", indexLegend);  
                htmlItemLegend = htmlItemLegend.replace("#COLOR#", colours(indexCategory));  
                htmlItemLegend = htmlItemLegend.replace("#CATEGORY#", iCategory);  

                htmlLegend += htmlItemLegend;
            });    
            $("#contentLegend").html(htmlLegend);
        }
        catch(e){
            console.log(e);
        }
    }

    /**
     * Function that draws the speech text.
     * @param {string} pText
     * @param {array[string]} pCategories
     * @param {d3.scale} colours
     */
    function drawText(pText, pCategories, colours){
        
        try{
            var majorTypeAccepted = 'Cultural_Heritage';
            var htmlText = pText.replace(/(?:\r\n|\r|\n)/g, '<br/>');

            $("#contentText").html(htmlText);

            $("lookup[majortype="+majorTypeAccepted+"]").each(function() {
                var iTag = $(this);

                var minorType = iTag.attr("minortype");
                if(minorType==null || !pCategories.includes(minorType) ){
                    // Continue bucle
                    return;
                }

                var indexLegend = pCategories.indexOf(iTag.attr("minorType")) + 1;

                var htmlKeyword = ""+
                    "<b>|#INDEX#|</b>"+
                    "<span style='background-color: #COLOR#;'>"+
                        "#KEYWORD#"+
                    "</span>"+
                    "<b>|#INDEX#|</b>";
                htmlKeyword = htmlKeyword.replace("#COLOR#", colours(indexLegend-1));  
                htmlKeyword = htmlKeyword.replace("#KEYWORD#", iTag.text() );  
                htmlKeyword = htmlKeyword.replace("#INDEX#", indexLegend);
                htmlKeyword = htmlKeyword.replace("#INDEX#", indexLegend);

                iTag.html(htmlKeyword);
            });
        }
        catch(e){
            console.log(e);
        }
    }
}