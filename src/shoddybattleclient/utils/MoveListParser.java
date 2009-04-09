/* MoveListParser.java
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
import shoddybattleclient.shoddybattle.*;
/**
 *
 * @author ben
 */
public class MoveListParser extends DefaultHandler {
    private ArrayList<PokemonMove> m_moves = new ArrayList<PokemonMove>();
    private PokemonMove tempMove;
    private String tempStr;

    public ArrayList<PokemonMove> parseDocument(String file) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse(file, this);
        } catch (IOException e) {
            System.out.println("Failed to open moves files");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_moves;
    }

    public void startElement (String uri, String localName,
			      String qName, Attributes attributes) throws SAXException {
        if (qName.equals("move")) {
            tempMove = new PokemonMove();
            tempMove.name = attributes.getValue("name");
            tempMove.id = Integer.parseInt(attributes.getValue("id"));
        }
        tempStr = "";
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String addend = new String(ch, start, length);
        tempStr += addend;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("type")) {
            tempMove.type = tempStr;
        } else if (qName.equals("class")) {
            tempMove.damageClass = tempStr;
        } else if (qName.equals("power")) {
            tempMove.power = Integer.parseInt(tempStr);
        } else if (qName.equals("accuracy")) {
            tempMove.accuracy = (int) (Double.parseDouble(tempStr) * 100);
        } else if (qName.equals("pp")) {
            tempMove.pp = Integer.parseInt(tempStr);
        } else if (qName.equals("move")) {
            m_moves.add(tempMove);
        }
    }
}
