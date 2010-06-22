/*
 * TeamBuilder.java
 *
 * Created on June 18, 2010, 5:45 PM
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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import shoddybattleclient.shoddybattle.Pokemon;
import shoddybattleclient.shoddybattle.PokemonBox;
import shoddybattleclient.shoddybattle.PokemonBox.PokemonWrapper;

/**
 *
 * @author Carlos
 */
public class BoxForm extends javax.swing.JPanel {

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

        //Let the list know that we added a box
        public void fireListChanged() {
            ListDataEvent evt = new ListDataEvent(listBoxes,
                    ListDataEvent.CONTENTS_CHANGED, 0, m_boxes.size());
            for (ListDataListener listener : getListDataListeners()) {
                listener.contentsChanged(evt);
            }
        }

        @Override
        public int getSize() {
            return m_boxes.size();
        }

        @Override
        public Object getElementAt(int index) {
            if(getSize() == 0) return null;
            return m_boxes.get(index);
        }     
    }

    private class PokemonTableModel extends AbstractTableModel {
        private PokemonBox owner;

        public void setBox(PokemonBox box) {
            owner = box;
            fireTableDataChanged();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public int getRowCount() {
            if (owner == null)
                return 0;
            return owner.getSize();
        }

        public PokemonWrapper getPokemonAt(int row) {
            return owner.getPokemonAt(row);
        }

        private String getMovesString(Pokemon p) {
            if (p.moves.length == 0) return "";

            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < p.moves.length-1; i++) {
                buf.append(p.moves[i]);
                buf.append("/");
            }
            buf.append(p.moves[p.moves.length-1]);
            return new String(buf);
        }

        @Override
        public Object getValueAt(int row, int col) {
            PokemonWrapper wrapper = getPokemonAt(row);
            switch (col) {
                case 0:
                    try {
                        Pokemon poke = wrapper.pokemon;
                        boolean showMale = poke.gender != Pokemon.Gender.GENDER_FEMALE;
                        java.awt.Image img = GameVisualisation.getSprite(
                            m_teamBuilder.getSpecies(poke.toString()).getId(), true, showMale, poke.shiny);
                            img = img.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
                        return new ImageIcon(img);
                    } catch (Exception ex) {
                        return null;
                    }
                case 1:
                    return wrapper.name;
                case 2:
                    return wrapper.pokemon.item;
                case 3:
                    return getMovesString(wrapper.pokemon);
            }
            return null;
        }
    }

    private class IconCellRenderer extends JLabel implements TableCellRenderer {
        public IconCellRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
                this.setForeground(table.getSelectionForeground());
            } else {
                this.setBackground(table.getBackground());
                this.setForeground(table.getForeground());
            }

            setIcon((ImageIcon)value);
            return this;
        }
    }

    private TeamBuilder m_teamBuilder;

    private BoxListModel m_boxModel;
    private PokemonTableModel m_pokemonModel;
    private JTable tblPokemon;

    /** Creates new form BoxForm */
    public BoxForm(TeamBuilder teamBuilder) {
        initComponents();
        m_teamBuilder = teamBuilder;

        File boxDir = new File(Preference.getBoxLocation());
        m_boxModel = new BoxListModel();
        ArrayList<PokemonBox> boxes = new ArrayList<PokemonBox>();
        if(boxDir.exists()) {
            for(File boxFile : boxDir.listFiles()) {
                if(!boxFile.isDirectory())
                    continue;
                boxes.add(new PokemonBox(boxFile.getName()));
            }
        }
        m_boxModel.addBoxes(boxes);
        listBoxes.setModel(m_boxModel);
        listBoxes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tblPokemon = new JTable();
        m_pokemonModel = new PokemonTableModel();
        tblPokemon.setModel(m_pokemonModel);
        tblPokemon.setRowHeight(34);
        tblPokemon.setRowSelectionAllowed(true);
        tblPokemon.setColumnSelectionAllowed(false);
        tblPokemon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        TableColumnModel model = tblPokemon.getColumnModel();
        model.getColumn(0).setHeaderValue("");
        model.getColumn(0).setCellRenderer(new IconCellRenderer());
        model.getColumn(0).setMaxWidth(34);
        model.getColumn(1).setHeaderValue("Name");
        model.getColumn(2).setHeaderValue("Item");
        model.getColumn(3).setHeaderValue("Moves");
        model.getColumn(1).setPreferredWidth(60);
        model.getColumn(2).setPreferredWidth(60);
        model.getColumn(3).setPreferredWidth(220);
        
        scrollPokemon.setViewportView(tblPokemon);
    }

    public void updateBoxes() {
        //Hold the current selected value
        Object selected = listBoxes.getSelectedValue();

        File boxDir = new File(Preference.getBoxLocation());
        m_boxModel = new BoxListModel();
        ArrayList<PokemonBox> boxes = new ArrayList<PokemonBox>();
        if(boxDir.exists()) {
            for(File boxFile : boxDir.listFiles()) {
                if(!boxFile.isDirectory())
                    continue;
                boxes.add(new PokemonBox(boxFile.getName()));
            }
        }
        m_boxModel.addBoxes(boxes);
        listBoxes.setModel(m_boxModel);

        if (selected != null) {
            for (int i = 0; i < m_boxModel.getSize(); i++) {
                if (m_boxModel.getElementAt(i).equals(selected)) {
                    listBoxes.setSelectedIndex(i);
                }
            }
        }
    }

    public Pokemon getSelectedPokemon() {
        int selected = tblPokemon.getSelectedRow();
        if (selected < 0 || selected >= tblPokemon.getRowCount()) {
            return null;
        }

        return m_pokemonModel.getPokemonAt(selected).pokemon;
    }

    public PokemonBox getSelectedBox() {
        Object obj = listBoxes.getSelectedValue();
        if (obj == null)
            return null;
        return (PokemonBox)obj;
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

        scrollPokemon = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        listBoxes = new javax.swing.JList();
        btnNewBox = new javax.swing.JButton();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(662, 352));

        listBoxes.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listBoxes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listBoxesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(listBoxes);

        btnNewBox.setText("New Box");
        btnNewBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(btnNewBox, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(scrollPokemon, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNewBox))
                    .addComponent(scrollPokemon, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listBoxesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listBoxesValueChanged
        int index = listBoxes.getSelectedIndex();
        if (index < 0 || index >= m_boxModel.getSize())
            return;

        m_pokemonModel.setBox((PokemonBox)m_boxModel.getElementAt(index));
    }//GEN-LAST:event_listBoxesValueChanged

    private void btnNewBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewBoxActionPerformed
        String boxName = JOptionPane.showInputDialog(this, "New box name:");

        if(boxName == null) return;
        if(m_boxModel.getBox(boxName) != null) {
            JOptionPane.showMessageDialog(this, "This box already exists", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PokemonBox newBox = new PokemonBox(boxName);
            m_boxModel.addBox(newBox);
            listBoxes.setSelectedValue(newBox, true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error making box", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnNewBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNewBox;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList listBoxes;
    private javax.swing.JScrollPane scrollPokemon;
    // End of variables declaration//GEN-END:variables

}
