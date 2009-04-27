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
import java.util.List;
import shoddybattleclient.shoddybattle.Pokemon.Gender;

/**
 *
 * @author ben
 */
public class PokemonSpecies {
    private int m_id;
    private String m_name;
    private ArrayList<String> m_moves = new ArrayList<String>();
    private Gender m_genders;
    private int[] m_bases = new int[Pokemon.STAT_COUNT];
    private ArrayList<String> m_abilities = new ArrayList<String>();

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
}
