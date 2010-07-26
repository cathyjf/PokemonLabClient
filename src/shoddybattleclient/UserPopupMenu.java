/*
 * UserPopupMenu.java
 *
 * Created on May 22, 2009, 11:38:23 PM
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
import shoddybattleclient.LobbyWindow.User;

/**
 * Context menu provided when a user's name is right clicked
 * @author ben
 */
public class UserPopupMenu extends JPopupMenu {
    final LobbyWindow m_lobby;
    final User m_user;
    public UserPopupMenu(LobbyWindow lobby, User u, int level) {
        m_lobby = lobby;
        m_user = u;
        JMenuItem chal = new JMenuItem("Challenge");
        chal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.openUserPanel(m_user.getName());
            }

        });
        JMenuItem bat = new JMenuItem("View Battle");
        JMenuItem kick = new JMenuItem("Kick");
        kick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().sendBanMessage(
                        m_lobby.getActiveChannel(), m_user.getName(), 0);
            }

        });
        JMenuItem ban = new JMenuItem("Ban");
        ban.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BanDialog bd = new BanDialog(m_user.getName());
                bd.setVisible(true);
                long length = bd.getBanLength();
                int channel = (bd.isGlobal()) ? -1 : m_lobby.getActiveChannel();
                if (length > 0) {
                    m_lobby.getLink().sendBanMessage(channel, m_user.getName(), length);
                }
            }

        });
        JMenuItem mute = new JMenuItem("Mute");
        mute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), m_user.getName(), 3, true);
            }

        });
        JMenuItem unmute = new JMenuItem("Unmute");
        unmute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), m_user.getName(), 3, false);
            }

        });
        JMenuItem sop = new JMenuItem("Add SOp");
        sop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), m_user.getName(), 0, true);
            }

        });
        JMenuItem op = new JMenuItem("Add Op");
        op.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), m_user.getName(), 1, true);
            }

        });
        JMenuItem removeOp = new JMenuItem("Remove Op");
        removeOp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), m_user.getName(), 1, false);
            }

        });
        JMenuItem voice = new JMenuItem("Add Voice");
        voice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), m_user.getName(), 2, true);
            }

        });
        JMenuItem removeVoice = new JMenuItem("Remove Voice");
        removeVoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().updateMode(m_lobby.getActiveChannel(), m_user.getName(), 2, false);
            }

        });
        JMenuItem lookup = new JMenuItem("Lookup");
        lookup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m_lobby.getLink().requestUserLookup(m_user.getName());
            }

        });
        
        this.add(chal);
        //todo: decide what to do about battles
        this.add(bat);
        //change this to enable moderator functions for unauthorized users
        if (level > 1) {
            this.addSeparator();
            this.add(kick);
            this.add(ban);
            if (!m_user.hasMute()) {
                this.add(mute);
            }
            else {
                this.add(unmute);
            }
            this.add(lookup);
        }
        if (level > 2) {
            this.addSeparator();
            if (m_user.getLevel() < 3) {
                this.add(sop);
            }
            //todo: determine if sops should be able to remove sops
            //maybe we need a channel owner
            if (m_user.getLevel() < 2) {
                this.add(op);
            }
            else if (m_user.getLevel() == 2) {
                this.add(removeOp);
            }
            if (m_user.getLevel() < 1) {
                this.add(voice);
            }
            else if (m_user.getLevel() == 1) {
                this.add(removeVoice);
            }
        }
    }

}
