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
function drawKeywords(pJsonData){
    
    var config = {
        minFontSize: 13, 
        maxFontSize: 80
    }
    
    var dataWC = pJsonData["data"];
    var dataWordCloud = getDataWordCloud(dataWC);
    var scaleSize = getScale(dataWordCloud);
    
    var divContainerId = "#chart1";
    
    Util.removeAllChild(d3.selectAll(divContainerId));
    d3.selectAll("#divLateralMenuData").remove();
    
    var fill = d3.scale.category20();
    
    var heigth = d3.select(divContainerId).node().clientHeight;
    var width = d3.select(divContainerId).node().clientWidth;
    
    heigth = heigth - heigth*0.15;
    width = width - width*0.25;

    d3.layout.cloud().size([width, heigth])
        .words(dataWordCloud)
        .padding(2)
        .rotate(function() {
            return ~~(Math.random() * 2) * 90;
        })
        .font("Impact")
        .fontSize(function(d) {
            return scaleSize(d.size);
        })
        .on("end", drawWC)
        .start()
    ;
    
    drawLateralMenuData();
    
    function drawWC(dataWC) {
        
        if(dataWC.length != dataWordCloud.length){
            alert("Error Code 375. Please contact with the administrator");
        }
        
        d3.select("#chart1")                                                  
            .append("svg")
            .attr("width", width)
            .attr("height", heigth)
            .append("g")
            .attr("transform", "translate("+width/2+","+heigth/2+")")
            .selectAll("text")
            .data(dataWC)
            .enter().append("text")
            .style("font-size", function(d) {
                return d.size + "px";
            })
            .style("font-family", "Impact")
            .style("fill", function(d, i) {
                return fill(i);
            })
            .attr("text-anchor", "middle")
            .attr("transform", function(d) {
                return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
            })
            .text(function(d) {
                return d.text;
            })
            .on("click", function(d){
                showSpeeches(d.text, d.data);
            })
            ;
    };
    
    function drawLateralMenuData(){
        
        d3.selectAll("#chart1").append("div")
            .attr("id", "divLateralMenuData")
            .attr("class", "well")
            .style("float", "right")
            .style("margin-top", "-"+heigth+"px")
            .style("width", "20%")
            .style("font-size", "12px")
            .style("background-color", "#f5f5f0")
            .style("border", "2px solid #e3e3e3")
            .style("height", heigth)
            .style("overflow", "scroll")
            ;  
            
        d3.selectAll("#divLateralMenuData").html("<B>Keyword:<B>");
    }
    
    function showSpeeches(pKeyword, pData){

        var htmlTemplate = ""+
            "<B> Keyword: #KEYWORD_VALUE# </B>" +
            "<BR/>" +
            "<B> Category: </B> #CATEGORIES#" +
            "<BR/>" +
            "<TABLE style='width:100%; font-size:12px;'>" +
                "#ROWS#" + 
            "</TABLE>" + 
            "<BR/>" +
            "<B>Info: Date AgendaItemId SpeechId</B>";
    
        var htmlTemplateRow = ""+
            "<TR>" +
                "<TD>" +
                    "#SPEECH_DATA#" +
                "</TD>" +
                "<TD>" +
                    "<A href='#HREF_TEXT_DETAILS#' target='_blank'>" +
                        "<img src='img/view-icone.png' alt='View' style='width:18px;height:18px'>" +
                    "</A>" +
                "</TD>" +
                "<TD>" +
                    "<A href='#HREF_TEXT_EU#' target='_blank'>" +
                        "<img src='img/eu_logo.png' alt='View' style='width:26px;height:18px'>"+
                    "</A>" +
                "</TD>" +
            "</TR>"; 

        var htmlRows = "";  
        $.each(pData.speeches, function (indexSpeech, iSpeech) {
           
            var speechData      = iSpeech.id.replace(/_/g, " ");
            var hrefTextDetails = urlBaseData + iSpeech.id + ".json";
            var hrefTextEU      = iSpeech.textURI;
            
            htmlRows += htmlTemplateRow;
            htmlRows = htmlRows.replace("#SPEECH_DATA#", speechData);
            htmlRows = htmlRows.replace("#HREF_TEXT_DETAILS#", hrefTextDetails);
            htmlRows = htmlRows.replace("#HREF_TEXT_EU#", hrefTextEU);
        });
        
        var categories = Object.values(pData.categories);
        htmlTemplate = htmlTemplate.replace("#KEYWORD_VALUE#", pKeyword);
        htmlTemplate = htmlTemplate.replace("#CATEGORIES#", categories);
        htmlTemplate = htmlTemplate.replace("#ROWS#", htmlRows);
        
        d3.selectAll("#divLateralMenuData").html(htmlTemplate);
    }
    
    function getDataWordCloud(pJsonData){
        var terms = {};
        
        $.each(pJsonData, function (indexCategory, iCategory) {
            $.each(iCategory.keywords, function (indexKeyword, iKeyword) {
                proccessKeyword(iKeyword, iCategory, terms);                
            });
        });
        
        var arrayTerms = Object.values(terms);
        return arrayTerms;
    };
    
    function proccessKeyword(iKeyword, iCategory, terms){
        try{
            var keyword = iKeyword.value;

            if (terms[keyword] == null){
                terms[keyword] = {  
                    text: keyword, 
                    size: 0,
                    data: {
                        speeches: {},
                        categories: {}    
                    }
                };
            }

            var term = terms[keyword];
            term.size++;
            term.data.speeches[iKeyword.speech.id] = iKeyword.speech;
            term.data.categories[iCategory.name] = iCategory.name;
        }
        catch(err){
            console.log(err);
        }
    }
    
    function getScale(dataWordCloud){
        var arraySize = dataWordCloud.map(function(d){ return d.size; });
        var maxSize = Math.max.apply(Math, arraySize);
        var minSize = Math.min.apply(Math, arraySize);
        
        return d3.scale.linear()
                .domain([minSize, maxSize])
                .range([config.minFontSize, config.maxFontSize]);                
    }
}
