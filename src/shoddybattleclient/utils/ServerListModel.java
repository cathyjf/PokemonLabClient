/*
 * ServerListModel.java
 *
 * Created on Apr 9, 2009, 3:04:28 PM
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
import java.util.Comparator;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import shoddybattleclient.WelcomeWindow.ServerListEntry;


/**
 *
 * @author ben
 */
public class ServerListModel implements ListModel {

    private List<ServerListEntry> m_entries = new ArrayList<ServerListEntry>();
    private List<ListDataListener> m_listeners = new ArrayList<ListDataListener>();

    public ServerListModel(ServerListEntry[] entries) {
        for (int i = 0; i < entries.length; i++) {
            m_entries.add(entries[i]);
        }
        Collections.sort(m_entries, new Comparator<ServerListEntry>() {
            public int compare(ServerListEntry o1, ServerListEntry o2) {
                if (o1.getUsers() < o2.getUsers()) {
                    return 1;
                } else if (o1.getUsers() > o2.getUsers()) {
                    return -1;
                }
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    public int getSize() {
        return m_entries.size();
    }

    public ServerListEntry getElementAt(int index) {
        try {
            return m_entries.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public void addListDataListener(ListDataListener l) {
        m_listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        m_listeners.remove(l);
    }

}
