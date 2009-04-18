/*
 * BattleWindow.java
 *
 * Created on Apr 7, 2009, 11:51:16 PM
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import shoddybattleclient.GameVisualisation.VisualPokemon;
import shoddybattleclient.shoddybattle.*;
import shoddybattleclient.utils.*;

/**
 *
 * @author ben
 */
public class BattleWindow extends javax.swing.JFrame {

    private static class Move {
        private String m_name;
        private int m_pp;
        private int m_maxPp;
        private String m_type;
        public Move(String name, String type, int maxPp) {
            m_name = name;
            m_type = type;
            m_maxPp = maxPp;
            m_pp = maxPp;
        }
        public String getName() {
            return m_name;
        }
        public String getType() {
            return m_type;
        }
        public String getPp() {
            return m_pp + "/" + m_maxPp;
        }
    }

    private static class MoveButton extends JToggleButton {
        private Move m_move = null;
        public MoveButton() {
            setFocusPainted(false);
        }
        public void setMove(Move move) {
            m_move = move;
            repaint();
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (m_move == null) return;
            Graphics g2 = g.create();
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            g2.drawString(m_move.getName(), 10, 20);
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.drawString(m_move.getType(), 10, 50);
            g2.drawString(m_move.getPp(), 110, 50);
            g2.dispose();
        }
    }

    private static class SwitchButton extends JToggleButton {
        public SwitchButton(String text) {
            super(text);
            this.setFocusPainted(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics g2 = g.create();
            String text = getText();
            setText(null);
            super.paintComponent(g2);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            g2.drawString(text, 10, 20);
            setText(text);
            g2.dispose();
        }
    }

    private MoveButton[] m_moves;
    private SwitchButton[] m_switches;
    private GameVisualisation m_visual;
    private HealthBar[] m_healthBars = new HealthBar[2];
    private HTMLPane m_chat;
    private ArrayList<PokemonMove> m_moveList;

    /** Creates new form BattleWindow */
    public BattleWindow() {
        initComponents();

        m_chat = new HTMLPane("Ben");
        scrollChat.add(m_chat);
        scrollChat.setViewportView(m_chat);

        MoveListParser mlp = new MoveListParser();
        m_moveList = mlp.parseDocument(BattleWindow.class.getResource("resources/moves2.xml").toString());

        m_moves = new MoveButton[4];
        panelMoves.setLayout(new GridLayout(2, 2));

        ButtonGroup moveButtons = new ButtonGroup();
        for (int i = 0; i < m_moves.length; i++) {
            MoveButton button = new MoveButton();
            moveButtons.add(button);
            m_moves[i] = button;
            panelMoves.add(button);
        }

        ButtonGroup switchButtons = new ButtonGroup();
        panelSwitch.setLayout(new GridLayout(3, 2));
        m_switches = new SwitchButton[6];
        for (int i = 0; i < m_switches.length; i++) {
            SwitchButton button = new SwitchButton(String.valueOf(i));
            switchButtons.add(button);
            m_switches[i] = button;
            panelSwitch.add(button);
        }
        m_visual = new GameVisualisation(0);
        m_visual.setSize(m_visual.getPreferredSize());
        m_visual.setLocation(20, 60);
        m_healthBars[0] = new HealthBar();
        m_healthBars[0].setLocation(20, 20);
        m_healthBars[0].setSize(m_visual.getWidth(), 35);
        m_healthBars[1] = new HealthBar();
        m_healthBars[1].setLocation(20, 65 + m_visual.getHeight());
        m_healthBars[1].setSize(m_visual.getWidth(), 35);
        add(m_healthBars[0]);
        add(m_healthBars[1]);
        add(m_visual);
    }

    public void setParties(VisualPokemon[] p1, VisualPokemon[] p2) {
        m_visual.setParties(p1, p2);
    }

