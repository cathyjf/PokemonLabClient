/* ChallengeNotifier.java
 *
 * Created April 10, 2009
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import shoddybattleclient.network.ServerLink;
import shoddybattleclient.network.ServerLink.ChallengeMediator;
import shoddybattleclient.shoddybattle.Pokemon;

/**
 *
 * @author ben
 */
public class ChallengeNotifier extends JComponent {

    public static class Challenge {
        private String m_name;
        private boolean m_incoming;
        private boolean m_popup = false;
        private Pokemon[] m_team = null;
        private int m_generation = 0;
        private int m_n = 0;

        public Challenge(String name, boolean incoming, int generation, int n) {
            m_name = name;
            m_incoming = incoming;
            m_generation = generation;
            m_n = n;
        }
        public void setTeam(Pokemon[] team) {
            m_team = team;
        }
        public String getName() {
            return m_name;
        }
        public int getId() {
            return hashCode();
        }
        public boolean isIncoming() {
            return m_incoming;
        }
        public boolean popupActive() {
            return m_popup;
        }
        public void setPopup(boolean active) {
            m_popup = active;
        }

        private ChallengeMediator getMediator() {
            return new ChallengeMediator() {
                public Pokemon[] getTeam() {
                    return m_team;
                }
                public void informResolved(boolean accepted) {
                    return;
                }
                public String getOpponent() {
                    return m_name;
                }
                public int getGeneration() {
                    return m_generation;
                }
                public int getActivePartySize() {
                    return m_n;
                }
            };
        }
    }

    private static final int OPACITY = 200;
    private static final Color INCOMING_FILL = new Color(255, 230, 120, OPACITY);
    private static final Color INCOMING_BORDER = new Color(210, 200, 90, OPACITY);
    private static final Color OUTGOING_FILL = new Color(230, 220, 210, OPACITY);
    private static final Color OUTGOING_BORDER = new Color(180, 180, 180, OPACITY);
    private static final int CELL_HEIGHT = 23;


    private LobbyWindow m_parent;
    private List<Challenge> m_challenges = new ArrayList<Challenge>();
    private Image m_img;
    private PopupFactory m_factory = PopupFactory.getSharedInstance();

    public ChallengeNotifier(LobbyWindow parent) {
        m_parent = parent;
        try {
            m_img = Toolkit.getDefaultToolkit()
                    .createImage(ChallengeNotifier.class.getResource("resources/cancel.gif"));
        } catch (Exception e) {
            System.out.println("Could not load icon");
        }
    }


    public synchronized void addChallenge(String challenger, boolean incoming,
            int generation, int n) {
        m_challenges.add(new Challenge(challenger, incoming, generation, n));
        setVisible(true);
        repaint();
    }

    public synchronized void removeChallenge(String name) {
        for (Challenge c : m_challenges) {
            if (c.getName().equals(name)) {
                removeChallenge(c.getId());
                return;
            }
        }
    }

    public synchronized void removeChallenge(int id) {
        for (Challenge c : m_challenges) {
            if (c.getId() == id) {
                m_challenges.remove(c);
                break;
            }
        }
        if (m_challenges.isEmpty()) {
            setVisible(false);
        } else {
            repaint();
        }
    }

    public ServerLink.ChallengeMediator getMediator(String name) {
        for (Challenge c : m_challenges) {
            if (c.getName().equals(name)) {
                return c.getMediator();
            }
        }
        return null;
    }

    private int getY(int idx) {
        return (int) (m_parent.getChatBounds().getY() + (idx * CELL_HEIGHT)) + 1;
    }

    public int getW() {
        return (int) m_parent.getChatBounds().getWidth() - 3;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        Rectangle r = m_parent.getChatBounds();
        int x = (int) r.getX() + 1;
        int w = getW();

        for (int i = 0; i < m_challenges.size(); i++) {
            Challenge c = m_challenges.get(i);
            Color bg, border;
            String msg;
            if (c.isIncoming()) {
                bg = INCOMING_FILL;
                border = INCOMING_BORDER;
                msg = c.getName() + " wants to battle!";
            } else {
                bg = OUTGOING_FILL;
                border = OUTGOING_BORDER;
                msg = "Pending request to " + c.getName();
            }
            int y = getY(i);
            if (y > r.getHeight()) return;
            //g.setColor(bg);
            int delta = 15;
            Color bg2 = new Color(bg.getRed() - delta, bg.getGreen() - delta
                    , bg.getBlue() - delta, bg.getAlpha());
            GradientPaint gp = new GradientPaint((float)(w/2), (float)y, bg2, (float)(w/2), (float)(y+CELL_HEIGHT/2), bg, true);
            g2.setPaint(gp);
            g2.fillRect(x, y, w, CELL_HEIGHT);
            g2.setColor(border);
            g2.drawRect(x, y, w, CELL_HEIGHT);
            g2.setColor(Color.BLACK);
            g2.drawString(msg, x + 25,  y + (3 * CELL_HEIGHT / 4));
            g2.drawImage(m_img, x + 2, y + 5, this);
        }
        g2.dispose();
    }

    /**
     * Gets a new popup instance at a specified point relative to the chat
     */
    private void createPopup(Challenge c, Point point) {
        if (c.popupActive()) return;
        c.setPopup(true);
        SwingUtilities.convertPointToScreen(point, m_parent.getChat().getChat());
         ChallengePanel cp = new ChallengePanel(m_parent, c);
         Popup p = m_factory.getPopup(m_parent, cp, point.x, point.y);
         cp.setPopup(p);
         p.show();
    }

    public void processClick(MouseEvent e) {
        Point point = e.getPoint();
        int w = getW();
        for (int i = 0; i < m_challenges.size(); i++) {
            Challenge c = m_challenges.get(i);
            int y = i * CELL_HEIGHT;
            Rectangle fullRect = new Rectangle(0, y, w, CELL_HEIGHT);
            Rectangle imgRect = new Rectangle(2, y + 5, m_img.getWidth(this), m_img.getHeight(this));
            if (imgRect.contains(point)) {
                //clicked the close icon
                removeChallenge(c.getId());
                return;
            } else if (fullRect.contains(point)) {
                //clicked somewhere else on the notification
                createPopup(c, point);
            }
        }
    }

    @Override
    public boolean contains(Point p) {
        return false;
    }

    public static void main(String[] args) {
        //new LobbyWindow("Ben").setVisible(true);
    }
}
