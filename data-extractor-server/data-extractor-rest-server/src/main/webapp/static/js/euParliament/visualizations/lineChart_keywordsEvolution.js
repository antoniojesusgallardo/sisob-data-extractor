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
function drawLineChart(pJsonData){

    var dataLC = pJsonData["data"];

    Util.removeAllChild(d3.selectAll("#chart1"));
    d3.selectAll("#divLateralMenuData").remove();
    
    var arrayOntologyTypes = dataLC.map(function(d){ return d.name; });
    arrayOntologyTypes.unshift('All');
    
    d3.selectAll("#chart1").text('Select Ontology Item:');
    
    d3.selectAll("#chart1").append("select")
        .attr("class", "chzn-select")
        .attr("id", "idSelectOntologyType")
        .on("change", function(d){changeOntologyType(d);})
        .selectAll("option")
            .data(arrayOntologyTypes).enter()
            .append("option")
            .attr("value", function(d){return d;})
            .text( function(d){return Util.getCategoryFormatted(d);})
        ;
                
    var chart;
    
    drawLineChart_Chart();
    
    function drawLineChart_Chart(){
       
        nv.addGraph(function() {
            var chart = nv.models.lineWithFocusChart(); //.lineWithFocusChart(); //nv.models.lineChart();  //nv.models.multiBarChart();
    //                        .useInteractiveGuideline(true)
                            ;
                            
            chart.xAxis
                .tickFormat(function(d) {
                    return d3.time.format('%d-%m-%y')(new Date(d));
                });
            chart.xScale(d3.time.scale()); //fixes misalignment of timescale with line graph 

            chart.yAxis
                .tickFormat(d3.format(',.1f'));

            chart.x2Axis
                .tickFormat(function(d) {
                    return d3.time.format('%d-%m-%y')(new Date(d));
                });

            chart.y2Axis
                .tickFormat(d3.format(',.1f'));

            d3.select('#chart1').append('svg')
                .datum(getData(dataLC))
                .transition().duration(500)
                .call(chart)
                ;

            nv.utils.windowResize(chart.update);

            return chart;
        });
    };

    

    function changeOntologyType(itemSelected){
        d3.selectAll("#chart1").selectAll("svg").remove();
        drawLineChart_Chart();
    }

    function getData(dataLC){
        
        /*
         * 
         * Format JSON Data
        0: Object
	keywords: Object (Category)
		culture: Object (Keyword)
			count: 2
			name: "culture"
                        numDistintKeyword: 1
                        numKeywords: 1
                        numSpeech: 1
			speech: Object (Speeches)
                            country: "Germany"
                            date: "1999-07-20"
                            id: "1999-07-20_3_22"
                            textURI: "http://www.europarl.europa.eu/sides/getDoc.do?pubRef=-//EP//TEXT+CRE+19990
                        
        1: Object
        ... 
         */
        
        var categorySelected = Util.getSelect_value('idSelectOntologyType');
        
        try{
            
            var keywords = {};
            $.each(dataLC, function (indexCategory, iCategory) { 

                // Filter by Category
                if (categorySelected!=='All' && iCategory.name!=categorySelected ){
                    return true;
                }
                
                // Convert JSON Data to Objects with the values
                $.each(iCategory.keywords, function (indexKeyword, iKeyword) {
                    
                    var keyword_name= iKeyword.value;
                    var speech      = iKeyword.speech;
                   
                    // 'map' is a keyword in Javascript. We rename this name.
                    /*if(keyword_name == 'map'){
                        keyword_name = keyword_name + " ";
                    }*/
                    
                    var keyword = keywords[keyword_name];
                    if(keyword == null){
                        keyword = {key: keyword_name , dates: {} };
                    }
                    
                    if (keyword.dates[speech.date] == null){
                        keyword.dates[speech.date] = 0;
                    }

                    keyword.dates[speech.date] += 1;
                    
                    keywords[keyword_name] = keyword;
                });
            });
            
            /* Format D3.js
             *                 
                0: Object
                    key: "cultural action"
                    values: Array[1]
                        0: Object (Data)
                            x: Wed Oct 27 1999 02:00:00 GMT+0200 (CEST)
                            y: 1
                1: Object
                    key: "cultural education"
                    values: Array[1]
                        0: Object (Data)
                            x: Thu Dec 16 1999 01:00:00 GMT+0100 (CET)
                            y: 1
                ...
             */
            // Convert Objects with the values to Format Values from D3.js: {key: XX , values: []} 
            var listKeywords = [];
            $.each(keywords, function (indexKeyword, iObjKeyword) {
                
                var key = iObjKeyword.key;                
                
                var iListKeyword = {
                    key:  key, 
                    values: []
                };
                
                for (var fieldName in iObjKeyword.dates) { 
                    iListKeyword.values.push({
                        x: new Date(fieldName), 
                        y: iObjKeyword.dates[fieldName] 
                    });
                }
                iListKeyword.values = iListKeyword.values.sort(compareKeywordValues);
                
                listKeywords.push(iListKeyword);
            });
            
            // Insert dates with cero values
            $.each(listKeywords, function (indexKeyword, iKeyword) {
                
                var valuesTemp = [];
                var lastDate = null;
                
                $.each(iKeyword.values, function (indexKeyword2, currentValue) {
                    
                    if(lastDate != null){
                        lastDate = UtilDate.addDays(lastDate , 1);
                        if( !(UtilDate.isDateEquals(lastDate, currentValue.x)) ){
                            var newData = { x:lastDate, y:0};
                            valuesTemp.push(newData); // console.log(iKeyword.key + " - "+ newData.x);
                            
                            var previousCurrentDate = UtilDate.addDays(currentValue.x , -1);
                            if( !(UtilDate.isDateEquals(previousCurrentDate, lastDate)) ){
                                var newData = { x:previousCurrentDate, y:0};
                                valuesTemp.push(newData); // console.log(iKeyword.key + " - "+ newData.x);
                            }
                        }
                    }
                    
                    valuesTemp.push(currentValue); //console.log(iKeyword.key + " - "+ currentValue.x);
                    lastDate = currentValue.x;
                });
                
                iKeyword.values = valuesTemp; 
            });
            
            var arrayKeywords = [];
            // Ignore the keywords with only an appearance.
            /*for(var iK in listKeywords){
                var iKeyword = listKeywords[iK];
                
                // Remove keywords with one date
                if (Object.keys(iKeyword.values).length > 1){
                    arrayKeywords.push(iKeyword);    
                }
            }
            arrayKeywords = arrayKeywords.sort(compareKeyword);*/
            // FIN - TODO BORRAR
            
            // If we have one value, we create the previous and next day with cero value.
            for(var iK in listKeywords){
                var iKeyword = listKeywords[iK];
                
                if (Object.keys(iKeyword.values).length === 1){
            
                    var iDate = iKeyword.values[0].x;
                    var previousDay = UtilDate.addDays(iDate, -1);
                    var nextDay = UtilDate.addDays(iDate, 1);
                    
                    var dataPreviousDay = { x:previousDay, y:0};
                    var dataNextDay = { x:nextDay, y:0};
                    
                    iKeyword.values.unshift(dataPreviousDay);
                    iKeyword.values.push(dataNextDay);
                }
            }
            listKeywords = listKeywords.sort(compareKeyword);
            
            arrayKeywords = listKeywords;
            // End - If we have one value, we create the previous and next day with cero value.
        }
        catch(err) {
            console.log(err.message);
        }
        
        return arrayKeywords;
        
        function compareKeyword(a,b) {
            if (a.key < b.key)
                return -1;
            if (a.key > b.key)
                return 1;
            return 0;
        }
        
        function compareKeywordValues(a,b) {
            if (a.x < b.x)
                return -1;
            if (a.x > b.x)
                return 1;
            return 0;
        }
    };
    
}