    public void setMove(int idx, String name) {
        if ((idx < 0) || (idx >= m_moves.length)) return;
        for (PokemonMove move : m_moveList) {
            if (move.name.equals(name)) {
                m_moves[idx].setMove(new Move(name, move.type, move.pp));
                break;
            }
        }
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        panelMoves = new javax.swing.JPanel();
        btnMove = new javax.swing.JButton();
        btnMoveCancel = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        panelSwitch = new javax.swing.JPanel();
        btnSwitch = new javax.swing.JButton();
        btnSwitchCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        scrollChat = new javax.swing.JScrollPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

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

        jPanel3.setOpaque(false);

        panelMoves.setOpaque(false);

        org.jdesktop.layout.GroupLayout panelMovesLayout = new org.jdesktop.layout.GroupLayout(panelMoves);
        panelMoves.setLayout(panelMovesLayout);
        panelMovesLayout.setHorizontalGroup(
            panelMovesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 319, Short.MAX_VALUE)
        );
        panelMovesLayout.setVerticalGroup(
            panelMovesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 117, Short.MAX_VALUE)
        );

        btnMove.setText("Choose");

        btnMoveCancel.setText("Cancel");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(btnMove, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(btnMoveCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
            .add(panelMoves, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(panelMoves, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnMove)
                    .add(btnMoveCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {btnMove, btnMoveCancel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jTabbedPane1.addTab("Move", jPanel3);

        jPanel5.setOpaque(false);

        panelSwitch.setOpaque(false);

        org.jdesktop.layout.GroupLayout panelSwitchLayout = new org.jdesktop.layout.GroupLayout(panelSwitch);
        panelSwitch.setLayout(panelSwitchLayout);
        panelSwitchLayout.setHorizontalGroup(
            panelSwitchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 319, Short.MAX_VALUE)
        );
        panelSwitchLayout.setVerticalGroup(
            panelSwitchLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 117, Short.MAX_VALUE)
        );

        btnSwitch.setText("Switch");

        btnSwitchCancel.setText("Cancel");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(btnSwitch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 165, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(btnSwitchCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
            .add(panelSwitch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .add(panelSwitch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnSwitch)
                    .add(btnSwitchCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Switch", jPanel5);

        jScrollPane1.setViewportView(jList1);

        jLabel1.setText("Player 1");

        jLabel2.setText("Player 2");

        jLabel3.setText("20:00:00");

        jLabel4.setText("20:00:00");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(29, 29, 29)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 340, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(scrollChat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .add(txtChat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel3))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jLabel1, jLabel2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {jLabel3, jLabel4}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jLabel1))
                        .add(6, 6, 6)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(scrollChat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtChat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 216, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtChatFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtChatFocusGained

}//GEN-LAST:event_txtChatFocusGained

    private void txtChatFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtChatFocusLost

    }//GEN-LAST:event_txtChatFocusLost

    private void txtChatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtChatKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            m_chat.addMessage("Ben", txtChat.getText());
            txtChat.setText("");
        }
    }//GEN-LAST:event_txtChatKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int result = JOptionPane.showConfirmDialog(this, "Leaving will cause you " +
                "to forfeit this battle. Are you sure you want to leave?",
                "Leaving Battle", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception e) {
                    
                }
                BattleWindow battle = new BattleWindow();
                VisualPokemon[] party1 = new VisualPokemon[] {
                    new VisualPokemon("Squirtle", 0, false)/*,
                    new VisualPokemon("Ivysaur", 0, true)*/
                };
                VisualPokemon[] party2 = new VisualPokemon[] {
                    new VisualPokemon("Blissey", 1, false)/*,
                    new VisualPokemon("Wobbuffet", 1, true)*/
                };
                battle.setParties(party1, party2);

                battle.setMove(0, "Surf");
                battle.setMove(1, "Tackle");
                battle.setMove(2, "Withdraw");
                battle.setMove(3, "Hydro Pump");

                battle.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMove;
    private javax.swing.JButton btnMoveCancel;
    private javax.swing.JButton btnSwitch;
    private javax.swing.JButton btnSwitchCancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel panelMoves;
    private javax.swing.JPanel panelSwitch;
    private javax.swing.JScrollPane scrollChat;
    private javax.swing.JTextField txtChat;
    // End of variables declaration//GEN-END:variables

}
