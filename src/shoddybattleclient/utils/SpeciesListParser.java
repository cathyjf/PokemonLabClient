/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/**
 * This file parses a species XML file and generates an array of Po
 *
 * @author ben
 */
public class SpeciesListParser extends DefaultHandler {
    private ArrayList<PokemonSpecies> m_species = new ArrayList<PokemonSpecies>();

    private PokemonSpecies tempSpecies;
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
            idx = 0;
        }
        tempStr = "";

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String addend = new String(ch, start, length);
        tempStr += addend;
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("gender")) {
            if (tempStr.equals("Male")) {
                tempSpecies.setGenders(Gender.GENDER_MALE);
            } else if (tempStr.equals("Female")) {
                tempSpecies.setGenders(Gender.GENDER_FEMALE);
            } else if (tempStr.equals("Both")) {
                tempSpecies.setGenders(Gender.GENDER_BOTH);
            } else {
                tempSpecies.setGenders(Gender.GENDER_NONE);
            }
        } else if (qName.equals("ability")) {
            tempSpecies.addAbility(tempStr);
        } else if (qName.equals("move")) {
            tempSpecies.addMove(tempStr);
        } else if (qName.equals("base")) {
            tempSpecies.setBase(idx, Integer.valueOf(tempStr));
            idx++;
        } else if (qName.equals("species")) {
            m_species.add(tempSpecies);
        }
    }
}
