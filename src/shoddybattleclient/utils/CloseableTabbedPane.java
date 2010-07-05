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

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import shoddybattleclient.GameVisualisation;

/**
 *
 * @author ben
 */
public class CloseableTabbedPane extends JTabbedPane implements MouseMotionListener, MouseListener {

    public interface CloseableTab {
        // Tell a tab that it is closing
        // Return whether it should continue to close
        public boolean informClosed();
    }

    public interface TabCloseListener {
        public void tabClosed(Component c);
    }

    private static class CloseableTabIcon implements Icon, ActionListener {
        private CloseableTabbedPane m_parent;

        private String m_text;
        private int m_ascent;
        private int m_strWidth;
        private int m_strHeight;

        private boolean m_closeable;
        private boolean m_highlight = false;
        private Color m_textColour = COLOR_NORMAL;
        private Timer m_timer = new Timer(500, this);

        // Calling JTabbedPane.getBoundsAt doesn't get the icon's bounds.
        // Updating these in paintIcon is hacky, but it works
        private int m_x = 0;
        private int m_y = 0;

        //These variables are used as constant data
        private static final Color COLOR_NORMAL = Color.BLACK;
        private static final Color COLOR_FLASH = Color.RED;
        private static final int BUFFER = 6;
        private static final Image m_icon = GameVisualisation.getImageFromResource("x.gif");
        private static final int m_iconH = 7;
        private static final int m_iconW = 7;

        public CloseableTabIcon(CloseableTabbedPane parent, String text, FontMetrics metrics, boolean closeable) {
            m_parent = parent;
            m_closeable = closeable;
            setText(text, metrics);

            MediaTracker tracker = new MediaTracker(parent);
            tracker.addImage(m_icon, 0);
            try {
                tracker.waitForAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setText(String text, FontMetrics metrics) {
            m_text = text;
            m_ascent = metrics.getAscent();
            m_strWidth = metrics.stringWidth(text);
            m_strHeight = metrics.getHeight();
        }

        public void setHighlight(boolean highlight) {
            m_highlight = highlight;
        }

        public boolean isOnX(int x, int y) {
            if (!m_closeable) return false;

            int minY = m_ascent - 13;
            int minX = m_strWidth + BUFFER;
            if ((x < minX) || (x > (minX + 13))) return false;
            if ((y < minY) || (y > (minY + 13))) return false;
            return true;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            m_x = x;
            m_y = y;

            Graphics2D g2 = (Graphics2D)g.create(x, y, getIconWidth(), getIconHeight());

            if (m_text != null) {
                g2.setColor(m_textColour);
                g2.drawString(m_text, 0, m_ascent);
            }

            if (m_closeable) {
                int left = m_strWidth + BUFFER + 3;
                int top = m_ascent-m_iconH;
                if (m_highlight) {
                    int r = 12;
                    g2.setColor(new Color(235, 235, 235));
                    g2.fillOval(left-3, top-3, r, r);
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawOval(left-3, top-3, r, r);
                }
                g2.drawImage(m_icon, left, top, c);
                g2.dispose();
            }
        }

        public int getIconWidth() {
            if (!m_closeable)
                return m_strWidth;
            return m_strWidth + BUFFER + 13;
        }

        public int getIconHeight() {
            return m_strHeight;
        }

        public Rectangle getBounds() {
            return new Rectangle(m_x, m_y, getIconWidth(), getIconHeight());
        }

        private void repaint() {
            m_parent.repaint(this);
        }

        public void setFlashing(boolean flashing) {
            if (flashing) {
                m_timer.start();
            } else {
                m_textColour = COLOR_NORMAL;
                m_timer.stop();
                repaint();
            }
        }

        public void actionPerformed(ActionEvent e) {
            m_textColour = (m_textColour == COLOR_NORMAL) ? COLOR_FLASH : COLOR_NORMAL;
            repaint();
        }
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
        addTab(name, c, true);
    }

    public void addTab(String name, Component c, boolean closeable) {
        super.addTab("", c);
        this.setIconAt(this.getTabCount() - 1,
                new CloseableTabIcon(this, name, getFontMetrics(getFont()), closeable));
    }

    // While setTitleAt works, getTitleAt doesn't. This is because Swing sucks.
    @Override public void setTitleAt(int idx, String text) {
        if (idx < 0 || idx >= getTabCount()) return;
        CloseableTabIcon icon = (CloseableTabIcon)getIconAt(idx);
        if (icon == null) return;
        icon.setText(text, getFontMetrics(getFont()));
        repaint();
    }

    @Override
    public void removeTabAt(int idx) {
        if (idx >= getTabCount()) return;
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

    public void setFlashingAt(int idx, boolean flashing) {
        CloseableTabIcon icon = (CloseableTabIcon)getIconAt(idx);
        if (icon != null) icon.setFlashing(flashing);
    }

    public void repaint(Icon icon) {
        int idx = this.indexOfTab(icon);
        if (idx != -1) {
            repaint(getBoundsAt(idx));
        }
    }

    public void mouseReleased(MouseEvent e) {
        int tabIndex = indexAt(e);
        if (tabIndex == -1) return;
        CloseableTabIcon icon = (CloseableTabIcon)this.getIconAt(tabIndex);
        Rectangle r = icon.getBounds();

        if (icon.isOnX((int)(e.getX() - r.getX()), (int)(e.getY() - r.getY()))) {
            removeTabAt(tabIndex);
        }
    }

    private int indexAt(MouseEvent e) {
           return getUI().tabForCoordinate(this, e.getX(), e.getY());
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
        Rectangle r = icon.getBounds();
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
