/* CloseableTabIcon.java
 *
 * Created on Saturday June 20, 2009, 7:36 PM
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author ben
 */
public class CloseableTabbedPane extends JTabbedPane implements MouseListener, MouseMotionListener {

    public interface CloseableTab {
        // Tell a tab that it is closing
        // Return whether it should continue to close
        public boolean informClosed();
    }

    public interface TabCloseListener {
        public void tabClosed(Component c);
    }

    private java.util.List<TabCloseListener> m_listeners = new ArrayList<TabCloseListener>();

    public CloseableTabbedPane() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void addTabCloseListener(TabCloseListener lst) {
        m_listeners.add(lst);
    }

    public void removeTabCloseListener(TabCloseListener val) {
        for (TabCloseListener lst : m_listeners) {
            if (lst.equals(val)) m_listeners.remove(lst);
        }
    }

    @Override
    public void addTab(String name, Component c) {
        super.addTab("", c);
        this.setIconAt(this.getTabCount() - 1,
                new CloseableTabIcon(this, name, getFontMetrics(getFont())));
    }

    private int indexAt(MouseEvent e) {
        return getUI().tabForCoordinate(this, e.getX(), e.getY());
    }

    @Override
    public void removeTabAt(int idx) {
        Component tab = getComponentAt(idx);
        if (tab instanceof CloseableTab) {
            CloseableTab cTab = (CloseableTab)tab;
            if (!cTab.informClosed()) return;
        }
        super.removeTabAt(idx);
        for (TabCloseListener lst : m_listeners) {
            lst.tabClosed(tab);
        }
    }

    public void mouseReleased(MouseEvent e) {
        int tabIndex = indexAt(e);
        if (tabIndex == -1) return;
        CloseableTabIcon icon = (CloseableTabIcon)this.getIconAt(tabIndex);
        Rectangle r = getBoundsAt(tabIndex);
        if (icon.isOnX((int)(e.getX() - r.getX()), (int)(e.getY() - r.getY()))) {
            removeTabAt(tabIndex);
        }
    }

    public void mouseMoved(MouseEvent e) {
        int tabIndex = indexAt(e);
        if (tabIndex == -1) {
            for (int i = 0; i < getTabCount(); i++) {
                CloseableTabIcon icon = (CloseableTabIcon)this.getIconAt(i);
                icon.setHighlight(false);
                repaint(this.getBoundsAt(i));
            }
            return;
        }
        CloseableTabIcon icon = (CloseableTabIcon)this.getIconAt(tabIndex);
        Rectangle r = getBoundsAt(tabIndex);
        if (icon.isOnX((int)(e.getX() - r.getX()), (int)(e.getY() - r.getY()))) {
            icon.setHighlight(true);
        } else {
            icon.setHighlight(false);
        }
        repaint(r);
    }

    public void mouseDragged(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent arg0) { }
    public void mousePressed(MouseEvent arg0) { }
}
