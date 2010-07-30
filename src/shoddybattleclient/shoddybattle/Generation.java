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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class loads generation information for the teambuilder. Currently
 * its implementation is temporary and is only here to ease a transition
 * to server specific pokemon lists/learnsets.
 * 
 * @author Carlos
 */
public class Generation {
    private ArrayList<PokemonSpecies> m_species;
    private ArrayList<PokemonMove> m_moves;
    private ArrayList<String> m_items;

    public Generation(ArrayList<PokemonSpecies> species, ArrayList<PokemonMove> moves,
            ArrayList<String> items) {
        Collections.sort(species, new Comparator<PokemonSpecies>() {
            public int compare(PokemonSpecies arg0, PokemonSpecies arg1) {
                return arg0.getName().compareToIgnoreCase(arg1.getName());
            }
        });
        Collections.sort(items);

        m_species = species;
        m_moves = moves;
        m_items = items;
    }

    public ArrayList<PokemonMove> getMoves() {
        return m_moves;
    }

    public ArrayList<PokemonSpecies> getSpecies() {
        return m_species;
    }

    public ArrayList<String> getItems() {
        return m_items;
    }

    public PokemonSpecies getSpeciesByName(String name) {
        int left = 0;
        int right = m_species.size() - 1;
        while (left <= right) {
            int middle = (left+right)/2;
            int compare = name.compareToIgnoreCase(m_species.get(middle).getName());
            if (compare == 0)
                return m_species.get(middle);
            else if (compare < 0)
                right = middle - 1;
            else
                left = middle + 1;
        }
        return null;
    }
}
