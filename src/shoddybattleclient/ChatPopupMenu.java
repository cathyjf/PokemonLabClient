/*
 * ChatPopupMenu.java
 *
 * Created on July 15, 2010, 12:09 PM
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

package shoddybattleclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Context menu provided when a channel's chat is right clicked
 * @author justin
 */
public class ChatPopupMenu extends JPopupMenu {
    final LobbyWindow m_lobby;
    public ChatPopupMenu(LobbyWindow lobby, int level) {
        m_lobby = lobby;
        int active = m_lobby.getActiveChannel();
        JMenuItem mute = new JMenuItem("Mute " + m_lobby.getChannelName(active));
        mute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), "", 4, true);
            }

        });
        JMenuItem unmute = new JMenuItem("Unmute " + m_lobby.getChannelName(active));
        unmute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), "", 4, false);
            }

        });
        
        if (level > 1) {
            //Check if channel is muted
            if (!m_lobby.getChannel(active).isMuted()) {
                this.add(mute);
            }
            else {
                this.add(unmute);
            }
        }
    }
}
