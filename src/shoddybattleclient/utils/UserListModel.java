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
import shoddybattleclient.LobbyWindow.User;

public class UserListModel extends AbstractListModel {

    private List<User> m_users = new ArrayList<User>();

    @Override
    public int getSize() {
        return m_users.size();
    }

    @Override
    public Object getElementAt(int index) {
        return m_users.get(index);
    }

    public void addUser(User u) {
        final int size = m_users.size();
        if (size == 0) {
            m_users.add(u);
            this.fireIntervalAdded(this, 0, 0);
            return;
        }
        for (int i = 0; i < size; ++i) {
            if (u.compareTo(m_users.get(i)) < 0) {
                m_users.add(i, u);
                this.fireIntervalAdded(this, i, i);
                return;
            }
        }
        m_users.add(u);
        this.fireIntervalAdded(u, size, size);
    }

    public void removeUser(String name) {
        User u = new User(name, 0);
        int idx = m_users.indexOf(u);
        m_users.remove(u);
        this.fireIntervalRemoved(u, idx, idx);
    }

    public void setLevel(User u, int flags) {
        User user = null;
        for (int i = 0; i < m_users.size(); i++) {
            user = m_users.get(i);
            if (user.equals(u)) {
                user.setLevel(flags);
                m_users.remove(user);
                this.fireIntervalRemoved(this, i, i);
                addUser(user);
                break;
            }
        }
    }

    public List<String> getNames() {
        List<String> ret = new ArrayList<String>();
        for (User u : m_users) {
            ret.add(u.getName());
        }
        return ret;
    }
}
