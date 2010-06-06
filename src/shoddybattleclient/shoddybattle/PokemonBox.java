/* PokemonBox.java
 *
 * Created on Wednesday June 2, 2010, 1:44 PM
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import shoddybattleclient.Preference;
import shoddybattleclient.utils.TeamFileParser;

/**
 * Pokemon boxes are used both by BoxTreeModel and BoxDialog, so this class
 * exists to avoid code repetition.
 * @author Carlos
 */
public class PokemonBox implements Comparable<PokemonBox> {
    public class PokemonWrapper implements Comparable<PokemonWrapper> {
        public String name;
        public Pokemon pokemon;
        public PokemonWrapper(String pokemonName, Pokemon poke) {
            name = pokemonName;
            pokemon = poke;
        }
        public PokemonBox getParent() {
            return PokemonBox.this;
        }
        @Override
        public String toString() {
            return name;
        }
        @Override
        public int compareTo(PokemonWrapper o) {
            return name.compareToIgnoreCase(o.name);
        }
        @Override
        public boolean equals(Object o2) {
            if (o2 == null) return false;
            if (!o2.getClass().equals(getClass())) return false;

            if (name.equalsIgnoreCase(((PokemonWrapper)o2).name))
                return true;
            else
                return false;
        }
        @Override
        public int hashCode() {
            return name.toUpperCase().hashCode();
        }
    }

    private String m_name;
    private ArrayList<PokemonWrapper> m_pokemon;

    public PokemonBox(String name) {
        this(name, null);
    }

    public PokemonBox(String name, String species) {
        m_name = name;
        loadContents(species);
    }

    private void loadContents(String species) {
        m_pokemon = new ArrayList<PokemonWrapper>();

        //Create the box itself if it doesn't exist
        File boxDir = getBoxFolder();
        if (!boxDir.exists())
            boxDir.mkdirs();

        //Read in all the pokemon in this box
        for (File pokeFile : boxDir.listFiles()) {
            if (pokeFile.isDirectory()) continue;
            try {
                TeamFileParser tfp = new TeamFileParser();
                Pokemon poke = tfp.parseTeam(pokeFile.getAbsolutePath())[0];
                if (species == null || poke.toString().equals(species))
                    m_pokemon.add(new PokemonWrapper(pokeFile.getName(), poke));
            }
            catch (Exception ex) {}
        }
        Collections.sort(m_pokemon);
        clearDuplicates(m_pokemon);
    }

    //On *nix systems, PokemonBox allows duplicates on first load.
    //This clears those duplicates from the list.
    //This list MUST be sorted before using this method
    private void clearDuplicates(List<? extends Comparable> list) {
        //Sorted lists allow us to do this in O(n)
        Iterator<? extends Comparable> iter = list.iterator();
        Comparable previous = null;
        while (iter.hasNext()) {
            Comparable current = iter.next();

            //this only happens once, but it makes the code cleaner
            if (previous == null) {
                previous = current;
                continue;
            }

            //FIXME: Find a way to do this without the compiler throwing a hissy fit
            if (previous.compareTo(current) == 0)
                iter.remove();
            previous = current;
        }
    }

    //This creates a new file if it doesn't exist
    public void addPokemon(String name, Pokemon pokemon) throws IOException {
        PokemonWrapper wrapper = new PokemonWrapper(name, pokemon);
        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        buf.append(wrapper.pokemon.toXML());

        File pokemonPath = new File(getBoxPath() + "/" + wrapper.name);
        Writer writer = new PrintWriter(new FileWriter(pokemonPath));
        writer.write(new String(buf));
        writer.flush();
        writer.close();

        //Insert the pokemon into its sorted position
        int i = 0;
        for (i = 0; i < getSize(); i++) {
            int compare = wrapper.compareTo(m_pokemon.get(i));
            if (compare < 0) {
                break;
            } else if (compare == 0) {
                m_pokemon.set(i, wrapper);
                return;
            }
        }
        m_pokemon.add(i, wrapper);
    }

    public int indexOf(PokemonWrapper wrapper) {
        int index = Collections.binarySearch(m_pokemon, wrapper);
        if (index < 0)
            return -1;
        return index;
    }

    public int indexOf(String name) {
        //Wrappers compare using names, so a dud allows us to search in O(logn)
        PokemonWrapper dud = new PokemonWrapper(name, null);
        return indexOf(dud);
    }

    public PokemonWrapper getPokemonAt(int idx) {
        return m_pokemon.get(idx);
    }

    public PokemonWrapper getPokemon(String name) {
        int index = indexOf(name);
        if (index < 0)
            return null;
        return m_pokemon.get(index);
    }

    //If the pokemon doesn't exist, it does nothing
    public void removePokemon(String name) {
        int index = indexOf(name);
        if (index >= 0)
            removePokemonAt(index);
    }

    public void removePokemonAt(int index) {
        PokemonWrapper wrapper = getPokemonAt(index);
        new File(getBoxPath() + "/" + wrapper.name).delete();
        m_pokemon.remove(index);
    }

    public int getSize() {
        return m_pokemon.size();
    }

    public String getName() {
        return m_name;
    }

    public String getBoxPath() {
        return Preference.getBoxLocation() + File.separatorChar + getName();
    }

    public File getBoxFolder() {
        return new File(getBoxPath());
    }

    @Override
    public int compareTo(PokemonBox o) {
        return getName().compareToIgnoreCase(o.getName());
    }

    @Override
    public boolean equals(Object o2) {
        if (o2 == null) return false;
        if (!o2.getClass().equals(getClass())) return false;

        if (getName().equalsIgnoreCase(((PokemonBox)o2).getName()))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return getName().toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
