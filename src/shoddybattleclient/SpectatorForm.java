/* Spectator.java
 *
 * Created October 19, 2010
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import shoddybattleclient.GameVisualisation.State;
import shoddybattleclient.GameVisualisation.VisualPokemon;
import shoddybattleclient.utils.JCustomTooltip;

/**
 *
 * @author davidpeter
 */
public class SpectatorForm extends JPanel {
    private static class MiniIcon extends JPanel {
        private static final Color GREEN  = new Color(0x67e446);
        private static final Color YELLOW = new Color(230, 240, 38);
        private static final Color RED    = new Color(219, 43, 15);

        private VisualPokemon m_pokemon;
        private Image m_image;
        private boolean m_fainted = false;
        private String m_lastName = null;
        
        public MiniIcon(VisualPokemon p) {
            setOpaque(false);
            m_pokemon = p;
            updateIcon();
            setToolTipText("asdf");
            repaint();
        }

        public void updateIcon() {
            String name = m_pokemon.getName();
            if ((m_lastName == null) || !name.equals(m_lastName)) {
                m_lastName = m_pokemon.getName();
                m_image = m_pokemon.getIcon();
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(m_image, 0);
                try {
                    tracker.waitForAll();
                } catch (Exception e) {

                }
            }
        }

        public Dimension getPreferredSize() {
            return new Dimension(32, 32);
        }
        private BufferedImage createBufferedImage(Image img) {
            BufferedImage buf = new BufferedImage(32, 32,
                BufferedImage.TYPE_INT_ARGB);
            Graphics g = buf.createGraphics();
            g.drawImage(m_image, 0, 0, null);
            g.dispose();
            return buf;
        }
        public void paintComponent(Graphics g) {
            if (m_fainted) {
                g.drawImage(m_image, 0, 0, null);
                return;
            }
            updateIcon();
            Graphics2D g2 = (Graphics2D)g.create();
            if (m_pokemon.getState() == State.FAINTED) {
                BufferedImage img = createBufferedImage(m_image);
                RescaleOp rop = new RescaleOp(1f, -128f, null);
                ColorConvertOp cop = new ColorConvertOp(
                        ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                img = rop.filter(img, img);
                img = cop.filter(img, img);
                m_fainted = true;
                m_image = img;
                g2.drawImage(img, 0, 0, null);
                g2.dispose();
                return;
            }

            g2.drawImage(m_image, 0, 0, null);
            Image img = m_pokemon.getState().getImage();
            if (img != null) {
                g2.drawImage(img, 0, 24, null);
            }
            
            if (m_pokemon.getName() != null) {
                double frac = (double)m_pokemon.getNumerator() /
                        m_pokemon.getDenominator();
                int height = (int)(frac * 10);
                if (frac > 0 && height == 0) {
                    height = 1;
                }
                g2.setColor(new Color(32, 32, 32));
                g2.fillRect(27, 20, 5, 12);
                Color color = (frac > 0.5) ? GREEN
                        : (frac > 0.15) ? YELLOW : RED;
                g2.setColor(color);
                g2.fillRect(28, 31 - height, 3, height);
            }
            g2.dispose();
        }
        @Override
        public JToolTip createToolTip() {
            return new JCustomTooltip(this,
                    new VisualToolTip(m_pokemon, true));
        }
    }

    private GameVisualisation m_visual;
    private int m_length;

    private JPanel panelTop;
    private JPanel panelBottom;
    private MiniIcon[][] m_icons;

    /** Creates new form SpectatorForm */
    public SpectatorForm(String player1, String player2,
            GameVisualisation visual, int length) {
        m_visual = visual;
        m_length = length;
        m_icons = new MiniIcon[2][m_length];
        setOpaque(false);

        panelTop = new JPanel();
        panelBottom = new JPanel();

        GridLayout grid = new GridLayout(1, 3);
        setLayout(grid);
        add(panelTop);
        JPanel buffer = new JPanel();
        buffer.setLayout(new BorderLayout());
        buffer.setOpaque(false);
        JLabel label = new JLabel("vs.", JLabel.CENTER);
        buffer.add(label, BorderLayout.NORTH);
        add(buffer);
        add(panelBottom);

        initPanel(player1, panelTop, 0);
        initPanel(player2, panelBottom, 1);

        refresh();
    }

    private void initPanel(String playerName, JPanel panel, int party) {
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(playerName, JLabel.CENTER);
        Font f = label.getFont();
        label.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        panel.add(label, BorderLayout.NORTH);

        JPanel buffer = new JPanel();
        buffer.setOpaque(false);

        // create party grid
        GridLayout grid = new GridLayout((m_length + 1) / 2, 2);
        buffer.setLayout(grid);
        for (int j = 0; j < m_length; j++) {
            VisualPokemon p = m_visual.getPokemon(party, j);
            MiniIcon icon = new MiniIcon(p);
            JPanel inner = new JPanel();
            inner.setOpaque(false);
            inner.add(icon);
            buffer.add(inner);
            m_icons[party][j] = icon;
        }

        // fill in empty spots for odd-numbered parties
        if (m_length % 2 == 1) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            buffer.add(empty);
        }

        // add party grid to wrapper panel
        panel.add(buffer, BorderLayout.CENTER);
    }

    public void refresh() {
    	for (int i = 0; i < m_icons.length; i++) {
            for (int j = 0; j < m_length; j++) {
                m_icons[i][j].repaint();
            }
        }
    }
}
