/*
 * HealthBar.java
 *
 * Created on March 11, 2007, 4:36 PM
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2006  Catherine Fitzpatrick
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
 * @author Catherine
 */
public class HealthBar extends JPanel implements ActionListener {
    
    private static final Image m_image;
    private int m_numerator = 100;
    private int m_denominator = 100;
    private boolean m_fraction;
    private double m_ratio = 1.0;
    private Timer m_timer = new Timer(20, this);

    static {
        m_image = Toolkit
                .getDefaultToolkit()
                .createImage(
                    HealthBar.class.getResource("resources/healthbar.jpg"));
    }
    
    /** Creates a new instance of HealthBar */
    public HealthBar(boolean fraction) {
        m_fraction = fraction;
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(m_image, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {

        }
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                m_fraction = !m_fraction;
                repaint();
            }
        });
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
        g2.setColor(getBackground());
        g2.fillRect(0, 0, width, height);
        double ratio = m_ratio;
        int x = (int)((double)width * ratio);
        g2.drawImage(m_image, 0, 0, x, height, this);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, x - 1, height - 1);
        g2.drawRect(0, 0, width - 1, height - 1);
        g2.setFont(new Font(null, Font.BOLD, 20));
        FontMetrics metrics = g2.getFontMetrics();
        String str;
        //TODO: display preference
        if (!m_fraction) {
            str = (int)(ratio * 100) + "%";
        } else {
            str = m_numerator + "/" + m_denominator;
        }
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
        health.setRatio(6, 48, true);
        frame.getContentPane().add(health);
        health.setSize(frame.getSize());
        health.setVisible(true);
        health.setLocation(0, 0);
        frame.setVisible(true);
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