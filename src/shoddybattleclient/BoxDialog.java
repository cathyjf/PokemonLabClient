/* BoxDialog.java
 *
 * Created on Sunday May 31, 2010, 8:58 PM
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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import shoddybattleclient.shoddybattle.Pokemon;
import shoddybattleclient.utils.TeamFileParser;

/**
 * A dialog to manage pokemon and boxes.
 * Files are case insensitive for best cross-platform effects.
 * @author Carlos
 */
public class BoxDialog extends javax.swing.JDialog {

    //This is needed for proper list display
    private class PokemonWrapper implements Comparable<PokemonWrapper> {
        public String name;
        public Pokemon pokemon;
        public PokemonWrapper(String pokemonName, Pokemon poke) {
            name = pokemonName;
            pokemon = poke;
        }
        @Override
        public String toString() {
            return name;
        }
        @Override
        public int compareTo(PokemonWrapper o) {
            return name.compareToIgnoreCase(o.name);
        }
    }

    //TODO: Consider this box with BoxTreeModel.Box
    //Calling methods on this Box modifies the hard drive
    private class Box implements Comparable<Box> {
        
        private String m_name;
        private ArrayList<PokemonWrapper> m_pokemon;

        public Box(String name) {
            m_name = name;
            m_pokemon = new ArrayList<PokemonWrapper>();
            
            //Create the box itself
            File boxDir = new File(getBoxPath());
            if(!boxDir.exists())
                boxDir.mkdirs();

            //Read in all the pokemon in this box
            for(File pokeFile : boxDir.listFiles()) {
                if(pokeFile.isDirectory())
                    continue;
                try {
                    TeamFileParser tfp = new TeamFileParser();
                    Pokemon poke = tfp.parseTeam(pokeFile.getAbsolutePath())[0];
                    m_pokemon.add(new PokemonWrapper(pokeFile.getName(), poke));
                }
                catch(Exception ex) {}
            }
            Collections.sort(m_pokemon);
            clearDuplicates(m_pokemon);
        }

        //This creates a new file if it doesn't exist
        public void addPokemon(String name, Pokemon pokemon) throws IOException {
            addPokemon(new PokemonWrapper(name, pokemon));
        }

        public void addPokemon(PokemonWrapper wrapper) throws IOException {
            StringBuffer buf = new StringBuffer();
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            buf.append(wrapper.pokemon.toXML());

            File pokemonPath = new File(getBoxPath() + "/" + wrapper.name);
            Writer writer = new PrintWriter(new FileWriter(pokemonPath));
            writer.write(new String(buf));
            writer.flush();
            writer.close();

            //Insert the pokemon into its sorted position
            int i = 0;
            for(i = 0; i < getPokemonCount(); i++) {
                int compare = wrapper.compareTo(m_pokemon.get(i));
                if(compare < 0) {
                    break;
                } else if(compare == 0) {
                    m_pokemon.set(i, wrapper);
                    return;
                }
            }
            m_pokemon.add(i, wrapper);
        }

        //If the pokemon doesn't exist, it does nothing
        public void removePokemon(String name) {
            for(int i = 0; i < getPokemonCount(); i++) {
                PokemonWrapper wrapper = getPokemonAt(i);
                if(wrapper.name.equalsIgnoreCase(name)) {
                    removePokemonAt(i);
                }
            }
        }

        public void removePokemonAt(int index) {
            PokemonWrapper wrapper = getPokemonAt(index);
            new File(getBoxPath() + "/" + wrapper.name).delete();
            m_pokemon.remove(index);
        }

        public PokemonWrapper getPokemonAt(int idx) {
            return m_pokemon.get(idx);
        }

        public Pokemon getPokemon(String name) {
            PokemonWrapper wrapper = getPokemonWrapper(name);
            if(wrapper == null)
                return null;
            return wrapper.pokemon;
        }

        //This has a use if we want a case-sensitive name
        public PokemonWrapper getPokemonWrapper(String name) {
            //With the right comparator its O(logn), but I don't think its worth it
            for(PokemonWrapper wrapper : m_pokemon) {
                if(wrapper.name.equalsIgnoreCase(name))
                    return wrapper;
            }
            return null;
        }

        public int getPokemonCount() {
            return m_pokemon.size();
        }

        public String getName() {
            return m_name;
        }

        public String getBoxPath() {
            return Preference.getBoxLocation() + File.separatorChar + getName();
        }

