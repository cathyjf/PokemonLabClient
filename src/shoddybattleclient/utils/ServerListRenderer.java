/*
 * ServerListRenderer.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import shoddybattleclient.WelcomeWindow.ServerListEntry;

/**
 *
 * @author ben
 */
public class ServerListRenderer extends JPanel implements ListCellRenderer {
    private ServerListEntry m_sle;

    public Component getListCellRendererComponent(JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        
        m_sle = (ServerListEntry)value;
        this.setOpaque(true);
        this.setPreferredSize(new Dimension(0, 40));
        Color background;
        Color foreground;
        if (isSelected) {
            background = list.getSelectionBackground();
            foreground = list.getSelectionForeground();
        } else {
            background = list.getBackground();
            foreground = list.getForeground();
        }
        this.setBackground(background);
        this.setForeground(foreground);

        return this;
    }

    public void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        //initial left margin
        int left = 5;
        //draw background
        g.setColor(getBackground());
        g.fillRect(0, 0, w, h);
        //draw the server name and description
        g.setColor(Color.BLACK);
        g.setFont(getFont().deriveFont(18f).deriveFont(Font.BOLD));
        g.drawString(m_sle.getName(), left, h / 2);
        int nameRight = left + g.getFontMetrics().stringWidth(m_sle.getName());
        g.setFont(g.getFont().deriveFont(10f).deriveFont(Font.PLAIN));
        g.drawString(m_sle.getDescription(), nameRight + 10, h / 2);

        //draw host, port users
        g.setColor(Color.DARK_GRAY);
        int hostPortHeight = h / 2 + 15;
        String host = "Host: " + m_sle.getHost();
        int hostRight = left + g.getFontMetrics().stringWidth(host);
        g.drawString(host, left, hostPortHeight);
        String port = "Port: " + m_sle.getPort();
        g.drawString(port, hostRight + 5, hostPortHeight);
        int portRight = hostRight + g.getFontMetrics().stringWidth(port);
        String users = "[" + m_sle.getUsers() + "/" + m_sle.getMaxUsers() + "]";
        g.drawString(users, portRight + 15, hostPortHeight);
    }

}
