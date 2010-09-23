/* SpeciesListParser.java
 *
 * Created April 8, 2009
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2009  Catherine Fitzpatrick and Benjamin Gwin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, visit the Free Software Foundation, Inc.
 * online at http://gnu.org.
 */

package shoddybattleclient.utils;

import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import shoddybattleclient.shoddybattle.Pokemon.Gender;
import shoddybattleclient.shoddybattle.PokemonSpecies;
import shoddybattleclient.shoddybattle.PokemonSpecies.IllegalCombo;

/**
 * This file parses a species XML file and generates an array of Pokemon species
 *
 * @author ben
 */
public class SpeciesListParser extends DefaultHandler {
    private ArrayList<PokemonSpecies> m_species = new ArrayList<PokemonSpecies>();

    private PokemonSpecies tempSpecies;
    private IllegalCombo tempCombo;
    private String tempStr;
    int idx;

    public SpeciesListParser() {

    }

    public ArrayList<PokemonSpecies> parseDocument(String file) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(file, this);
        } catch (IOException e) {
            System.out.println("Failed to open species files");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_species;
    }

    public void startElement (String uri, String localName,
            String qName, Attributes attributes) throws SAXException {
        if (qName.equals("species")) {
            tempSpecies = new PokemonSpecies();
            tempSpecies.setName(attributes.getValue("name"));
            tempSpecies.setId(Integer.parseInt(attributes.getValue("id")));
            idx = 0;
        } else if (qName.equals("combo")) {
            tempCombo = new IllegalCombo();
        }
        tempStr = "";

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String addend = new String(ch, start, length);
        tempStr += addend;
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("gender")) {
            Gender gender = null;
            if (tempStr.equals("Male")) {
                gender = Gender.GENDER_MALE;
            } else if (tempStr.equals("Female")) {
                gender = Gender.GENDER_FEMALE;
            } else if (tempStr.equals("Both")) {
                gender = Gender.GENDER_BOTH;
            } else {
                gender = Gender.GENDER_NONE;
            }
            
            if (tempCombo != null) {
                tempCombo.setGender(gender);
            } else {
                tempSpecies.setGenders(gender);
            }
        } else if (qName.equals("ability")) {
            if (tempCombo != null) {
                tempCombo.setAbility(tempStr);
            } else {
                tempSpecies.addAbility(tempStr);
            }
        } else if (qName.equals("nature")) {
            tempCombo.setNature(tempStr);
        } else if (qName.equals("move")) {
            if (tempCombo != null) {
                tempCombo.addMove(tempStr);
            } else {
                tempSpecies.addMove(tempStr);
            }
        } else if (qName.equals("base")) {
            tempSpecies.setBase(idx, Integer.valueOf(tempStr));
            idx++;
        } else if (qName.equals("species")) {
            m_species.add(tempSpecies);
        } else if (qName.equals("combo")) {
            tempSpecies.addIllegalCombo(tempCombo);
            tempCombo = null;
        }
    }
}
