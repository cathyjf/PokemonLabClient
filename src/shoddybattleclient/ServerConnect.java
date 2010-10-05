/*
 * ServerConnect.java
 *
 * Created on Apr 4, 2009, 2:41:15 PM
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
import java.util.Date;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import shoddybattleclient.LobbyWindow.Channel;
import shoddybattleclient.network.ServerLink;
import shoddybattleclient.shoddybattle.Generation;

/**
 *
 * @author ben
 */
public class ServerConnect extends javax.swing.JFrame {

    private ServerLink m_link;

    /** Creates new form ServerConnect */
    public ServerConnect(ServerLink link, String name, String message,
            boolean canRegister, String loginMessage,
            String registrationMessage) {
        initComponents();
        m_link = link;
        lblName.setText(name);
        String server = link.getHostPort();
        String userName = Preference.getRememberMeUserName(server);
        String password = Preference.getRememberMePassword(server);
        txtLoginName.setText(userName);
        txtLoginPassword.setText(password);
        if ((userName != null) && (userName.length() > 0)) {
            chkRememberMe.setSelected(true);
        }
        txtLoginName.requestFocus();
        setHtmlMessage(txtWelcome, message);
        txtLoginInfo.setText(loginMessage);
        setHtmlMessage(txtRegisterInfo, registrationMessage);

        // TODO: temporary
        link.loadGeneration(Generation.loadGeneration());

        tabbedPane.removeTabAt(canRegister ? 2 : 1);
    }

