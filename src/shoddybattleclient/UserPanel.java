/*
 * UserPanel.java
 *
 * Created on May 12, 2009, 1:48:11 PM
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

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import shoddybattleclient.network.ServerLink;
import shoddybattleclient.network.ServerLink.ChallengeMediator;
import shoddybattleclient.network.ServerLink.MessageListener;
import shoddybattleclient.shoddybattle.Pokemon;
import shoddybattleclient.shoddybattle.Pokemon.Gender;
import shoddybattleclient.shoddybattle.PokemonSpecies;
import shoddybattleclient.utils.ClauseList;
import shoddybattleclient.utils.ClauseList.ClauseListModel;
import shoddybattleclient.utils.CloseableTabbedPane.CloseableTab;
import shoddybattleclient.utils.HTMLPane;
import shoddybattleclient.utils.TeamFileParser;
import shoddybattleclient.utils.Text;

/**
 *
 * @author ben
 */
public class UserPanel extends javax.swing.JPanel implements CloseableTab, MessageListener {

    public static class TeamBox extends JPanel {
        public TeamBox() {
            reset();
        }

        private void reset() {
            this.removeAll();
            for (int i = 0; i < 6; i++) {
                this.add(new SpritePanel(null, -1, null, false));
            }
        }

        /**
         * Initialises the sprites from a team file and returns the team
         */
        public Pokemon[] loadFromTeam(String file, List<PokemonSpecies> speciesList) {
            TeamFileParser tfp = new TeamFileParser();
            Pokemon[] team = null;
            try {
                team = tfp.parseTeam(file);
            } catch (Exception e) {
                return null;
            }
            if (team != null) {
                this.removeAll();
                this.repaint();
                for (int i = 0; i < team.length; i++) {
                    Pokemon p = team[i];
                    SpritePanel panel = new SpritePanel(p.species,
                            PokemonSpecies.getIdFromName(speciesList, p.species), p.gender, p.shiny);
                    this.add(panel);
                }
            }
            return team;
        }
    }

    private static class SpritePanel extends JPanel {
        private Image m_image;
        public SpritePanel(String species, int speciesId, Gender g, boolean shiny) {
            setBorder(BorderFactory.createEtchedBorder());
            m_image = GameVisualisation.getSprite(speciesId, true,
                    !Gender.GENDER_FEMALE.equals(g), shiny);
            if (m_image == null) return;
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(m_image, 0);
            try {
                tracker.waitForAll();
            } catch (Exception e) {

            }
            setToolTipText(species);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(40, 40);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(m_image, 0, 0, this);
        }
    }

    private final ServerLink m_link;
    private String m_opponent;
    private int m_idx;
    private Pokemon[] m_team = null;
    private boolean m_incoming = false;
    private int m_n;
    private int m_generation;
    private boolean m_waiting = false;

    /** Creates new form UserPanel */
    public UserPanel(String name, ServerLink link, int index) {
        initComponents();
        lblName.setText(name);
        m_opponent = name;
        m_link = link;
        m_idx = index;
        m_link.addMessageListener(this);
        m_link.requestUserMessage(name);
        listClauses.setModel(new ClauseListModel(m_link.getClauseList()));
    }

    public void setPersonalMessage(String msg) {
        if ("".equals(msg)) {
            msg = Text.getText(26, 5);
        }
        msg = "<html>" + HTMLPane.htmlEntityEncode(msg) + "</html>";
        lblMessage.setText(msg);
    }

    public void setIncoming() {
        btnChallenge.setText("Accept");
        cmbRules.setEnabled(false);
        cmbN.setEnabled(false);
        cmbGen.setEnabled(false);
        m_incoming = true;
    }

    public void setOptions(int n, int generation) {
        if (m_waiting) return;
        m_n = n;
        m_generation = generation;
        cmbN.setSelectedIndex(n - 1);
        cmbGen.setSelectedIndex(generation);
    }

    public ChallengeMediator getMediator() {
        return new ChallengeMediator() {
            public Pokemon[] getTeam() {
                return m_team;
            }
            public void informResolved(boolean accepted) {
                close();
            }
            public String getOpponent() {
                return m_opponent;
            }
            public int getGeneration() {
                return m_generation;
            }
            public int getActivePartySize() {
                return m_n;
            }
        };
    }

    public void close() {
        m_link.getLobby().closeTab(m_idx);
    }