        @Override
        public int compareTo(Box o) {
            return getName().compareToIgnoreCase(o.getName());
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private class BoxListModel extends AbstractListModel {

        //TreeSets are a major performance liability come getElementAt()
        List<Box> m_boxes = new ArrayList<Box>();

        public void addBoxes(List<Box> boxes) {
            m_boxes.addAll(boxes);
            Collections.sort(m_boxes);
            clearDuplicates(m_boxes);
            fireListChanged();
        }

        public void addBox(Box box) {
            //This is faster for already sorted lists
            int i = 0;
            for(i = 0; i < getSize(); i++) {
                int compare = box.compareTo(m_boxes.get(i));
                if(compare < 0) {
                    break;
                } else if(compare == 0) {
                    m_boxes.set(i, box);
                    fireListChanged();
                    return;
                }
            }
            m_boxes.add(i, box);
            fireListChanged();
        }

        //Let the list know that we added a box
        public void fireListChanged() {
            ListDataEvent evt = new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, 0, m_boxes.size());
            for (ListDataListener listener : getListDataListeners()) {
                listener.contentsChanged(evt);
            }
        }

        public Box getBox(String name) {
            for(Box box : m_boxes) {
                if(box.getName().equalsIgnoreCase(name))
                    return box;
            }
            return null;
        }

        public void removeBox(Box box) {
            m_boxes.remove(box);
        }

        @Override
        public Object getElementAt(int index) {
            return m_boxes.get(index);
        }

        @Override
        public int getSize() {
            return m_boxes.size();
        }
    }

    private class PokemonListModel extends AbstractListModel {

        Box owner;

        public void setBox(Box box) {
            owner = box;
            fireListChanged();
        }

        public void fireListChanged() {
            ListDataEvent evt = new ListDataEvent(owner,
                    ListDataEvent.CONTENTS_CHANGED, 0, getSize());
            for (ListDataListener listener : getListDataListeners()) {
                listener.contentsChanged(evt);
            }
        }

        @Override
        public Object getElementAt(int index) {
            return owner.getPokemonAt(index);
        }

        @Override
        public int getSize() {
            if(owner == null)
                return 0;
            return owner.getPokemonCount();
        }
    }

    class PokemonListRenderer extends JLabel implements ListCellRenderer {
        public PokemonListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Box box = (Box)listBoxes.getSelectedValue();
            PokemonWrapper wrapper = box.getPokemonAt(index);
            Pokemon poke = wrapper.pokemon;

            if(isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setText(value.toString());

            boolean showMale = true;
            if(poke.gender == Pokemon.Gender.GENDER_FEMALE) showMale = false;

            try {
                java.awt.Image img = GameVisualisation.getSprite(
                    teamBuilder.getSpecies(poke.toString()).getId(), true, showMale, poke.shiny);
                img = img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(img);
                setIcon(icon);
            } catch (Exception ex) {}

            return this;
        }
    }

    private BoxListModel boxModel;
    private PokemonListModel pokemonModel;
    private TeamBuilder teamBuilder;

    /** Creates new form BoxDialog */
    public BoxDialog(TeamBuilder parent) {
        initComponents();
        teamBuilder = parent;

        pokemonModel = new PokemonListModel();
        listPokemon.setModel(pokemonModel);
        listPokemon.setCellRenderer(new PokemonListRenderer());

        File boxDir = new File(Preference.getBoxLocation());
        boxModel = new BoxListModel();
        ArrayList<Box> boxes = new ArrayList<Box>();
        if(boxDir.exists()) {
            for(File boxFile : boxDir.listFiles()) {
                if(!boxFile.isDirectory())
                    continue;
                boxes.add(new Box(boxFile.getName()));
            }
        }
        boxModel.addBoxes(boxes);
        listBoxes.setModel(boxModel);

        listBoxes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listPokemon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setVisible(true);
    }