    private static void setHtmlMessage(JEditorPane pane, String message) {
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet css = new StyleSheet();
        css.addRule("a {color: blue; text-decoration: underline;}");
        kit.setStyleSheet(css);
        HyperlinkListener listener = new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    LobbyWindow.viewWebPage(e.getURL());
                }
            }
        };

        pane.setEditorKit(kit);
        pane.addHyperlinkListener(listener);
        message = "<p>" + message;
        message = message.replaceAll("\n", "</p><p>");
        message += "</p>";
        HTMLDocument doc = (HTMLDocument)pane.getDocument();
        try {
            kit.insertHTML(doc, 0, message, 0, 0, null);
        } catch (Exception e) {
            //really should not be here
        }
        pane.setCaretPosition(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtWelcome = new javax.swing.JEditorPane();
        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtLoginName = new javax.swing.JTextField();
        cmdLogIn = new javax.swing.JButton();
        txtLoginPassword = new javax.swing.JPasswordField();
        txtLoginInfo = new javax.swing.JLabel();
        chkRememberMe = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtRegisterName = new javax.swing.JTextField();
        cmdRegister = new javax.swing.JButton();
        txtRegisterPassword = new javax.swing.JPasswordField();
        txtRegisterConfirm = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtRegisterInfo = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationByPlatform(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblName.setFont(new java.awt.Font("Lucida Grande", 1, 24));
        lblName.setText("Server Name");

        txtWelcome.setContentType("text/html");
        txtWelcome.setEditable(false);
        jScrollPane1.setViewportView(txtWelcome);

        jPanel1.setBackground(new java.awt.Color(244, 242, 242));
        jPanel1.setOpaque(false);

        jLabel2.setText("Username:");

        jLabel3.setText("Password:");

        txtLoginName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLoginNameKeyPressed(evt);
            }
        });

        cmdLogIn.setText("Log In");
        cmdLogIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLogInActionPerformed(evt);
            }
        });

        txtLoginPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLoginPasswordKeyPressed(evt);
            }
        });

        txtLoginInfo.setText("Enter your user name and password to login.");

        chkRememberMe.setText("Remember me?");
        chkRememberMe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRememberMeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(txtLoginName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .add(txtLoginPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)))
                    .add(txtLoginInfo)
                    .add(chkRememberMe)
                    .add(cmdLogIn))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtLoginInfo)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(txtLoginName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtLoginPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkRememberMe)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmdLogIn)
                .add(16, 16, 16))
        );

        tabbedPane.addTab("Log In", jPanel1);

        jPanel2.setBackground(new java.awt.Color(244, 242, 242));
        jPanel2.setOpaque(false);

        jLabel4.setText("Username:");

        jLabel5.setText("Password:");

        txtRegisterName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRegisterNameKeyPressed(evt);
            }
        });

        cmdRegister.setText("Register");
        cmdRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRegisterActionPerformed(evt);
            }
        });

        txtRegisterPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRegisterPasswordKeyPressed(evt);
            }
        });

        txtRegisterConfirm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRegisterConfirmKeyPressed(evt);
            }
        });

        jLabel1.setText("Confirm:");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel5)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtRegisterPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtRegisterName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .add(txtRegisterConfirm, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)))
                    .add(cmdRegister))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(txtRegisterName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtRegisterPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtRegisterConfirm, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cmdRegister)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Register", jPanel2);

        txtRegisterInfo.setContentType("text/html");
        txtRegisterInfo.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtRegisterInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtRegisterInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Register", jPanel3);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .add(lblName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(lblName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
       m_link.close();
       new WelcomeWindow().setVisible(true);
       this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void cmdLogInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdLogInActionPerformed
        String user = txtLoginName.getText().trim();
        // todo: collapse whitespace?
        String password = new String(txtLoginPassword.getPassword()).trim();
        m_link.attemptAuthentication(user, password);
        String server = m_link.getHostPort();
        if (chkRememberMe.isSelected()) {
            Preference.setRememberMe(server, user, password);
        } else {
            Preference.setRememberMe(server, "", "");
        }
}//GEN-LAST:event_cmdLogInActionPerformed

    public void informSuccessfulLogin() {
        dispose();
        new LobbyWindow(m_link, txtLoginName.getText().trim()).setVisible(true);
        m_link.joinChannel("main"); // join #main - the main chat
    }
    
    public static void informUserBanned(String explanation) {
        String message = "You have been banned from this channel until ";
        message += Channel.DATE_FORMATTER.format(
                new Date(Long.valueOf(explanation) * 1000));
        JOptionPane.showMessageDialog(null, message);
    }

    public void informNameUnavailable() {
        // todo: internationalisation
        JOptionPane.showMessageDialog(this, "The user name you have requested "
                + "is already taken on the server. "
                + "Please try a different name.");
    }

    public void informFailedChallenge() {
        // todo: internationalisation
        JOptionPane.showMessageDialog(this,
                "Error: Invalid password."
            );
    }
    
    public void informNonexistentAccount() {
        // todo: internationalisation
        JOptionPane.showMessageDialog(this,
                "Error: No such user account exists on the server.");
    }

    public void informInvalidName() {
        // todo: internationalisation
        String message = "Error: User names may contain only the letters, "
                + "the numbers, the underscore, the hyphen, the dot, "
                + "and the space.";
        JOptionPane.showMessageDialog(this, message);
    }

    public void informNameTooLong() {
        // todo: internationalisation
        JOptionPane.showMessageDialog(this,
                "Error: User name must be shorter than 19 characters.");
    }

    public void informAlreadyLoggedIn() {
        JOptionPane.showMessageDialog(this, "Error: You are already logged onto this server.");
    }
    
    public void informRegisterSuccess() {
        // todo: internationalisation
        JOptionPane.showMessageDialog(this,
                "Successfully registered the account!");
        txtRegisterPassword.setText(null);
        txtRegisterConfirm.setText(null);
        String name = txtRegisterName.getText().trim();
        // todo: collapse whitespace...
        txtRegisterName.setText(null);
        // set it to the login tab and put the user's name in
        tabbedPane.setSelectedIndex(0);
        txtLoginName.setText(name);
        txtLoginPassword.setText(null);
    }

    private void cmdRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRegisterActionPerformed

        String pass1 = new String(txtRegisterPassword.getPassword()).trim();
        String pass2 = new String(txtRegisterConfirm.getPassword()).trim();

        if (!pass1.equals(pass2)) {
            // todo: internationalisation
            JOptionPane.showMessageDialog(this,
                    "Error: Passwords do not match.");
            return;
        }

        String user = txtRegisterName.getText().trim();
        // todo: collapse whitespace...
        m_link.registerAccount(user, pass1);
    }//GEN-LAST:event_cmdRegisterActionPerformed

    private void txtLoginPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLoginPasswordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cmdLogInActionPerformed(null);
        }
    }//GEN-LAST:event_txtLoginPasswordKeyPressed

    private void txtLoginNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLoginNameKeyPressed
        txtLoginPasswordKeyPressed(evt);
    }//GEN-LAST:event_txtLoginNameKeyPressed

    private void txtRegisterNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRegisterNameKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            cmdRegisterActionPerformed(null);
        }
    }//GEN-LAST:event_txtRegisterNameKeyPressed

    private void txtRegisterPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRegisterPasswordKeyPressed
        txtRegisterNameKeyPressed(evt);
    }//GEN-LAST:event_txtRegisterPasswordKeyPressed

    private void txtRegisterConfirmKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRegisterConfirmKeyPressed
        txtRegisterNameKeyPressed(evt);
    }//GEN-LAST:event_txtRegisterConfirmKeyPressed

    private void chkRememberMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRememberMeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkRememberMeActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) throws Exception {
        new ServerLink("localhost", 8446).start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkRememberMe;
    private javax.swing.JButton cmdLogIn;
    private javax.swing.JButton cmdRegister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblName;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JLabel txtLoginInfo;
    private javax.swing.JTextField txtLoginName;
    private javax.swing.JPasswordField txtLoginPassword;
    private javax.swing.JPasswordField txtRegisterConfirm;
    private javax.swing.JEditorPane txtRegisterInfo;
    private javax.swing.JTextField txtRegisterName;
    private javax.swing.JPasswordField txtRegisterPassword;
    private javax.swing.JEditorPane txtWelcome;
    // End of variables declaration//GEN-END:variables

}
