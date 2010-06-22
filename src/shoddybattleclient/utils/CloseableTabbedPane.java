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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import shoddybattleclient.GameVisualisation;

/**
 *
 * @author ben
 */
public class CloseableTabbedPane extends JTabbedPane {

    public interface CloseableTab {
        // Tell a tab that it is closing
        // Return whether it should continue to close
        public boolean informClosed();
    }

    public interface TabCloseListener {
        public void tabClosed(Component c);
    }

    //These variables are used by the CloseableTabComponent
    private static final Image m_x = GameVisualisation.getImageFromResource("x.gif");
    private static final Color COLOR_NORMAL = Color.BLACK;
    private static final Color COLOR_FLASH = Color.RED;

    /**
     * The component used to display the tabs
     */
    private class CloseableTabComponent extends JPanel implements ActionListener {
        private class CloseButton extends JButton implements MouseListener {
            private boolean m_highlight = false;

            public CloseButton() {
                MediaTracker tracker = new MediaTracker(CloseableTabComponent.this);
                tracker.addImage(m_x, 0);
                try {
                    tracker.waitForAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                this.setPreferredSize(new Dimension(13, 13));

                addMouseListener(this);
                setAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        removeTabAt(indexOfTabComponent(CloseableTabComponent.this));
                    }
                });
            }

            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                if (m_highlight) {
                    int r = 12;
                    g2.setColor(new Color(235, 235, 235));
                    g2.fillOval(0, 0, r, r);
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawOval(0, 0, r, r);
                }
                g2.drawImage(m_x, (13 - m_x.getWidth(null))/2,
                                            (13 - m_x.getHeight(null))/2, this);
            }

            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                m_highlight = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                m_highlight = false;
                repaint();
            }
        }

        private Timer m_timer = new Timer(500, this);
        private JLabel m_label;
        private CloseButton m_close;

        public CloseableTabComponent(String text) {

            m_label = new JLabel(text);
            m_close = new CloseButton();

            setOpaque(false);
            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.PAGE_END;
            constraints.insets = new Insets(0, 4, 0, 0);
            layout.setConstraints(m_close, constraints);

            add(m_label);
            add(m_close);
        }

        public void setFlashing(boolean flashing) {
            if (flashing) {
                m_timer.start();
            } else {
                m_label.setForeground(COLOR_NORMAL);
                m_timer.stop();
                repaint();
            }
        }

        public void setText(String text) {
            m_label.setText(text);
        }

        public void actionPerformed(ActionEvent e) {
            Color colour = (m_label.getForeground() == COLOR_NORMAL) ?
                    COLOR_FLASH : COLOR_NORMAL;
            m_label.setForeground(colour);
        }
    }

    private java.util.List<TabCloseListener> m_listeners = new ArrayList<TabCloseListener>();

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
        super.addTab(name, c);
        if (closeable)
            setTabComponentAt(getTabCount() - 1, new CloseableTabComponent(name));
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
        Component component = getTabComponentAt(idx);
        if (!(component instanceof CloseableTabComponent)) return;
        CloseableTabComponent tab = (CloseableTabComponent)component;
        if (tab != null)
            tab.setFlashing(flashing);
    }
}
