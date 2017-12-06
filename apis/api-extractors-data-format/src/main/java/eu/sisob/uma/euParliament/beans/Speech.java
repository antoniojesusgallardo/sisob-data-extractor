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
package eu.sisob.uma.euParliament.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Output used in JSON files related to Speech
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class Speech{
        
    private String id;
    private String text;
    
    private String date;
    private String agendaItemNr;
    private String agendaItemTitle;
    private String speechNr;
    private String countryName;
    private String textURI;
    
    private List<Keyword> keywords;

    public Speech() {
        keywords = new ArrayList<Keyword>();
    }
    
    public void addKeyword(Keyword pKeyword){
        
        this.keywords.add(pKeyword);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public String getAgendaItemNr() {
        return agendaItemNr;
    }

    public void setAgendaItemNr(String agendaItemNr) {
        this.agendaItemNr = agendaItemNr;
    }

    public String getAgendaItemTitle() {
        return agendaItemTitle;
    }

    public void setAgendaItemTitle(String agendaItemTitle) {
        this.agendaItemTitle = agendaItemTitle;
    }

    public String getSpeechNr() {
        return speechNr;
    }

    public void setSpeechNr(String speechNr) {
        this.speechNr = speechNr;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getTextURI() {
        return textURI;
    }

    public void setTextURI(String textURI) {
        this.textURI = textURI;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }
}
