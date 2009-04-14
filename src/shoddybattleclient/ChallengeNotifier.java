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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author ben
 */
public class ChallengeNotifier extends JComponent {

    private static class Challenge {
        private String m_name;
        //battle id
        private int m_id;
        private boolean m_incoming;
        public Challenge(String name, int id, boolean incoming) {
            m_name = name;
            m_id = id;
            m_incoming = incoming;
        }
        public String getName() {
            return m_name;
        }
        public int getId() {
            return m_id;
        }
        public boolean isIncoming() {
            return m_incoming;
        }
    }

    private static final int OPACITY = 200;
    private static final Color INCOMING_FILL = new Color(255, 230, 120, OPACITY);
    private static final Color INCOMING_BORDER = new Color(210, 200, 90, OPACITY);
    private static final Color OUTGOING_FILL = new Color(230, 220, 210, OPACITY);
    private static final Color OUTGOING_BORDER = new Color(150, 150, 150, OPACITY);
    private static final int CELL_HEIGHT = 23;


    private LobbyWindow m_parent;
    private List<Challenge> m_challenges = new ArrayList<Challenge>();
    private BufferedImage m_img;

    public ChallengeNotifier(LobbyWindow parent) {
        m_parent = parent;
        try {
            m_img = ImageIO.read(new File("resources/cancel.gif"));
        } catch (Exception e) {
            System.out.println("Could not load icon");
        }
    }


    public synchronized void addChallenge(String challenger, int id, boolean incoming) {
        m_challenges.add(new Challenge(challenger, id, incoming));
        setVisible(true);
        repaint();
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

    private int getY(int idx) {
        return (int) (m_parent.getChatBounds().getY() + (idx * CELL_HEIGHT)) + 1;
    }

    public int getW() {
        return (int) m_parent.getChatBounds().getWidth() - 3;
    }

    @Override
    protected void paintComponent(Graphics g) {
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
            g.setColor(bg);
            g.fillRect(x, y, w, CELL_HEIGHT);
            g.setColor(border);
            g.drawRect(x, y, w, CELL_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawString(msg, x + 25,  y + (3 * CELL_HEIGHT / 4));

            g.drawImage(m_img, x + 2, y + 5, this);
        }
    }

    public void processClick(Point point) {
        int w = getW();
        for (int i = 0; i < m_challenges.size(); i++) {
            Challenge c = m_challenges.get(i);
            int y = getY(i);
            Rectangle fullRect = new Rectangle(0, y, w, CELL_HEIGHT);
            Rectangle imgRect = new Rectangle(2, y + 5, m_img.getWidth(), m_img.getHeight());
            if (imgRect.contains(point)) {
                removeChallenge(c.getId());
                return;
            } else if (fullRect.contains(point)) {
                System.out.println(c.getName() + " " + c.getId());
            }
        }
    }

    @Override
    public boolean contains(Point p) {
        return false;
    }
}
