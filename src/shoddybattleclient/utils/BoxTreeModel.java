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
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import shoddybattleclient.shoddybattle.Pokemon;

/**
 * A subclass of TreeModel that is used to display pokemon in boxes in the team
 * builder
 * @author ben
 */
public class BoxTreeModel implements TreeModel {

    public class Box {
        private String m_name;
        private List<Pokemon> m_pokemon = new ArrayList<Pokemon>();
        public Box(String name) {
            m_name = name;
            loadContents();
        }
        public String toString() {
            return m_name;
        }
        private void loadContents() {

        }
        public int getChildCount() {
            return m_pokemon.size();
        }
        public Pokemon getChild(int idx) {
            return m_pokemon.get(idx);
        }
        public int indexOf(Object child) {
            return m_pokemon.indexOf(child);
        }
    }

    private static final String m_root = "Root";
    private static final String m_default = "(default)";
    private static final String m_teamRoot = "Teams";
    private static final String m_boxRoot = "Boxes";
    private List<TreeModelListener> m_listeners = new ArrayList<TreeModelListener>();
    private List<Pokemon> m_teamPokemon = new ArrayList<Pokemon>();
    private List<Box> m_boxes = new ArrayList<Box>();

    public static boolean isDefaultNode(String name) {
        return m_default.equals(name);
    }

    public static boolean isTeamRoot(String name) {
        return m_teamRoot.equals(name);
    }

    public static boolean isBoxRoot(String name) {
        return m_boxRoot.equals(name);
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
            return m_boxes.get(idx);
        } else if (parent instanceof Box) {
            return ((Box)parent).getChild(idx);
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
            return m_boxes.size();
        } else if (node instanceof Box) {
            return ((Box)node).getChildCount();
        } else {
            return 0;
        }
    }

    public boolean isLeaf(Object node) {
        return (node instanceof Pokemon) || (node.equals(m_default));
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
            return m_boxes.indexOf(child);
        } else if (parent instanceof Box) {
            return ((Box)parent).indexOf(child);
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

}
