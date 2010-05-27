/*
 * PokemonNature.java
 *
 * Created on December 15, 2006, 12:20 PM
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2006  Catherine Fitzpatrick
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
import java.util.Iterator;

/**
 * This class represents the nature of a pokemon in the advance generation.
 *
 * @author Catherine
 */
public class PokemonNature implements Comparable {
        
    private static final ArrayList<PokemonNature> m_natures = new ArrayList<PokemonNature>();
    private int m_nature;

    public static final PokemonNature N_LONELY = new PokemonNature(1, "Lonely", Pokemon.S_ATTACK, Pokemon.S_DEFENCE);
    public static final PokemonNature N_BRAVE = new PokemonNature(2, "Brave", Pokemon.S_ATTACK, Pokemon.S_SPEED);
    public static final PokemonNature N_ADAMANT = new PokemonNature(3, "Adamant", Pokemon.S_ATTACK, Pokemon.S_SPATTACK);
    public static final PokemonNature N_NAUGHTY = new PokemonNature(4, "Naughty", Pokemon.S_ATTACK, Pokemon.S_SPDEFENCE);
    public static final PokemonNature N_BOLD = new PokemonNature(5, "Bold", Pokemon.S_DEFENCE, Pokemon.S_ATTACK);
    public static final PokemonNature N_RELAXED = new PokemonNature(7, "Relaxed", Pokemon.S_DEFENCE, Pokemon.S_SPEED);
    public static final PokemonNature N_IMPISH = new PokemonNature(8, "Impish", Pokemon.S_DEFENCE, Pokemon.S_SPATTACK);
    public static final PokemonNature N_LAX = new PokemonNature(9, "Lax", Pokemon.S_DEFENCE, Pokemon.S_SPDEFENCE);
    public static final PokemonNature N_TIMID = new PokemonNature(10, "Timid", Pokemon.S_SPEED, Pokemon.S_ATTACK);
    public static final PokemonNature N_HASTY = new PokemonNature(11, "Hasty", Pokemon.S_SPEED, Pokemon.S_DEFENCE);
    public static final PokemonNature N_JOLLY = new PokemonNature(13, "Jolly", Pokemon.S_SPEED, Pokemon.S_SPATTACK);
    public static final PokemonNature N_NAIVE = new PokemonNature(14, "Naive", Pokemon.S_SPEED, Pokemon.S_SPDEFENCE);
    public static final PokemonNature N_MODEST = new PokemonNature(15, "Modest", Pokemon.S_SPATTACK, Pokemon.S_ATTACK);
    public static final PokemonNature N_MILD = new PokemonNature(16, "Mild", Pokemon.S_SPATTACK, Pokemon.S_DEFENCE);
    public static final PokemonNature N_QUIET = new PokemonNature(17, "Quiet", Pokemon.S_SPATTACK, Pokemon.S_SPEED);
    public static final PokemonNature N_RASH = new PokemonNature(19, "Rash", Pokemon.S_SPATTACK, Pokemon.S_SPDEFENCE);
    public static final PokemonNature N_CALM = new PokemonNature(20, "Calm", Pokemon.S_SPDEFENCE, Pokemon.S_ATTACK);
    public static final PokemonNature N_GENTLE = new PokemonNature(21, "Gentle", Pokemon.S_SPDEFENCE, Pokemon.S_DEFENCE);
    public static final PokemonNature N_SASSY = new PokemonNature(22, "Sassy", Pokemon.S_SPDEFENCE, Pokemon.S_SPEED);
    public static final PokemonNature N_CAREFUL = new PokemonNature(23, "Careful", Pokemon.S_SPDEFENCE, Pokemon.S_SPATTACK);
    public static final PokemonNature N_QUIRKY = new PokemonNature(24, "Quirky", -1, -1);
    public static final PokemonNature N_HARDY = new PokemonNature(0, "Hardy", -1, -1);
    public static final PokemonNature N_SERIOUS = new PokemonNature(12, "Serious", -1, -1);
    public static final PokemonNature N_BASHFUL = new PokemonNature(18, "Bashful", -1, -1);
    public static final PokemonNature N_DOCILE = new PokemonNature(6, "Docile", -1, -1);
    
    transient private int m_internal;
    transient private String m_name;
    transient private int m_harms;
    transient private int m_benefits;
    
    /**
     * Get a nature by index.
     */
    public static PokemonNature getNature(int i) {
        if ((i < 0) || (i >= m_natures.size()))
            return new PokemonNature(-1, "Error", -1, -1);
        return m_natures.get(i);
    }

    /**
     * Get a nature by name
     */
    public static PokemonNature getNature(String name) {
        for (PokemonNature n : m_natures) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }
    
    /**
     * Creates a new instance of PokemonNature by arbitrary indices.
     */
    private PokemonNature(int internal, String name, int benefits, int harms) {
        m_internal = internal;
        m_name = name;
        m_benefits = benefits;
        m_harms = harms;
        m_nature = m_natures.size();
        m_natures.add(this);
    }
    
    /**
     * Initialise this nature by its name. Note that the first letter should
     * be capital, e.g., "Hardy", "Naive", etc.
     */
    private PokemonNature(String name) {
        for (PokemonNature nature : m_natures) {
            if (name.equals(name)) {
                m_internal = nature.m_internal;
                m_name = name;
                m_benefits = nature.m_benefits;
                m_harms = nature.m_harms;
                break;
            }
        }
    }
    
    /**
     * Get the internal value of this nature.
     */
    public int getInternalValue() {
        return m_internal;
    }
    
    /**
     * Get the effect a nature has on a particular stat.
     * This will be 0.9, 1, or 1.1.
     *
     * @param i the index of the statistic
     */
    public double getEffect(int i) {
        return (i == m_benefits) ? 1.1 : ((i == m_harms) ? 0.9 : 1.0);
    }
    
    /**
     * Get a list of natures.
     */
    public static String[] getNatureNames() {
        String[] natures = new String[m_natures.size()];
        Iterator i = m_natures.iterator();
        int j = 0;
        while (i.hasNext()) {
            natures[j++] = ((PokemonNature)i.next()).getName();
        }
        return natures;
    }
    
    /**
     * Get a textual representation of the nature.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * Gets the stat that this nature benefits
     */
    public int getBenefits() {
        return m_benefits;
    }
    
    /**
     * Gets the stat that this nature hinders
     */
    public int getHarms() {
        return m_harms;
    }

    public static PokemonNature[] getNatures() {
        return m_natures.toArray(new PokemonNature[m_natures.size()]);
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(m_name);
        b.append("\t(");
        if (m_benefits < 0) {
            b.append("Neutral)");
        } else {
            b.append("+");
            b.append(Pokemon.getStatName(m_benefits));
            b.append(",-");
            b.append(Pokemon.getStatName(m_harms));
            b.append(")");
        }
        return b.toString();
    }    
}
