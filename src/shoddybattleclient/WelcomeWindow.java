/*
 * WelcomeWindow.java
 *
 * Created on Apr 4, 2009, 2:26:28 PM
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
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import shoddybattleclient.network.ServerLink;
import shoddybattleclient.utils.*;

/**
 *
 * @author ben
 */
public class WelcomeWindow extends javax.swing.JFrame {

    public static class ServerListEntry {
        private String m_host;
        private int m_port;
        private String m_name;
        private int m_users;
        private int m_maxUsers;
        private String m_desc;
        
        public ServerListEntry(String name, String desc, String host, int port,
                int users, int maxUsers) {
            m_name = name;
            m_desc = desc;
            m_host = host;
            m_port = port;
            m_users = users;
            m_maxUsers = maxUsers;
        }
        public String getName() {
            return m_name;
        }
        public String getHost() {
            return m_host;
        }
        public int getPort() {
            return m_port;
        }
        public int getUsers() {
            return m_users;
        }
        public int getMaxUsers() {
            return m_maxUsers;
        }
        public String getDescription() {
            return m_desc;
        }
        @Override
        public String toString() {
            return m_name;
        }
    }

    /** Creates new form WelcomeWindow */
    public WelcomeWindow() {
        initComponents();
        ServerListEntry sle = new ServerListEntry("Official Server",
                "This server may or may not be gone", "localhost",
                8446, 220, 250);
        ServerListEntry sle2 = new ServerListEntry("Smogon", 
                "Official server of smogon.com", "shoddy.smogon.com",
                50000, 150, 250);
        ServerListEntry sle3 = new ServerListEntry("Pokemonexperte",
                "A german server.", "shoddy.pokemonexperte.com",
                1234, 5, 250);
        lstServers.setModel(new ServerListModel(new ServerListEntry[] {sle,sle2,sle3}));
        lstServers.setCellRenderer(new ServerListRenderer());
    }

    private void connect(int index) {
        ServerListEntry sle =
                (ServerListEntry)lstServers.getModel().getElementAt(index);
        
        //new ServerConnect("test server", "this is a test").setVisible(true);
        ServerLink link;
        try {
            link = new ServerLink(sle.getHost(), sle.getPort());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        link.start();
        dispose();
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        serverListPane = new javax.swing.JScrollPane();
        lstServers = new javax.swing.JList();
        btnConnect = new javax.swing.JButton();
        btnAdvanced = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        cmdTeamBuilder = new javax.swing.JMenuItem();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Shoddy Battle - Server List");
        setLocationByPlatform(true);

        lstServers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstServersMouseClicked(evt);
            }
        });
        serverListPane.setViewportView(lstServers);

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        btnAdvanced.setText("Advanced");
        btnAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdvancedActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");

        jMenu1.setText("File");

        cmdTeamBuilder.setText("Open Team Builder");
        cmdTeamBuilder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdTeamBuilderActionPerformed(evt);
            }
        });
        jMenu1.add(cmdTeamBuilder);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(serverListPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(btnConnect)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnAdvanced)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 109, Short.MAX_VALUE)
                        .add(btnRefresh)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(serverListPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnConnect)
                    .add(btnRefresh)
                    .add(btnAdvanced))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdTeamBuilderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdTeamBuilderActionPerformed
        new TeamBuilder().setVisible(true);
}//GEN-LAST:event_cmdTeamBuilderActionPerformed

    private void lstServersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstServersMouseClicked
        if ((evt.getClickCount() == 2) && (evt.getButton() == MouseEvent.BUTTON1)) {
            int index = lstServers.locationToIndex(evt.getPoint());
            connect(index);
        }
    }//GEN-LAST:event_lstServersMouseClicked

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        connect(lstServers.getSelectedIndex());
}//GEN-LAST:event_btnConnectActionPerformed

    private void btnAdvancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdvancedActionPerformed
        new AdvancedDialog(this).setVisible(true);
}//GEN-LAST:event_btnAdvancedActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } catch (Exception e) {
            
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WelcomeWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdvanced;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JMenuItem cmdTeamBuilder;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JList lstServers;
    private javax.swing.JScrollPane serverListPane;
    // End of variables declaration//GEN-END:variables

}
