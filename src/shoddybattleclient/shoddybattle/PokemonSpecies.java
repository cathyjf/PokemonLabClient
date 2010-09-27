/* PokemonSpecies.java
 *
 * Created April 7, 2009
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import shoddybattleclient.shoddybattle.Pokemon.Gender;

/**
 *
 * @author ben
 */
public class PokemonSpecies {
    private int m_id;
    private String m_name;
    private Set<String> m_moves = new HashSet<String>();
    private Gender m_genders;
    private int[] m_bases = new int[Pokemon.STAT_COUNT];
    private ArrayList<String> m_abilities = new ArrayList<String>();
    private ArrayList<IllegalCombo> m_illegal = new ArrayList<IllegalCombo>();

    public static class IllegalCombo {
        private ArrayList<String> m_moves = new ArrayList<String>();
        private String m_nature = null;
        private String m_ability = null;
        private Gender m_gender = null;
        public void addMove(String move) {
            m_moves.add(move);
        }
        public List<String> getMoves() {
            return m_moves;
        }
        public void setNature(String nature) {
            m_nature = nature;
        }
        public String getNature() {
            return m_nature;
        }
        public void setAbility(String ability) {
            m_ability = ability;
        }
        public String getAbility() {
            return m_ability;
        }
        public void setGender(Gender gender) {
            m_gender = gender;
        }
        public Gender getGender() {
            return m_gender;
        }
        // Helper used to join strings with a plus
        private String join(List<String> items) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                buf.append(items.get(i));
                if (i != (items.size() - 1)) {
                    buf.append(" + ");
                }
            }
            return buf.toString();
        }
        @Override
        public String toString() {
            ArrayList<String> items = new ArrayList<String>();
            if (m_nature != null) {
                items.add(m_nature);
            }
            if (m_ability != null) {
                items.add(m_ability);
            }
            if (m_gender != null) {
                items.add(m_gender.getName());
            }
            for (int i = 0; i < m_moves.size(); i++) {
                items.add(m_moves.get(i));
            }
            return join(items);
        }
    }

    public PokemonSpecies() {

    }
    public static String getNameFromId(List<PokemonSpecies> species, int id) {
        for (PokemonSpecies i : species) {
            if (i.m_id == id) {
                return i.m_name;
            }
        }
        return null;
    }
    public static int getIdFromName(List<PokemonSpecies> species, String name) {
        for (PokemonSpecies i : species) {
            if (i.m_name.equals(name)) {
                return i.m_id;
            }
        }
        return -1;
    }
    public void setId(int id) {
        m_id = id;
    }
    public int getId() {
        return m_id;
    }
    public void setName(String name) {
        m_name = name;
    }
    public String getName() {
        return m_name;
    }
    public void addMove(String move) {
        m_moves.add(move);
    }
    public String[] getMoves() {
        String[] ret = new String[m_moves.size()];
        int i = 0;
        for (String move : m_moves) {
            ret[i++] = move;
        }
        return ret;
    }
    public void addAbility(String ability) {
        m_abilities.add(ability);
    }
    public String[] getAbilities() {
        String[] ret = new String[m_abilities.size()];
        int i = 0;
        for (String ability : m_abilities) {
            ret[i++] = ability;
        }
        return ret;
    }
    public void setGenders(Gender g) {
        m_genders = g;
    }
    public Gender getGenders() {
        return m_genders;
    }
    public void setBase(int i, int stat) {
        m_bases[i] = stat;
    }
    public int getBase(int i) {
        return m_bases[i];
    }
    public void addIllegalCombo(IllegalCombo combo) {
        m_illegal.add(combo);
    }
    public List<IllegalCombo> getIllegalCombos() {
        return m_illegal;
    }
    public String toString() {
        return m_name;
    }
    public boolean equals(Object o2) {
        if (o2 instanceof PokemonSpecies) {
            return m_name.equalsIgnoreCase(((PokemonSpecies)o2).m_name);
        } else if (o2 instanceof String) {
            return m_name.equalsIgnoreCase((String)o2);
        }
        return false;
    }
}
