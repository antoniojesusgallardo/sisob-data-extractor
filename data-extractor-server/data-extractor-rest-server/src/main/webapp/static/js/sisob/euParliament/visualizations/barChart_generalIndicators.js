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
sisob.euParliament.visualizations.generalIndicators = function (pJsonData) {

    var dataBC = pJsonData["data"];

    sisob.util.removeAllChild(d3.selectAll("#chart1"));
    d3.selectAll("#divLateralMenuData").remove();

    var chart;

    nv.addGraph(function() {
        var chart = nv.models.multiBarChart();

        chart.yAxis
            .tickFormat(d3.format(',.1f'));

        d3.select('#chart1').append('svg')
            .datum(getData(dataBC))
            .transition().duration(500)
            .call(chart)
            ;

        var xTicks = d3.selectAll("#chart1").selectAll('.nv-x.nv-axis > g').selectAll('g');
        xTicks
            .selectAll('text')
            .attr('transform', function(d,i,j) { return 'translate (10, 0) rotate(15 0,0)' }) 
            .style("text-anchor", "start")
        ;

        nv.utils.windowResize(chart.update);

        return chart;
    });

    function getData(dataBC){

        var numKeywords = [],
            numDistintKeyword = [],
            numSpeech = [];

        for(var index in dataBC){
            var oData = dataBC[index];
            numKeywords.push( {x:oData["name"], y: oData["numKeywords"]} );
            numDistintKeyword.push( {x:oData["name"], y: oData["numDistintKeywords"]} );
            numSpeech.push( {x:oData["name"], y: oData["numSpeech"]} );
        }

        return [
            {
                values: numKeywords,
                key: "Number of Keywords Appearanced (Repeated)"
            },
            {
                values: numDistintKeyword,
                key: "Number of Distint Keywords Appearanced (No Repeated)"
            },
            {
                values: numSpeech,
                key: "Number of Speeches with keyword"
            }
        ];
    }
};  
 