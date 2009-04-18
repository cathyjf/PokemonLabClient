/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shoddybattleclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author ben
 */
public class GameVisualisation extends JPanel {

    public static class VisualPokemon {
        private String m_species;
        private int m_gender;
        private boolean m_shiny;
        private List<String> m_statuses = new ArrayList<String>();
        public VisualPokemon(String species, int gender, boolean shiny) {
            m_species = species;
            m_gender = gender;
            m_shiny = shiny;
        }
        public String getSpecies() {
            return m_species;
        }
        public int getGender() {
            return m_gender;
        }
        public boolean isShiny() {
            return m_shiny;
        }
        public void addStatus(String status) {
            m_statuses.add(status);
        }
        public void removeStatus(String status) {
            m_statuses.remove(status);
        }
    }

    private static final Image m_background;
    private static final Image[] m_pokeball = new Image[3];
    private VisualPokemon[][] m_parties = new VisualPokemon[2][];
    private int m_view;
    
    public static Image getImageFromResource(String file) {
        return Toolkit.getDefaultToolkit()
                .createImage(GameVisualisation.class.getResource("resources/" + file));
    }

    static {
        m_background = getImageFromResource("background.jpg");
        m_pokeball[0] = getImageFromResource("pokeball.png");
        m_pokeball[1] = getImageFromResource("pokeball2.png");
        m_pokeball[2] = getImageFromResource("pokeball3.png");
    }
    
    public GameVisualisation(int view) {
        m_view = view;
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(m_background, 0);
        tracker.addImage(m_pokeball[0], 1);
        tracker.addImage(m_pokeball[1], 2);
        tracker.addImage(m_pokeball[2], 3);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setParties(VisualPokemon[] party1, VisualPokemon[] party2) {
        m_parties[0] = party1;
        m_parties[1] = party2;
    }

    public void setPokemon(int party, int order, VisualPokemon p) {
        m_parties[party][order] = p;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(m_background.getWidth(this), m_background.getHeight(this));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.drawImage(m_background, 0, 0, this);
        paintParty(1 - m_view, g2);
        paintParty(m_view, g2);
        g2.dispose();
    }

    private void paintParty(int idx, Graphics g) {
        Graphics g2 = g.create();
        VisualPokemon[] team = m_parties[idx];
        if (team == null) return;
        boolean us = (idx == m_view);
        int n = team.length;
        MediaTracker tracker = new MediaTracker(this);
        for (int i = team.length - 1; i >= 0; i--) {
            VisualPokemon p = team[i];
            Image img = null;
            try {
                img = getSprite(p.getSpecies(), !us, p.getGender() == 0, p.isShiny(), null);
            } catch (IOException e) {
                System.out.println(p.getSpecies() + " sprite not found");
            }
            if (img == null) continue;
            tracker.addImage(img, 0);
            try {
                tracker.waitForAll();
            } catch (InterruptedException e) {

            }
            int h = img.getHeight(this);
            int w = img.getWidth(this);
            int x;
            int y = us ? m_background.getHeight(this) - h : 90 - h;
            if (n == 1) {
                x = us ? 30 : 190 - w / 2;
            } else if (n == 2) {
                x = us ? 70 : 210 - w / 2;
                x -= us ? 70 * i : 50 * i;
            } else {
                //get ugly
                x = us ? 45 * (n - (i + 1)) - 15 : 220 - 45 * (n - (i + 1));
            }
            g2.drawImage(img, x, y, this);
        }
        g2.dispose();
    }

    public static Image getSprite(String name, boolean front, boolean male, 
            boolean shiny, String repository) throws IOException {
        String shininess = shiny ? "shiny" : "normal";
        String prefix = front ? "front" : "back";
        String gender = male ? "m" : "f";
        String path = prefix + shininess + "/" + gender + name.replaceAll("[ '\\.]", "").toLowerCase() + ".png";
        //TODO: change storage location
        String qualified = "/Users/ben/sprites/" + path;
        File f = new File(qualified);
        String[] repositories = new String[] {"http://shoddybattle.com/dpsprites/", repository};
        if (!f.exists()) {
            for (int i = 0; i < repositories.length; i++) {
                URL url = new URL(repositories[i] + path);
                InputStream input;
                try {
                    input = url.openStream();
                } catch (IOException e) {
                    continue;
                }
                f.getParentFile().mkdirs();
                FileOutputStream output = new FileOutputStream(f);
                byte[] bytes = new byte[255];
                while (true) {
                    int read = input.read(bytes);
                    if (read == -1)
                        break;
                    output.write(bytes, 0, read);
                }
                output.flush();
                output.close();
                input.close();
                break;
            }
        }
        return Toolkit.getDefaultToolkit().createImage(qualified);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Visualisation test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameVisualisation vis = new GameVisualisation(0);
        VisualPokemon[] party1 = new VisualPokemon[] {
            new VisualPokemon("Squirtle", 0, false),
            new VisualPokemon("Ivysaur", 0, true),
            new VisualPokemon("Ho-oh", 0, false),
            new VisualPokemon("Tyranitar", 0, false),
            new VisualPokemon("Lugia", 0, false),
            new VisualPokemon("Latios", 0, false)
        };
        VisualPokemon[] party2 = new VisualPokemon[] {
            new VisualPokemon("Blissey", 1, false),
            new VisualPokemon("Wobbuffet", 1, true),
            new VisualPokemon("Blastoise", 0, false),
            new VisualPokemon("Smeargle", 0, false),
            new VisualPokemon("Pikachu", 0, false),
            new VisualPokemon("Umbreon", 0, false)
        };
        vis.setParties(party1, party2);
        Dimension d = vis.getPreferredSize();
        frame.setSize(d.width, d.height + 22);
        vis.setSize(d);
        vis.setLocation(0, 0);
        frame.add(vis);
        frame.setVisible(true);
    }
}
