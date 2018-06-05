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
var speechesByCountriesCH = (function(){

    return {
        draw: function (pJsonData) {
    
            var dataBC = pJsonData["data"];
            var categories = pJsonData["categories"];

            Util.removeAllChild(d3.selectAll("#chart1"));
            d3.selectAll("#divLateralMenuData").remove();

            var chart;

            nv.addGraph(function() {
                var chart = nv.models.multiBarChart();

                chart.yAxis
                    .tickFormat(d3.format(',.1f'));

                chart.reduceXTicks(false);

                d3.select('#chart1').append('svg')
                    .datum(getData(dataBC, categories))
                    .transition().duration(500)
                    .call(chart)
                    ;

                var xTicks = d3.selectAll("#chart1").selectAll('.nv-x.nv-axis > g').selectAll('g');
                xTicks
                    .selectAll('text')
                    .attr('transform', function(d,i,j) { return 'translate (10, 0) rotate(40 0,0)' }) 
                    .style("text-anchor", "start")
                ;

                nv.utils.windowResize(chart.update);

                return chart;
            });

            function getData(dataBC, categories){

                var arrayCountries = dataBC.map(function(d){return d.country});
                var uniqueCountries = arrayCountries.filter(function(elem, pos) {
                    return arrayCountries.indexOf(elem) == pos;
                }); 
                uniqueCountries.sort();

                var oCountriesData = {};
                var oCountriesDataByCategories = {};
                for (var indexCategory = 0; indexCategory < categories.length; indexCategory++) {
                    var iCategory   = categories[indexCategory];

                    oCountriesDataByCategories[iCategory] = {};
                }

                for (var i = 0; i < uniqueCountries.length; ++i){
                    oCountriesData[uniqueCountries[i]] = {x: uniqueCountries[i], y:0};

                    for (var indexCategory = 0; indexCategory < categories.length; indexCategory++) {
                        var iCategory   = categories[indexCategory];

                        var iDataByCategory = oCountriesDataByCategories[iCategory];
                        iDataByCategory[uniqueCountries[i]] = {x: uniqueCountries[i], y:0};
                    }
                }

                for(var indexSpeech in dataBC){
                    var iSpeech = dataBC[indexSpeech];
                    var currentData = oCountriesData[iSpeech.country];
                    currentData.y += 1;

                    for (var indexCategory = 0; indexCategory < categories.length; indexCategory++) {
                        var iCategory   = categories[indexCategory];

                        if(iSpeech.categories[iCategory] != null){            
                            var iDataByCategory = oCountriesDataByCategories[iCategory];
                            var currentData1 = iDataByCategory[iSpeech.country];
                            currentData1.y += 1;
                        }
                    }
                }

                var arrayData = [];

                var arrayDataByCategories = [];
                for (var indexCategory = 0; indexCategory < categories.length; indexCategory++) {
                    var iCategory   = categories[indexCategory];

                    arrayDataByCategories.push([]);
                }
                for(var indexCountryName in uniqueCountries){
                    var iCountryName = uniqueCountries[indexCountryName];
                    arrayData.push(oCountriesData[iCountryName]);

                    for (var indexCategory = 0; indexCategory < categories.length; indexCategory++) {
                        var iCategory   = categories[indexCategory];

                        var iDataByCategory = oCountriesDataByCategories[iCategory];
                        var oData = iDataByCategory[iCountryName];

                        var iArrayData = arrayDataByCategories[indexCategory];
                        iArrayData.push(oData);
                    }
                }

                var rData = [];
                rData.push({
                    values: arrayData,
                    key: "Num. Speeches - Total"  
                });
                for (var indexCategory = 0; indexCategory < categories.length; indexCategory++) {
                    var iCategory   = categories[indexCategory];
                    var iCategoryText = iCategory.replace(/_/g, " ")

                    rData.push({
                        values: arrayDataByCategories[indexCategory],
                        key: "Num. Speeches - " + iCategoryText
                    });
                }

                return rData;
            }
        }
    };
}());

