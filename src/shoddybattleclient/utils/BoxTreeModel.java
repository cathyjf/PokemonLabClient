/* BoxTreeModel.java
 *
 * Created September 20, 2009
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import shoddybattleclient.shoddybattle.Pokemon;
import shoddybattleclient.shoddybattle.PokemonBox;
import shoddybattleclient.shoddybattle.PokemonBox.PokemonWrapper;

/**
 * A subclass of TreeModel that is used to display pokemon in boxes in the team
 * builder
 * @author ben
 */
public class BoxTreeModel implements TreeModel {

    private static final String m_root = "Root";
    private static final String m_default = "(default)";
    private static final String m_teamRoot = "Teams";
    private static final String m_boxRoot = "Boxes";
    private List<TreeModelListener> m_listeners = new ArrayList<TreeModelListener>();
    private List<Pokemon> m_teamPokemon = new ArrayList<Pokemon>();
    private List<PokemonWrapper> m_boxPokemon = new ArrayList<PokemonWrapper>();

    public static boolean isDefaultNode(Object name) {
        return m_default.equals(name);
    }

    public static boolean isTeamRoot(Object name) {
        return m_teamRoot.equals(name);
    }

    public static boolean isBoxRoot(Object name) {
        return m_boxRoot.equals(name);
    }

    public static TreePath getTeamPath() {
        return new TreePath(new Object[]{m_root, m_teamRoot});
    }

    public static TreePath getBoxPath() {
        return new TreePath(new Object[]{m_root, m_boxRoot});
    }

    public Object getRoot() {
        return m_root;
    }

    public Object getChild(Object parent, int idx) {
        if (parent.equals(m_root)) {
            switch (idx) {
                case 0: return m_default;
                case 1: return m_teamRoot;
                case 2: return m_boxRoot;
                default: return null;
            }
        } else if (parent.equals(m_teamRoot)) {
            return m_teamPokemon.get(idx);
        } else if (parent.equals(m_boxRoot)) {
            return m_boxPokemon.get(idx);
        } else {
            return null;
        }
    }

    public int getChildCount(Object node) {
        if (node.equals(m_root)) {
            return 3;
        } else if (node.equals(m_teamRoot)) {
            return m_teamPokemon.size();
        } else if (node.equals(m_boxRoot)) {
            return m_boxPokemon.size();
        } else {
            return 0;
        }
    }

    public boolean isLeaf(Object node) {
        return (node instanceof Pokemon) || (node instanceof PokemonWrapper) || (node.equals(m_default));
    }

    public void valueForPathChanged(TreePath path, Object val) {
        //not editable
    }

    public int getIndexOfChild(Object parent, Object child) {
        if ((parent == null) || (child == null)) return -1;
        if (parent.equals(m_root)) {
            return (child.equals(m_teamRoot)) ? 0 : 1;
        } else if (parent.equals(m_teamRoot)) {
            return m_teamPokemon.indexOf(child);
        } else if (parent.equals(m_boxRoot)) {
            return m_boxPokemon.indexOf(child);
        } else {
            return -1;
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        m_listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        m_listeners.remove(l);
    }

    public void addTeamPokemon(Pokemon p) {
        m_teamPokemon.add(p);
    }

    public void addBoxPokemon(PokemonWrapper wrapper) {
        //This is faster for already sorted lists
        int i = 0;
        for (i = 0; i < m_boxPokemon.size(); i++) {
            int compare = wrapper.compareTo(m_boxPokemon.get(i));
            if (compare == 0 && m_boxPokemon.get(i).getParent().equals(wrapper.getParent())) {
                m_boxPokemon.set(i, wrapper);
                fireChange(wrapper, i);
                return;
            }
            if (compare < 0) break;
        }
        m_boxPokemon.add(i, wrapper);
        fireInsert(wrapper, i);
    }

    private void fireInsert(PokemonWrapper wrapper, int index) {
        TreePath path = getBoxPath();
        TreeModelEvent evt = new TreeModelEvent(this, path, new int[]{index}, new Object[]{wrapper});
        for (TreeModelListener listener : m_listeners) {
            listener.treeNodesInserted(evt);
        }
    }

    private void fireChange(PokemonWrapper wrapper, int index) {
        TreePath path = getBoxPath();
        TreeModelEvent evt = new TreeModelEvent(this, path, new int[]{index}, new Object[]{wrapper});
        for (TreeModelListener listener : m_listeners) {
            listener.treeNodesChanged(evt);
        }
    }

    public void addBox(PokemonBox box) {
        for (int i = 0; i < box.getSize(); i++) {
            m_boxPokemon.add(box.getPokemonAt(i));
        }
        Collections.sort(m_boxPokemon);
    }
}
