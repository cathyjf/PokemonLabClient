/* CloseableTabIcon.java
 *
 * Created on Saturday June 20, 2009, 6:51 PM
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import shoddybattleclient.GameVisualisation;

/**
 *
 * @author ben
 */
public class CloseableTabIcon implements Icon, ActionListener {

    private static final Color COLOR_NORMAL = Color.BLACK;
    private static final Color COLOR_FLASH = Color.RED;

    private CloseableTabbedPane m_parent;
    private String m_text;
    private int m_strWidth;
    private static int m_iconH;
    private static int m_iconW;
    private boolean m_highlight = false;
    private Timer m_timer = new Timer(500, this);
    private Color m_textColour = COLOR_NORMAL;

    private final static Image m_x = GameVisualisation.getImageFromResource("x.gif");

    public static final int BUFFER = 6;

    public CloseableTabIcon(CloseableTabbedPane parent, String text, FontMetrics metrics) {
        m_parent = parent;
        m_text = text;
        m_strWidth = metrics.stringWidth(text);

        MediaTracker tracker = new MediaTracker(parent);
        tracker.addImage(m_x, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        m_iconH = 7;
        m_iconW = 7;
    }

    public void setHighlight(boolean highlight) {
        m_highlight = highlight;
    }

    public boolean isOnX(int x, int y) {
        int min = m_strWidth + BUFFER + 10;
        if ((x < min) || (x > (min + 15))) return false;
        if ((y < 0) || (y > 20)) return false;
        return true;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        final int top = y + 7;
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(m_textColour);
        g2.drawString(m_text, x, top);
        if (m_highlight) {
            int l = x + m_strWidth + BUFFER - 3;
            int t = top - 10;
            int r = 12;
            g2.setColor(new Color(235, 235, 235));
            g2.fillOval(l, t, r, r);
            g2.setColor(new Color(200, 200, 200));
            g2.drawOval(l, t, r, r);
        }
        g2.drawImage(m_x, x + m_strWidth + BUFFER, top - 7, c);
        g2.dispose();
    }

    public int getIconWidth() {
        return m_strWidth + BUFFER + m_iconW;
    }

    public int getIconHeight() {
        return m_iconH;
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
