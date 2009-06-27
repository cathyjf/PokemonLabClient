/*
 * ChatPane.java
 *
 * Created on Apr 5, 2009, 2:13:23 PM
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

import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JScrollPane;
import shoddybattleclient.utils.CloseableTabbedPane.CloseableTab;
import shoddybattleclient.utils.HTMLPane;

/**
 *
 * @author ben
 */
public class ChatPane extends javax.swing.JPanel implements CloseableTab {

    public static class CommandException extends Exception {
        public CommandException(String message) {
            super(message);
        }
    }

    private HTMLPane m_chatPane;
    private LobbyWindow m_lobby;
    private String m_name;
    private LobbyWindow.Channel m_channel;
    private String m_sub;
    private int m_tabCount = 0;

    public LobbyWindow getLobby() {
        return m_lobby;
    }

    /** Creates new form ChatPane */
    public ChatPane(LobbyWindow.Channel c, LobbyWindow lobby, String name) {
        m_channel = c;
        m_lobby = lobby;
        m_name = name;
        initComponents();
        txtChat.setFocusTraversalKeysEnabled(false);
        m_chatPane = new HTMLPane();
        scrollChat.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollChat.add(m_chatPane);
        scrollChat.setViewportView(m_chatPane);
    }

    public LobbyWindow.Channel getChannel() {
        return m_channel;
    }

    private void parseCommand(String command, String args)
            throws CommandException {
        if ("mode".equals(command)) {
            int idx = args.indexOf(' ');
            String action, cmd;
            if (idx == -1) {
                action = args;
                cmd = "";
            } else {
                action = args.substring(0, idx);
                cmd = args.substring(idx + 1);
            }
            parseMode(action.toLowerCase(), cmd);
        }
    }

    private int getMode(char c) {
        switch (c) {
            case 'a':
                return 0;
            case 'o':
                return 1;
            case 'v':
                return 2;
            case 'b':
                return 3;
            case 'm':
                return 4;
            case 'i':
                return 5;
            // todo: idle?
        }
        throw new InternalError();
    }

    private void parseMode(String action, String users) throws CommandException {
        if ("".equals(action) || "help".equals(action)) {
            throw new CommandException(
                    "Usage: /mode +a/o/v/b/m/i [user1[,user2,...]]");
        }
        char char1 = action.charAt(0);
        if ((char1 != '+') && (char1 != '-')) {
            throw new CommandException("Try '/mode help' for usage");
        }
        boolean add = (char1 == '+');
        action = action.substring(1);
        if (action.length() == 1) {
            String user = users;
            char c = action.charAt(0);
            switch (c) {
                case 'a':
                case 'o':
                case 'v':
                case 'b':
                case 'm':
                case 'i':
                    int mode = getMode(c);
                    m_lobby.getLink().updateMode(m_channel.getId(),
                            user, mode, add);
                    break;
                default:
                    throw new CommandException("Invalid command: " + action);

            }
        } else {
            String[] args = users.split(",");
            for (int i = 0; i < action.length(); i++) {
                String user;
                if (i >= args.length) {
                    user = "";
                } else {
                    user = args[i];
                }
                String pm = add ? "+" : "-";
                parseMode(pm + action.substring(i, i + 1), user.trim());
            }
        }
    }

    public void addMessage(String user, String message) {
        addMessage(user, message, true);
    }

    public void addMessage(String user, String message, boolean encode) {
        m_chatPane.addMessage(user, message, encode);
    }

    public void sendMessage(String message) throws CommandException {
        message = message.trim();
        if (message.equals("")) {
            return;
        }

        if (message.indexOf('/') == 0) {
            int idx = message.indexOf(' ');
            String command, args;
            if (idx != -1) {
                command = message.substring(1, idx);
                args = message.substring(idx + 1);
            } else {
                command = message.substring(1);
                args = "";
            }
            parseCommand(command.toLowerCase(), args);
            return;
        }

        m_lobby.getLink().sendChannelMessage(m_channel.getId(), message);
    }

    public JScrollPane getPane() {
        return scrollChat;
    }

    public HTMLPane getChat() {
        return m_chatPane;
    }

    public boolean informClosed() {
        //todo: leave this channel
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtChat = new javax.swing.JTextField();
        scrollChat = new javax.swing.JScrollPane();

        setBackground(new java.awt.Color(244, 242, 242));
        setOpaque(false);

        txtChat.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtChatFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtChatFocusLost(evt);
            }
        });
        txtChat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtChatKeyReleased(evt);
            }
        });

        scrollChat.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtChat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, scrollChat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(scrollChat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtChat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtChatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtChatFocusGained

}//GEN-LAST:event_txtChatFocusGained

    private void txtChatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtChatFocusLost

    }//GEN-LAST:event_txtChatFocusLost

    private void txtChatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtChatKeyReleased
        String msg = txtChat.getText();

        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            if ("".equals(msg)) return;
            int idx = msg.lastIndexOf(" ");
            if (m_tabCount == 0) {
                m_sub = msg.substring(idx + 1);
                if ("".equals(m_sub)) return;
            }
            List<String> names = m_lobby.autocompleteUser(m_sub);
            Collections.sort(names, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            int size = names.size();
            if (size == 0) return;
            int index = m_tabCount;
            while (index >= size) {
                index -= size;
            }
            String newStr = names.get(index);
            if (newStr != null){
                msg = msg.substring(0, idx + 1) + newStr;
                txtChat.setText(msg);
            }
            m_tabCount++;
        } else {
            m_tabCount = 0;
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                try {
                    sendMessage(msg);
                } catch (CommandException e) {
                    addMessage(null, e.getMessage());
                }
                txtChat.setText(null);
            }
        }
    }//GEN-LAST:event_txtChatKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollChat;
    private javax.swing.JTextField txtChat;
    // End of variables declaration//GEN-END:variables

}
