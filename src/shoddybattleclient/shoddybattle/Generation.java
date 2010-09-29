/* Generation.java
 *
 * Created July 6, 2010
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

package shoddybattleclient.shoddybattle;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import shoddybattleclient.TeamBuilder;
import shoddybattleclient.utils.MoveListParser;
import shoddybattleclient.utils.SpeciesListParser;

/**
 * This class loads generation information for the teambuilder. Currently
 * its implementation is temporary and is only here to ease a transition
 * to server specific pokemon lists/learnsets.
 * 
 * @author Carlos
 */
public class Generation {
    private List<PokemonSpecies> m_species;
    private List<PokemonMove> m_moves;
    private List<String> m_items;

    public Generation(List<PokemonSpecies> species,
            List<PokemonMove> moves,
            List<String> items) {
        Collections.sort(species, new Comparator<PokemonSpecies>() {
            @Override
            public int compare(PokemonSpecies arg0, PokemonSpecies arg1) {
                return arg0.getName().compareToIgnoreCase(arg1.getName());
            }
        });
        Collections.sort(items);

        m_species = species;
        m_moves = moves;
        m_items = items;
    }

    public static List<String> loadItems() {
        List<String> items = new ArrayList<String>();
        try {
            Scanner itemScanner = new Scanner(new URL(
                    TeamBuilder.class.getResource(
                    "resources/items.txt").toString()).openStream());
            while (itemScanner.hasNextLine()) {
                String line = itemScanner.nextLine();
                if (!line.equals("")) {
                    items.add(line);
                }
            }
        } catch (Exception ex) {

        }
        return items;
    }

    // Temporary holdover until proper generation loading is implemented
    public static Generation loadGeneration() {
        MoveListParser mlp = new MoveListParser();
        List<PokemonMove> moves = mlp.parseDocument(
                TeamBuilder.class.getResource("resources/moves.xml").toString());
        SpeciesListParser parser = new SpeciesListParser();
        List<PokemonSpecies> species = parser.parseDocument(
                TeamBuilder.class.getResource("resources/species.xml").toString());
        List<String> items = Generation.loadItems();
        return new Generation(species, moves, items);
    }

    public List<PokemonMove> getMoves() {
        return m_moves;
    }

    public List<PokemonSpecies> getSpecies() {
        return m_species;
    }

    public List<String> getItems() {
        return m_items;
    }

    public PokemonSpecies getSpeciesById(int id) {
        for (PokemonSpecies ps : m_species) {
            if (ps.getId() == id) {
                return ps;
            }
        }
        return null;
    }

    public PokemonSpecies getSpeciesByName(String name) {
        int left = 0;
        int right = m_species.size() - 1;
        while (left <= right) {
            int middle = (left+right)/2;
            int compare = name.compareToIgnoreCase(
                    m_species.get(middle).getName());
            if (compare == 0) {
                return m_species.get(middle);
            } else if (compare < 0) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }
        return null;
    }
}
