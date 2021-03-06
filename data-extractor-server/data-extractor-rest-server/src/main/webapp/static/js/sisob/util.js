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

sisob.util.removeAllChild = function (oObjectD3){
    // Remove all.
    while (oObjectD3.node().hasChildNodes()) {
        oObjectD3.node().removeChild(oObjectD3.node().firstChild);
    }
};

sisob.util.getSelect_value = function (idSelect){
    if(!d3.select("#"+idSelect).empty()){
        return d3.select("#"+idSelect).property("value");
    }
    else{
        return "";
    }
};

sisob.util.addDays = function (date, days) {
    var result = new Date(date);
    result.setDate(date.getDate() + days);
    return result;
};
        
sisob.util.isDateEquals = function (date1, date2){
    var rRes = false;
    if (date1.getDate() === date2.getDate()){
        if(date1.getMonth() === date2.getMonth()){
            if(date1.getFullYear() === date2.getFullYear()){
                rRes = true;
            }
        }
    }
    return rRes;
};

sisob.util.svgToPdf = function (svg, conf){

    var pdfConf = {
        compress: false, 
        layout: 'landscape'
    }

    if(conf != null){
        pdfConf.size = [conf.height, conf.width];
    }

    var doc = new PDFDocument(pdfConf);
    doc.fontSize(20).text(svg.id, {underline: true});
    SVGtoPDF(doc, svg.outerHTML || svg, 0, 0);

    var stream = doc.pipe(blobStream());
    doc.end();
    stream.on('finish', function() {
        var blob = stream.toBlob('application/pdf');

        var obj_url = URL.createObjectURL(blob);

        var today = new Date();
        var todayString = today.toISOString().slice(0, 10);

        var a = document.createElement("a");
        document.body.appendChild(a);
        a.style = "display: none";
        a.href = obj_url;
        a.download = todayString + "-visualization.pdf";
        a.click();
        a.parentNode.removeChild(a);
        window.URL.revokeObjectURL(obj_url);
    });
}; 