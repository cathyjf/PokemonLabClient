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
import shoddybattleclient.network.ServerLink.TimerOptions;
import shoddybattleclient.utils.ClauseList.Clause;
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

    public static interface RuleSet {
        public TimerOptions getTimerOptions();
        public int[] getClauses(List<Clause> clauses);
    }

    public static class Metagame implements RuleSet {
        private int m_idx;
        private String m_name;
        private String m_id;
        private String m_description;
        private int m_partySize;
        private int m_maxTeamLength;
        private List<String> m_banList;
        private List<String> m_clauses;
        private Pokemon[] m_team;
        private TimerOptions m_timerOptions;
        public Metagame(int idx, String name, String id, String description,
                int partySize, int maxTeamLength, List<String> banList,
                List<String> clauses, TimerOptions timeOps) {
            m_idx = idx;
            m_name = name;
            m_id = id;
            m_description = description;
            m_partySize = partySize;
            m_maxTeamLength = maxTeamLength;
            m_banList = banList;
            m_clauses = clauses;
            m_timerOptions = timeOps;
        }
        /*public int getIdx() {
            return m_idx;
        }*/
        public String getName() {
            return m_name;
        }
        public String getId() {
            return m_id;
        }
        public String getDescription() {
            return m_description;
        }
        public int getPartySize() {
            return m_partySize;
        }
        public int getMaxTeamLength() {
            return m_maxTeamLength;
        }
        public String[] getBanList() {
            return m_banList.toArray(new String[m_banList.size()]);
        }
        public String[] getClauseList() {
            return m_clauses.toArray(new String[m_clauses.size()]);
        }
        public int[] getClauses(List<Clause> clauses) {
            int[] ret = new int[m_clauses.size()];
            for (int i = 0; i < m_clauses.size(); i++) {
                String name = m_clauses.get(i);
                ret[i] = clauses.indexOf(new Clause(name, null));
            }
            return ret;
        }
        public TimerOptions getTimerOptions() {
            return m_timerOptions;
        }
        public void setTeam(Pokemon[] team) {
            m_team = team;
        }
        public Pokemon[] getTeam() {
            return m_team;
        }
        @Override
        public String toString() {
            return m_name;
        }
    }

    private String m_id;
    private String m_name;
    private List<PokemonSpecies> m_species;
    private List<PokemonMove> m_moves;
    private List<String> m_items;
    private List<Metagame> m_metagames = new ArrayList<Metagame>();

    public Generation(String id, String name,
            List<PokemonSpecies> species,
            List<PokemonMove> moves,
            List<String> items) {
        m_id = id;
        m_species = species;
        m_moves = moves;
        m_items = items;

        Collections.sort(species, new Comparator<PokemonSpecies>() {
            @Override
            public int compare(PokemonSpecies arg0, PokemonSpecies arg1) {
                return arg0.getName().compareToIgnoreCase(arg1.getName());
            }
        });
        Collections.sort(items);
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
        return new Generation("gen4", "Generation 4", species, moves, items);
    }

    // Temporary holdover until proper generation loading is implemented
    public static Generation loadGeneration(String id, String name) {
        Generation gen = loadGeneration();
        gen.m_id = id;
        gen.m_name = name;
        return gen;
    }

    public String getId() {
        return m_id;
    }

    public String getName() {
        return m_name;
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

    public List<Metagame> getMetagames() {
        return m_metagames;
    }

    public Metagame getMetagame(int idx) {
        return m_metagames.get(idx);
    }

    public void addMetagame(Metagame metagame) {
        m_metagames.add(metagame);
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

    @Override
    public String toString() {
        return m_name;
    }
}
