/* TeamFileParser.java
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
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import shoddybattleclient.shoddybattle.Pokemon;
import shoddybattleclient.shoddybattle.Pokemon.Gender;

/**
 * This file parses a species XML file and generates an array of Pokemon species
 *
 * @author ben
 */
public class TeamFileParser extends DefaultHandler {
    private List<Pokemon> m_pokemon = new ArrayList<Pokemon>();

    private Pokemon tempPoke;
    private String tempStr;
    private int moveIndex;

    public Pokemon[] parseTeam(String file) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(file, this);
        } catch (IOException e) {
            System.out.println("Failed to open team file");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Pokemon[] ret = new Pokemon[m_pokemon.size()];
        m_pokemon.toArray(ret);
        return ret;
    }

    @Override
    public void startElement (String uri, String localName,
			      String qName, Attributes attributes) throws SAXException {
        if (qName.equals("pokemon")) {
            tempPoke = new Pokemon();
            tempPoke.species = attributes.getValue("species");
            moveIndex = 0;
        } else if (qName.equals("move")) {
            try {
                tempPoke.ppUps[moveIndex] = Integer.parseInt(attributes.getValue("pp-up"));
            } catch (NumberFormatException e) {
                tempPoke.ppUps[moveIndex] = 3;
            }
        } else if (qName.equals("stat")) {
            int statIndex = getStatIndex(attributes.getValue("name"));
            try {
                tempPoke.ivs[statIndex] = Integer.parseInt(attributes.getValue("iv"));
            } catch (NumberFormatException e) {
                tempPoke.ivs[statIndex] = 31;
            }
            try {
                tempPoke.evs[statIndex] = Integer.parseInt(attributes.getValue("ev"));
            } catch (NumberFormatException e) {
                tempPoke.evs[statIndex] = 0;
            }
        }
        tempStr = "";

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String addend = new String(ch, start, length);
        tempStr += addend;
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("nickname")) {
            tempPoke.nickname = tempStr;
        } else if (qName.equals("level")) {
            try {
                tempPoke.level = Integer.parseInt(tempStr);
            } catch (NumberFormatException e) {
                tempPoke.level = 100;
            }
        } else if (qName.equals("gender")) {
            if (tempStr.equals("Male")) {
                tempPoke.gender = Gender.GENDER_MALE;
            } else if (tempStr.equals("Female")) {
                tempPoke.gender = Gender.GENDER_FEMALE;
            } else {
                tempPoke.gender = Gender.GENDER_NONE;
            }
        } else if (qName.equals("nature")) {
            tempPoke.nature = tempStr;
        } else if (qName.equals("item")) {
            tempPoke.item = tempStr;
        } else if (qName.equals("ability")) {
            tempPoke.ability = tempStr;
        } else if (qName.equals("move")) {
            tempPoke.moves[moveIndex] = tempStr;
            moveIndex++;
        } else if (qName.equals("pokemon")) {
            m_pokemon.add(tempPoke);
        }
    }

    private int getStatIndex(String s) {
        for (int i = 0; i < Pokemon.STAT_COUNT; i++) {
            if (s.equals(Pokemon.getStatName(i))) {
                return i;
            }
        }
        return -1;
    }
}