    public boolean informClosed() {
        boolean close = true;
        if (m_waiting) {
            close = JOptionPane.showConfirmDialog(this,
                    "Are you sure you wish to cancel your challenge?", "Cancel Challenge",
                     JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        } else {
            if (m_incoming) {
                m_link.resolveChallenge(m_opponent, false, null);
            }
            close = true;
        }
        if (!close) return false;
        m_link.removeMessageListener(this);
        return true;
    }

    public String getOpponent() {
        return m_opponent;
    }

    @Override
    public void informMessageRecevied(String user, String msg) {
        if (!user.equals(m_opponent)) return;
        setPersonalMessage(msg);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbRules = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbN = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cmbGen = new javax.swing.JComboBox();
        panelSprites = new TeamBox();
        btnLoad = new javax.swing.JButton();
        btnChallenge = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listClauses = new ClauseList();
        chkTimed = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        txtTimerPool = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTimerPeriods = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtTimerLength = new javax.swing.JTextField();

        setOpaque(false);

        jPanel1.setOpaque(false);

        lblName.setFont(new java.awt.Font("Lucida Grande", 1, 16));
        lblName.setText("bearzly");

        lblMessage.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        lblMessage.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel3.setText("Rankings:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblName)
                    .add(jLabel3)
                    .add(lblMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(lblName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblMessage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addContainerGap(121, Short.MAX_VALUE))
        );

        jPanel2.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel1.setText("Rules:");

        cmbRules.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard", "Ubers", "Custom..." }));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel2.setText("Pokemon per side:");

        cmbN.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6" }));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel4.setText("Generation:");

        cmbGen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "D/P", "Platinum", "Platinum Fake" }));

        panelSprites.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelSprites.setOpaque(false);
        panelSprites.setLayout(new java.awt.GridLayout(2, 3));

        btnLoad.setText("Load");
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        btnChallenge.setText("Challenge");
        btnChallenge.setEnabled(false);
        btnChallenge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChallengeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                        .add(btnLoad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnChallenge, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, panelSprites, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbGen, 0, 113, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbRules, 0, 151, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(166, 166, 166))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbRules))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cmbGen))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelSprites, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnLoad)
                    .add(btnChallenge))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Basic", jPanel2);

        jPanel3.setOpaque(false);

        jScrollPane1.setViewportView(listClauses);

        chkTimed.setSelected(true);
        chkTimed.setText("Timed Battle?");
        chkTimed.setEnabled(false);

        jLabel5.setText("Initial Pool:");

        txtTimerPool.setText("300");
        txtTimerPool.setEnabled(false);

        jLabel6.setText("Periods:");

        txtTimerPeriods.setText("3");
        txtTimerPeriods.setEnabled(false);

        jLabel7.setText("Period Length:");

        txtTimerLength.setText("30");
        txtTimerLength.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkTimed)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, 0, 0, Short.MAX_VALUE)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel3Layout.createSequentialGroup()
                                        .add(jLabel5)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(txtTimerPool, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jPanel3Layout.createSequentialGroup()
                                        .add(jLabel7)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(txtTimerLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtTimerPeriods, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(chkTimed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtTimerPool, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(txtTimerPeriods, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(txtTimerLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Advanced", jPanel3);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 266, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnChallengeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChallengeActionPerformed
        if (m_team == null) return;
        if (m_incoming) {
            m_link.resolveChallenge(m_opponent, true, m_team);
        } else {
            m_link.postChallenge(new ChallengeMediator() {
                public Pokemon[] getTeam() {
                    return m_team;
                }
                public void informResolved(boolean accepted) {
                    if (accepted) {
                        m_link.postChallengeTeam(m_opponent, m_team);
                    } else {
                        // todo: internationalisation
                        JOptionPane.showMessageDialog(UserPanel.this,
                                m_opponent + " rejected the challenge.");
                        m_waiting = false;
                        close();
                    }
                    m_waiting = false;
                }
                public String getOpponent() {
                    return m_opponent;
                }
                public int getGeneration() {
                    return cmbGen.getSelectedIndex();
                }
                public int getActivePartySize() {
                    return Integer.parseInt((String)cmbN.getSelectedItem());
                }
            });
            btnChallenge.setEnabled(false);
            btnLoad.setEnabled(false);
            m_waiting = true;
        }
    }//GEN-LAST:event_btnChallengeActionPerformed

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        FileDialog fd = new FileDialog(m_link.getLobby(), "Choose a team to load", FileDialog.LOAD);
        fd.setVisible(true);
        if (fd.getFile() == null) return;
        String file = fd.getDirectory() + fd.getFile();
        TeamBox box = (TeamBox)panelSprites;
        Pokemon[] team = box.loadFromTeam(file, m_link.getSpeciesList());
        if (team != null) {
            m_team = team;
            btnChallenge.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Selected team file could not be loaded");
            if (m_team == null) btnChallenge.setEnabled(false);
        }
    }//GEN-LAST:event_btnLoadActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChallenge;
    private javax.swing.JButton btnLoad;
    private javax.swing.JCheckBox chkTimed;
    private javax.swing.JComboBox cmbGen;
    private javax.swing.JComboBox cmbN;
    private javax.swing.JComboBox cmbRules;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblName;
    private javax.swing.JList listClauses;
    private javax.swing.JPanel panelSprites;
    private javax.swing.JTextField txtTimerLength;
    private javax.swing.JTextField txtTimerPeriods;
    private javax.swing.JTextField txtTimerPool;
    // End of variables declaration//GEN-END:variables

}
