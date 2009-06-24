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
import javax.swing.*;
import shoddybattleclient.GameVisualisation;

/**
 *
 * @author ben
 */
public class CloseableTabIcon extends Component implements Icon {

    private String m_text;
    private int m_strWidth;
    private static int m_iconH;
    private static int m_iconW;
    private boolean m_highlight = false;

    private final static Image m_x = GameVisualisation.getImageFromResource("x.gif");

    public static final int BUFFER = 6;

    public CloseableTabIcon(JTabbedPane parent, String text, FontMetrics metrics) {
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
        g2.setColor(Color.BLACK);
        g2.drawString(m_text, x, top);
        if (m_highlight) {
            g2.setColor(new Color(200, 200, 200, 200));
            g2.fillOval(x + m_strWidth + BUFFER - 3, top - 10, 13, 13);
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
}