    //On *nix systems, both Box and Pokemon loading allow for duplicates on
    //first load. This clears those duplicates from the list.
    //This list MUST be sorted before using this method
    private void clearDuplicates(List<? extends Comparable> list) {
        //Sorted lists allow us to do this in O(n)
        Iterator<? extends Comparable> iter = list.iterator();
        Comparable previous = null;
        while(iter.hasNext()) {
            Comparable current = iter.next();

            //this only happens once, but it makes the code cleaner
            if(previous == null) {
                previous = current;
                continue;
            }

            //FIXME: Find a way to do this without the compiler throwing a hissy fit
            if(previous.compareTo(current) == 0)
                iter.remove();
            previous = current;
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

        popupMenu = new javax.swing.JPopupMenu();
        menuRename = new javax.swing.JMenuItem();
        menuDelete = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        listBoxes = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        listPokemon = new javax.swing.JList()  {
            //TODO: Make a better Pokemon view implementation
            PokemonWrapper lastPoke;
            @Override
            public String getToolTipText(java.awt.event.MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());

                if(index < 0)
                return null;

                Rectangle r = this.getCellBounds(index, index);
                if(!r.contains(evt.getX(), evt.getY())) return null;

                PokemonWrapper poke = (PokemonWrapper)pokemonModel.getElementAt(index);
                StringBuffer buffer = new StringBuffer();
                buffer.append("<html>");
                buffer.append(poke.pokemon.
                    toTeamText().replaceAll("\n", "<br>"));
                return new String(buffer);
            }

            @Override
            public Point getToolTipLocation(java.awt.event.MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());

                if(index < 0) {
                    lastPoke = null;
                    return null;
                }

                PokemonWrapper poke = (PokemonWrapper)pokemonModel.getElementAt(index);
                if(poke == lastPoke)
                return null;

                lastPoke = poke;
                return new Point(evt.getX(), evt.getY() + 25);
            }
            /* Flickers too much, left here if anyone wants to deal with it
            long lastShow = 0;
            @Override
            public Point getToolTipLocation(java.awt.event.MouseEvent evt) {
                long currentTime = System.currentTimeMillis();
                if((currentTime - lastShow) > 50) {
                    lastShow = currentTime;
                    return new Point(evt.getX() + 15, evt.getY() + 15);
                } else
                return null;
            }*/
        };
        btnNewBox = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        menuRename.setText("Rename");
        menuRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRenameActionPerformed(evt);
            }
        });
        popupMenu.add(menuRename);

        menuDelete.setText("Delete");
        menuDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteActionPerformed(evt);
            }
        });
        popupMenu.add(menuDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        listBoxes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                listMouseEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listMouseEvent(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseEvent(evt);
            }
        });
        listBoxes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listBoxesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listBoxes);

        listPokemon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                listMouseEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listMouseEvent(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listMouseEvent(evt);
            }
        });
        jScrollPane2.setViewportView(listPokemon);

        btnNewBox.setText("New Box");
        btnNewBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewBoxActionPerformed(evt);
            }
        });

        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNewBox)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btnImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExport))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewBox)
                    .addComponent(btnImport)
                    .addComponent(btnExport))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewBoxActionPerformed
        String boxName = JOptionPane.showInputDialog(this, "New box name:");

        if(boxName == null)
            return;
        if(boxModel.getBox(boxName) != null) {
            JOptionPane.showMessageDialog(this, "This box already exists", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Box newBox = new Box(boxName);
            boxModel.addBox(newBox);
            listBoxes.setSelectedValue(newBox, true);
        } catch(SecurityException ex) {
            JOptionPane.showMessageDialog(this, "Permission denied", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error making box", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnNewBoxActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        Box box = (Box)listBoxes.getSelectedValue();

        if(box == null)
            return;
        
        String name = JOptionPane.showInputDialog(this, "New Pokemon's name:");

        if(name == null || name.trim().equals(""))
            return;

        if(box.getPokemon(name) != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "This Pokemon already exists, are " +
                    "you sure you want to replace it?", "", JOptionPane.YES_NO_OPTION);
            if(confirm != JOptionPane.YES_OPTION)
                return;
        }

        try {
            name = name.trim();
            box.removePokemon(name); //May be a rename
            PokemonWrapper newPoke = new PokemonWrapper(name, teamBuilder.getSelectedPokemon());
            box.addPokemon(newPoke);

            pokemonModel.fireListChanged();
            listPokemon.setSelectedValue(newPoke, true);
        } catch(SecurityException ex) {
            JOptionPane.showMessageDialog(this, "Permission denied", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding pokemon", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnImportActionPerformed

    private void listBoxesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listBoxesValueChanged
        Box box = (Box)listBoxes.getSelectedValue();
        pokemonModel.setBox(box);
    }//GEN-LAST:event_listBoxesValueChanged

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        PokemonWrapper wrapper = (PokemonWrapper)listPokemon.getSelectedValue();

        if(wrapper == null)
            return;

        teamBuilder.setSelectedPokemon(wrapper.pokemon);
    }//GEN-LAST:event_btnExportActionPerformed

    private void listMouseEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseEvent
        if(evt.isPopupTrigger()) {
            JList list = (JList)evt.getSource();
            int index = list.locationToIndex(new Point(evt.getX(), evt.getY()));

            if(index != -1) {
                Rectangle r = list.getCellBounds(index, index);
                if(!r.contains(evt.getX(), evt.getY())) return;
                list.setSelectedIndex(index);
                popupMenu.show(list, evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_listMouseEvent

    private void menuRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRenameActionPerformed
        //This method could apply to both listBoxes and listPokemon
        JList list = (JList)popupMenu.getInvoker();

        Object item = list.getSelectedValue();
        String newName = JOptionPane.showInputDialog(this, "New name for "+item.toString()+":");
        if(newName == null || newName.trim().equals(""))
            return;

        File oldFile;
        File newFile;
        if(list == listBoxes) {
            Box current = (Box)item;
            Box previous = boxModel.getBox(newName); //exists for renames

            //Duplicates are fine if we're merely changing the case
            if(previous != null && previous != current) {
                JOptionPane.showMessageDialog(this, "A box with this name already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            oldFile = new File(current.getBoxPath());
            newFile = new File(Preference.getBoxLocation() + "/" + newName);
            
            try {
                oldFile.renameTo(newFile);

                Box newBox = new Box(newName);
                boxModel.removeBox(current);
                boxModel.addBox(newBox);
                list.setSelectedValue(newBox, true);
            } catch(SecurityException ex) {
                JOptionPane.showMessageDialog(this, "Permission denied", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error renaming box", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Box box = (Box)listBoxes.getSelectedValue();
            PokemonWrapper current = (PokemonWrapper)item;
            PokemonWrapper previous = box.getPokemonWrapper(newName); //exists for renames

            //Duplicates are fine if we're merely changing the case
            if(previous != null && previous.pokemon != current.pokemon) {
                JOptionPane.showMessageDialog(this, "A pokemon with this name already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                PokemonWrapper newPoke = new PokemonWrapper(newName, current.pokemon);
                box.removePokemon(current.name);
                box.addPokemon(newPoke);

                pokemonModel.fireListChanged();
                list.setSelectedValue(newPoke, true);
            } catch(SecurityException ex) {
                JOptionPane.showMessageDialog(this, "Permission denied", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error renaming pokemon", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_menuRenameActionPerformed

    private void menuDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeleteActionPerformed
        //This method could apply to both listBoxes and listPokemon
        JList list = (JList)popupMenu.getInvoker();

        Object item = list.getSelectedValue();
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want " +
                " to delete " + item.toString() + "?", "", JOptionPane.YES_NO_OPTION);

        if(option != JOptionPane.YES_OPTION)
            return;

        if(list == listBoxes) {
            Box box = (Box)item;
            try {
                File boxFile = new File(box.getBoxPath());
                //While we delete remaining files later, this helps in case there is an exception
                //If there is a "problem Pokemon", we need the display to update properly after all
                while(box.getPokemonCount() != 0) {
                    box.removePokemonAt(box.getPokemonCount()-1);
                }

                //If the user has rigged it with duplicates, files may remain
                //FIXME: Consider ill placed non-empty sub directories
                for(File file : boxFile.listFiles()) {
                    file.delete();
                }

                boxModel.removeBox(box);
                boxFile.delete();
            } catch(SecurityException ex) {
                JOptionPane.showMessageDialog(this, "Permission denied", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting box", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            boxModel.fireListChanged();

        } else {
            Box box = (Box)listBoxes.getSelectedValue();
            PokemonWrapper wrapper = (PokemonWrapper)item;

            try {
                box.removePokemon(wrapper.name);
            } catch(SecurityException ex) {
                JOptionPane.showMessageDialog(this, "Permission denied", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting pokemon", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        //This always changes no matter what is deleted
        pokemonModel.fireListChanged();
    }//GEN-LAST:event_menuDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnNewBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList listBoxes;
    private javax.swing.JList listPokemon;
    private javax.swing.JMenuItem menuDelete;
    private javax.swing.JMenuItem menuRename;
    private javax.swing.JPopupMenu popupMenu;
    // End of variables declaration//GEN-END:variables

}
