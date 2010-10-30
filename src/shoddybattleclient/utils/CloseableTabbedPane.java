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
import java.awt.FlowLayout;
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
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import shoddybattleclient.GameVisualisation;

/**
 *
 * @author ben
 */
public class CloseableTabbedPane extends SlideTabbedPane {

    public interface CloseableTab {
        // Tell a tab that it is closing
        // Return whether it should continue to close
        public boolean informClosed();
    }

    public interface TabCloseListener {
        public void tabClosed(Component c);
    }

    //These variables are used by the CloseableTabComponent
    private static final Image m_x = 
            GameVisualisation.getImageFromResource("x.gif");
    private static final Color COLOR_NORMAL = Color.BLACK;
    private static final Color COLOR_FLASH = Color.RED;

    /**
     * The component used to display the tabs
     */
    private class CloseableTabComponent extends JPanel implements ActionListener {
        private class CloseButton extends JPanel implements MouseListener {
            private boolean m_highlight = false;

            public CloseButton() {
                MediaTracker tracker = new MediaTracker(
                        CloseableTabComponent.this);
                tracker.addImage(m_x, 0);
                try {
                    tracker.waitForAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                this.setPreferredSize(new Dimension(13, 13));

                addMouseListener(this);
            }

            @Override
            public void paint(Graphics g) {
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

            public void mouseClicked(MouseEvent e) {
                removeTabAt(indexOfTabComponent(CloseableTabComponent.this));
            }
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                m_highlight = true;
                CloseableTabComponent.this.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                m_highlight = false;
                CloseableTabComponent.this.repaint();
            }

            public void mouseReleased(MouseEvent e) {
                CloseableTabComponent.this.repaint();
            }
        }

        private Timer m_timer = new Timer(500, this);
        private JLabel m_label;
        private CloseButton m_close;
        private boolean m_closeable;

        public CloseableTabComponent(String text, boolean closeable) {
            m_label = new JLabel(text);
            m_close = new CloseButton();
            m_closeable = closeable;
            setOpaque(false);

            layoutCloseableTab();
        }

        public void setCloseable(boolean closeable) {
            m_closeable = closeable;
            layoutCloseableTab();
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

        private void layoutCloseableTab() {
            this.removeAll();

            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);

            add(m_label);
            if (m_closeable) {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.PAGE_END;
                constraints.insets = new Insets(0, 4, 0, 0);
                layout.setConstraints(m_close, constraints);
                
                add(m_close);
            }
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

    public void addTab(String name, Component c, boolean closeable) {
        this.insertTab(name, null, c, null, true, closeable, getTabCount());
    }

    public void insertTab(String title, Icon icon, Component component,
            String tip, boolean slidable, boolean closeable, int index) {
        super.insertTab(title, icon, component, tip, slidable, index);

        Component tabComponent = new CloseableTabComponent(title, closeable);
        setTabComponentAt(index, tabComponent);
    }

    @Override
    public void insertTab(String title, Icon icon, Component component,
            String tip, boolean slidable, int index) {
        this.insertTab(title, icon, component, tip, slidable, true, index);
    }

    // While setTitleAt works, getTitleAt doesn't. This is because Swing sucks.
    @Override
    public void setTitleAt(int idx, String text) {
        if ((idx < 0) || (idx >= getTabCount())) {
            return;
        }
        
        CloseableTabComponent tab =
                (CloseableTabComponent)getTabComponentAt(idx);
        if (tab != null) {
            tab.setText(text);
            repaint();
        }
    }

    @Override
    public void remove(int idx) {
        if (idx >= getTabCount()) return;
        Component tab = getComponentAt(idx);
        if (tab instanceof CloseableTab) {
            CloseableTab cTab = (CloseableTab)tab;
            if (!cTab.informClosed()) return;
        }
        super.remove(idx);
        for (TabCloseListener lst : m_listeners) {
            lst.tabClosed(tab);
        }
    }

    public void setFlashingAt(int idx, boolean flashing) {
        Component component = getTabComponentAt(idx);
        if (!(component instanceof CloseableTabComponent)) return;
        CloseableTabComponent tab = (CloseableTabComponent)component;
        if (tab != null) {
            tab.setFlashing(flashing);
        }
    }
}