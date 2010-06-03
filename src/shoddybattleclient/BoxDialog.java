/* BoxDialog.java
 *
 * Created on Monday May 31, 2010, 8:58 PM
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
import shoddybattleclient.shoddybattle.PokemonBox;
import shoddybattleclient.shoddybattle.PokemonBox.PokemonWrapper;

/**
 * A dialog to manage pokemon and boxes.
 * Files are case insensitive for best cross-platform effects.
 * @author Carlos
 */
public class BoxDialog extends javax.swing.JDialog {

    private class BoxListModel extends AbstractListModel {
        private List<PokemonBox> m_boxes = new ArrayList<PokemonBox>();

        public void addBoxes(List<PokemonBox> boxes) {
            m_boxes.addAll(boxes);
            Collections.sort(m_boxes);
            clearDuplicates(m_boxes);
            fireListChanged();
        }

        public void addBox(PokemonBox box) {
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

        public PokemonBox getBox(String name) {
            for(PokemonBox box : m_boxes) {
                if(box.getName().equalsIgnoreCase(name))
                    return box;
            }
            return null;
        }

        public void removeBox(PokemonBox box) {
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
        private PokemonBox owner;

        public void setBox(PokemonBox box) {
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
            return owner.getSize();
        }
    }

    class PokemonListRenderer extends JLabel implements ListCellRenderer {
        public PokemonListRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();
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
        super(parent);
        initComponents();
        teamBuilder = parent;

        pokemonModel = new PokemonListModel();
        listPokemon.setModel(pokemonModel);
        listPokemon.setCellRenderer(new PokemonListRenderer());

        File boxDir = new File(Preference.getBoxLocation());
        boxModel = new BoxListModel();
        ArrayList<PokemonBox> boxes = new ArrayList<PokemonBox>();
        if(boxDir.exists()) {
            for(File boxFile : boxDir.listFiles()) {
                if(!boxFile.isDirectory())
                    continue;
                boxes.add(new PokemonBox(boxFile.getName()));
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
                return new Point(evt.getX() + 15, evt.getY() + 25);
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

        if(boxName == null) return;
        if(boxModel.getBox(boxName) != null) {
            JOptionPane.showMessageDialog(this, "This box already exists", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PokemonBox newBox = new PokemonBox(boxName);
            boxModel.addBox(newBox);
            listBoxes.setSelectedValue(newBox, true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error making box", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnNewBoxActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();

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
            box.addPokemon(name, teamBuilder.getSelectedPokemon());
            pokemonModel.fireListChanged();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding pokemon", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnImportActionPerformed

    private void listBoxesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listBoxesValueChanged
        PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();
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
            PokemonBox current = (PokemonBox)item;

            //Duplicates are fine if we're merely changing the case
            PokemonBox previous = boxModel.getBox(newName);
            if(previous != null && previous != current) {
                JOptionPane.showMessageDialog(this, "A box with this name already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            oldFile = new File(current.getBoxPath());
            newFile = new File(Preference.getBoxLocation() + "/" + newName);
            
            try {
                oldFile.renameTo(newFile);

                PokemonBox newBox = new PokemonBox(newName);
                boxModel.removeBox(current);
                boxModel.addBox(newBox);
                list.setSelectedValue(newBox, true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error renaming box", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();
            PokemonWrapper current = (PokemonWrapper)item;

            //Duplicates are fine if we're merely changing the case
            PokemonWrapper previous = box.getPokemon(newName);
            if(previous != null && previous.pokemon != current.pokemon) {
                JOptionPane.showMessageDialog(this, "A pokemon with this name already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                box.removePokemon(current.name);
                box.addPokemon(newName, current.pokemon);

                pokemonModel.fireListChanged();
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
            PokemonBox box = (PokemonBox)item;
            try {
                File boxFile = new File(box.getBoxPath());

                //While we delete remaining files later, this helps in case there is an exception
                //If there is a "problem Pokemon", we need the display to update properly after all
                while(box.getSize() != 0) {
                    box.removePokemonAt(box.getSize()-1);
                }

                //If the user has rigged it with duplicates, files may remain
                //FIXME: Consider ill placed non-empty sub directories
                for(File file : boxFile.listFiles()) {
                    file.delete();
                }

                boxModel.removeBox(box);
                boxFile.delete();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting box", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            boxModel.fireListChanged();

        } else {
            PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();
            PokemonWrapper wrapper = (PokemonWrapper)item;

            try {
                box.removePokemon(wrapper.name);
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
