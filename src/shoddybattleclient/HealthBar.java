/*
 * HealthBar.java
 *
 * Created on March 11, 2007, 4:36 PM
 *
 * This file is a part of Shoddy Battle 2.
 * Copyright (C) 2010  Catherine Fitzpatrick and Benjamin Gwin
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
 */

package shoddybattleclient;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Ben
 */
public class HealthBar extends JPanel implements ActionListener {
    
    private static final Color GREY1 = new Color(230, 230, 230);
    private static final Color GREY2 = new Color(180, 180, 180);
    private static final Color[] GREYS = new Color[] {GREY1, GREY2};
    private static final Color GREEN1 = new Color(42, 222, 67);
    private static final Color GREEN2 = new Color(36, 173, 52);
    private static final Color[] GREENS = new Color[] {GREEN1, GREEN2};
    private static final Color YELLOW1 = new Color(230, 240, 38);
    private static final Color YELLOW2 = new Color(158, 158, 17);
    private static final Color[] YELLOWS = new Color[] {YELLOW1, YELLOW2};
    private static final Color RED1 = new Color(219, 43, 15);
    private static final Color RED2 = new Color(163, 21, 21);
    private static final Color[] REDS = new Color[] {RED1, RED2};
    private static final Color FONT_SHADOW = new Color(110, 110, 110);
    private static final Color FONT_COLOUR = new Color(40, 40, 40);

    private int m_numerator = 100;
    private int m_denominator = 100;
    private boolean m_fraction;
    private double m_ratio = 1.0;
    private Timer m_timer = new Timer(20, this);
    
    /** Creates a new instance of HealthBar */
    public HealthBar(boolean fraction) {
        m_fraction = fraction;
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                m_fraction = !m_fraction;
                repaint();
            }
        });
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    public void setFraction(boolean fraction) {
        m_fraction = fraction;
        repaint();
    }
    
    public void setRatio(int numerator, int denominator, boolean animate) {
        if (!m_timer.isRunning()) {
            m_ratio = getRatio();
        }
        if (numerator < 0) numerator = 0;
        m_numerator = numerator;
        m_denominator = denominator;
        if (animate) {
            m_timer.start();
        } else {
            m_ratio = getRatio();
        }
        repaint();
    }

    public void setRatio(int num, int denom) {
        setRatio(num, denom, true);
    }

    private double getRatio() {
        double ratio = (double)m_numerator / m_denominator;
        if ((ratio > 0) && ((int)(ratio * 100.0) == 0)) {
            ratio = 0.01;
        }
        return ratio;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        double ratio = m_ratio;
        Color[] colours = (m_ratio > 0.5) ? GREENS : (m_ratio > 0.15) ? YELLOWS : REDS;
        int x = (int)((double)width * ratio);
        g2.setPaint(new GradientPaint(width/2, 0, GREYS[0], width/2, height, GREYS[1]));
        g2.fillRect(0, 0, width, height);
        g2.setPaint(new GradientPaint(width/2, 0, colours[0], width/2, height, colours[1]));
        g2.fillRect(0, 0, x, height);
        g2.setPaint(colours[1]);
        g2.fillRect(x, 0, 1, height);
        g2.setFont(new Font(null, Font.BOLD, height * 3 / 5));
        FontMetrics metrics = g2.getFontMetrics();
        String str;
        if (!m_fraction) {
            str = (int)(ratio * 100) + "%";
        } else {
            str = m_numerator + "/" + m_denominator;
        }
        /*g2.setPaint(FONT_SHADOW);
        g2.drawString(str,
                (width - metrics.stringWidth(str)) / 2 + 1,
                (height - metrics.getDescent() + metrics.getAscent()) / 2 + 1);*/
        g2.setPaint(FONT_COLOUR);
        g2.drawString(str,
                (width - metrics.stringWidth(str)) / 2,
                (height - metrics.getDescent() + metrics.getAscent()) / 2);
        g2.dispose();
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Testing HealthBar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(230, 70);
        final HealthBar health = new HealthBar(true);
        health.setRatio(48, 48, false);
        frame.getContentPane().add(health);
        health.setSize(frame.getSize());
        health.setVisible(true);
        health.setLocation(0, 0);
        frame.setVisible(true);
        new Timer(100, new ActionListener() {
            int max = 48;
            int current = max;
            @Override
            public void actionPerformed(ActionEvent e) {
                health.setRatio(current, max, false);
                current -= 1;
                if (current < 0) {
                    health.setRatio(max, max, false);
                    current = 48;
                }
            }

        }).start();
    }

    public void actionPerformed(ActionEvent arg0) {
        double ratio = getRatio();
        if (m_ratio > ratio) {
            m_ratio -= 0.02;
            if (m_ratio < ratio) m_ratio = ratio;
        } else if (m_ratio < ratio) {
            m_ratio += 0.02;
            if (m_ratio > ratio) m_ratio = ratio;
        } else {
            m_timer.stop();
        }
        repaint();
    }
    
}