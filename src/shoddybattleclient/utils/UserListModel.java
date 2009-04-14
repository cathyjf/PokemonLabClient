/*
 * UserListModel.java
 *
 * Created on December 22, 2006, 10:33 AM
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

package shoddybattleclient.utils;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import shoddybattleclient.LobbyWindow.User;

public class UserListModel implements ListModel {
    private final List<User> m_items;
    private List<ListDataListener> m_listeners = new ArrayList<ListDataListener>();
    
    public UserListModel(List<User> items) {
        m_items = items;
    }
    
    public List<User> getList() {
        return m_items;
    }
    
    public int getSize() {
        return m_items.size();
    }
    
    public User getElementAt(int index) {
        try {
            return m_items.get(index);
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
    
    public void add(User user) {
        synchronized (m_items) {
            if (m_items.contains(user)) return;
            Iterator i = m_items.iterator();
            while (i.hasNext()) {
                Object item = (Object)i.next();
                if (item == null) {
                    continue;
                }
                if (item.equals(user)) {
                    return;
                }
            }
        }
        m_items.add(user);
    }
    
    public void remove(String name) {
        synchronized (m_items) {
            for (User u : m_items) {
                if (u.getName().equals(name)) {
                    m_items.remove(u);
                    break;
                }
            }
        }
    }

    public void setStatus(String name, int status) {
        synchronized (m_items) {
            for (User u : m_items) {
                if (u.getName().equals(name)) {
                    u.setStatus(status);
                }
            }
        }
    }

    public void sort() {
        Collections.<User>sort(m_items);
    }
}
