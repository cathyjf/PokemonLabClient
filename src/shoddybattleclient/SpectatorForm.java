/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SpectatorForm.java
 *
 * Created on Oct 14, 2010, 11:12:10 PM
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
import java.util.EnumMap;
import java.util.Map;
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
    private static final Map<State, Image> m_statusMap
            = new EnumMap<State, Image>(State.class);
    static {
        m_statusMap.put(State.BURNED,
                GameVisualisation.getImageFromResource("status/burn.png"));
        m_statusMap.put(State.FROZEN,
                GameVisualisation.getImageFromResource("status/freeze.png"));
        m_statusMap.put(State.PARALYSED,
                GameVisualisation.getImageFromResource("status/paralyze.png"));
        m_statusMap.put(State.POISONED,
                GameVisualisation.getImageFromResource("status/poison.png"));
        m_statusMap.put(State.SLEEPING,
                GameVisualisation.getImageFromResource("status/sleep.png"));
    }

    private static class MiniIcon extends JPanel {
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
            if (m_lastName == null || !m_pokemon.getName().equals(m_lastName)) {
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
            Image img = m_statusMap.get(m_pokemon.getState());
            if (img != null) {
                g2.drawImage(img, 0, 24, null);
            }
            

            if (m_pokemon.getName() != null) {
                double ratio = (double)m_pokemon.getNumerator() / m_pokemon.getDenominator();
                int height = (int)(ratio * 10);
                if (ratio > 0 && height == 0) {
                    height = 1;
                }
                g2.setColor(new Color(32, 32, 32));
                g2.fillRect(27, 20, 5, 12);
                Color c;
                if (ratio > .5) {
                    c = new Color(0x67e446);
                } else if (ratio > .15) {
                    c = new Color(230, 240, 38);
                } else {
                    c = new Color(219, 43, 15);
                }
                g2.setColor(c);
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
        initComponents();

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

        // We need this in case icons weren't fully loaded. :(
        MediaTracker tracker = new MediaTracker(this);
        for (Image i : m_statusMap.values()) {
            tracker.addImage(i, 0);
        }
        try {
            tracker.waitForAll();
        } catch (Exception e) {

        }

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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
