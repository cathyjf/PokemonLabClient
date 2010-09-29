/*
 * BoxForm.java
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
            return 5;
        }

        @Override
        public int getRowCount() {
            return (owner == null) ? 0 : owner.getSize();
        }

        public PokemonWrapper getPokemonAt(int row) {
            return owner.getPokemonAt(row);
        }

        @Override
        public void fireTableDataChanged() {
            super.fireTableDataChanged();
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
                    return wrapper.pokemon.ability;
                case 3:
                    return wrapper.pokemon.nature;
                case 4:
                    return wrapper.pokemon.item;
            }
            return null;
        }
    }

    private class BoxListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            boolean focused = (m_focusedBox == index) ? true : cellHasFocus;
            return super.getListCellRendererComponent(list, value,
                    index, isSelected, focused);
        }
    }

    private class PokemonTableRenderer extends DefaultTableCellRenderer {
        public PokemonTableRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, 
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            
            if (column == 0) {
                this.setText("");
                this.setIcon((ImageIcon)value);
            } else {
                this.setText(value.toString());
                this.setIcon(null);
            }

            Pokemon p = m_pokemonModel.getPokemonAt(row).pokemon;
            setToolTipText("<html>" + p.toTeamText().replace("\n", "<br>"));
            return this;
        }
    }

    private class TeamRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value,
                    index, isSelected, cellHasFocus);
            Pokemon p = m_forms.get(index).getPokemon();
            setToolTipText("<html>Index " + (index+1) + ":<br>" +
                    p.toTeamText().replace("\n", "<br>"));
            return this;
        }
    }

    private static DataFlavor m_wrapperFlavor =
            new DataFlavor(PokemonWrapper.class, "PokemonWrapper");
    private static DataFlavor m_pokemonFlavor =
            new DataFlavor(Pokemon.class, "Pokemon");
    private class PokemonTableTransferHandler extends TransferHandler {
        private class WrapperTransferable implements Transferable {
            private PokemonWrapper m_poke;
            public WrapperTransferable(PokemonWrapper poke) {
                m_poke = poke;
            }
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {m_wrapperFlavor};
            }
            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.match(m_wrapperFlavor);
            }
            @Override
            public Object getTransferData(DataFlavor flavor) {
                return m_poke;
            }
        }
        @Override
        public boolean canImport(JComponent c, DataFlavor[] transferFlavors) {
            // The DropTarget handles importing. Letting the TransferHandler
            // deal with importing leads to an unintuitive focus behavior
            return false;
        }
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }
        @Override
        public Transferable createTransferable(JComponent c) {
            JTable table = (JTable)c;
            Point p = c.getMousePosition();
            int idx = table.rowAtPoint(p);
            if (idx < 0) return null;
            return new WrapperTransferable(m_pokemonModel.getPokemonAt(idx));
        }
    }

    private class TablePokemonDropTarget extends DropTarget {
        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            if (dtde.isDataFlavorSupported(m_pokemonFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }
        @Override
        public void drop(DropTargetDropEvent dtde) {
            this.dragExit(null);
            try {
                Transferable transfer = dtde.getTransferable();
                if (transfer.isDataFlavorSupported(m_pokemonFlavor)) {
                    PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();
                    PokemonWrapper result = m_teamBuilder.addPokemonToBox(box,
                            (Pokemon)transfer.getTransferData(m_pokemonFlavor));
                    if (result != null) {
                        int row = box.indexOf(result);
                        m_pokemonModel.fireTableDataChanged();
                        tblPokemon.setRowSelectionInterval(row, row);
                    }
                } else {
                    dtde.rejectDrop();
                    return;
                }
            } catch (Exception ex) {
                dtde.rejectDrop();
                return;
            }
        }
    }

    private class ListBoxesDropTarget extends DropTarget {
        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            if (dtde.isDataFlavorSupported(m_wrapperFlavor) ||
                    dtde.isDataFlavorSupported(m_pokemonFlavor)) {
                int idx = listBoxes.locationToIndex(dtde.getLocation());
                Rectangle r = listBoxes.getCellBounds(idx, idx);
                if(idx < 0 || !r.contains(dtde.getLocation())) {
                    dtde.rejectDrag();
                    return;
                }
                m_focusedBox = idx;
                listBoxes.repaint();
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }
        @Override
        public void dragExit(DropTargetEvent dte) {
            m_focusedBox = -1;
            listBoxes.repaint();
        }
        @Override
        public void drop(DropTargetDropEvent dtde) {
            this.dragExit(null);      
            try {
                Transferable transfer = dtde.getTransferable();
                if (transfer.isDataFlavorSupported(m_pokemonFlavor)) {
                    int idx = listBoxes.locationToIndex(dtde.getLocation());
                    listBoxes.setSelectedIndex(idx);
                    PokemonBox box = (PokemonBox)m_boxModel.getElementAt(idx);
                    PokemonWrapper result = m_teamBuilder.addPokemonToBox(box,
                            (Pokemon)transfer.getTransferData(m_pokemonFlavor));
                    if (result != null) {
                        int row = box.indexOf(result);
                        m_pokemonModel.fireTableDataChanged();
                        tblPokemon.setRowSelectionInterval(row, row);
                    }
                } else if (transfer.isDataFlavorSupported(m_wrapperFlavor)) {
                    PokemonWrapper wrapper = (PokemonWrapper)transfer.getTransferData(m_wrapperFlavor);
                    PokemonBox previousBox = (PokemonBox)listBoxes.getSelectedValue();
                    PokemonBox newBox = (PokemonBox)m_boxModel.getElementAt(
                                listBoxes.locationToIndex(dtde.getLocation()));
                    if (previousBox == newBox) {
                        dtde.rejectDrop();
                        return;
                    }

                    if (newBox.getPokemon(wrapper.name) != null) {
                        int option = JOptionPane.showConfirmDialog(m_teamBuilder,
                                "This pokemon already exists in this box. Overwrite?",
                                "", JOptionPane.YES_NO_OPTION);
                        if (option != JOptionPane.YES_OPTION) {
                            dtde.rejectDrop();
                            return;
                        }
                    }

                    previousBox.removePokemon(wrapper.name);
                    newBox.addPokemon(wrapper.name, wrapper.pokemon);
                    m_pokemonModel.fireTableDataChanged();
                    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                } else {
                    dtde.rejectDrop();
                    return;
                }
            } catch (Exception ex) {
                dtde.rejectDrop();
                return;
            }
        }
    }

    private class ListTeamTransferHandler extends TransferHandler {
        private class PokemonTransferable implements Transferable {
            private Pokemon m_poke;
            public PokemonTransferable(Pokemon poke) {
                m_poke = poke;
            }
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {m_pokemonFlavor};
            }
            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.match(m_pokemonFlavor);
            }
            @Override
            public Object getTransferData(DataFlavor flavor) {
                return m_poke;
            }
        }
        @Override
        public boolean canImport(JComponent c, DataFlavor[] transferFlavors) {
            if (transferFlavors.length != 1)
                return false;
            if (!m_wrapperFlavor.equals(transferFlavors[0]))
                return false;
            return true;
        }
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }
        @Override
        public Transferable createTransferable(JComponent c) {
            JList list = (JList)c;
            Point p = c.getMousePosition();
            int idx = list.locationToIndex(p);
            if (idx < 0) return null;
            return new PokemonTransferable((Pokemon)list.getSelectedValue());
        }
        @Override
        public boolean importData(JComponent comp, Transferable transfer) {
            if (!transfer.isDataFlavorSupported(m_wrapperFlavor))
                return false;

            try {
                PokemonWrapper wrapper = (PokemonWrapper)transfer.getTransferData(m_wrapperFlavor);
                int teamIndex = listTeam.locationToIndex(listTeam.getMousePosition());
                m_teamBuilder.setPokemonAt(teamIndex, wrapper.pokemon);
                ((DefaultListModel)listTeam.getModel()).set(teamIndex, wrapper.pokemon);

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    private TeamBuilder m_teamBuilder;

    private BoxListModel m_boxModel;
    private PokemonTableModel m_pokemonModel;
    private JTable tblPokemon;
    private JList listTeam;
    private List<TeamBuilderForm> m_forms;

    // Swing doesn't allow us to give a selected look to lists easily, 
    // so we use hacks to do it
    private int m_focusedBox = -1;

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
                boxes.add(new PokemonBox(boxFile.getName(), m_teamBuilder.getSpeciesList()));
            }
        }
        m_boxModel.addBoxes(boxes);
        listBoxes.setModel(m_boxModel);
        listBoxes.setCellRenderer(new BoxListRenderer());
        listBoxes.setDropTarget(new ListBoxesDropTarget());
        listBoxes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tblPokemon = new JTable();
        m_pokemonModel = new PokemonTableModel();
        tblPokemon.setModel(m_pokemonModel);
        tblPokemon.setRowHeight(34);
        tblPokemon.setRowSelectionAllowed(true);
        tblPokemon.setColumnSelectionAllowed(false);
        tblPokemon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPokemon.setDragEnabled(true);
        tblPokemon.setTransferHandler(new PokemonTableTransferHandler());
        tblPokemon.setDropTarget(new TablePokemonDropTarget());
        tblPokemon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    int row = tblPokemon.rowAtPoint(evt.getPoint());
                    if (row >= 0 && row < m_pokemonModel.getRowCount()) {
                        tblPokemon.setRowSelectionInterval(row, row);
                        popupPokemon.show(tblPokemon, evt.getX(), evt.getY());
                    }
                }
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mouseClicked(evt);
            }
        });

        TableColumnModel model = tblPokemon.getColumnModel();
        tblPokemon.setDefaultRenderer(Object.class, new PokemonTableRenderer());
        model.getColumn(0).setHeaderValue("");
        model.getColumn(0).setMaxWidth(34);
        model.getColumn(1).setHeaderValue("Name");
        model.getColumn(2).setHeaderValue("Ability");
        model.getColumn(3).setHeaderValue("Nature");
        model.getColumn(4).setHeaderValue("Item");
        scrollPokemon.setViewportView(tblPokemon);

        listTeam = new JList();
        listTeam.setDragEnabled(true);
        listTeam.setTransferHandler(new ListTeamTransferHandler());
        listTeam.setCellRenderer(new TeamRenderer());
    }

    public void updateBoxes() {
        //Hold the current selected value
        Object selected = listBoxes.getSelectedValue();

        File boxDir = new File(Preference.getBoxLocation());
        m_boxModel = new BoxListModel();
        ArrayList<PokemonBox> boxes = new ArrayList<PokemonBox>();
        if (boxDir.exists()) {
            for (File boxFile : boxDir.listFiles()) {
                if (!boxFile.isDirectory()) {
                    continue;
                }
                boxes.add(new PokemonBox(boxFile.getName(),
                        m_teamBuilder.getSpeciesList()));
            }
        }
        m_boxModel.addBoxes(boxes);
        listBoxes.setModel(m_boxModel);

        if (selected != null) {
            for (int i = 0; i < m_boxModel.getSize(); i++) {
                if (m_boxModel.getElementAt(i).equals(selected)) {
                    listBoxes.setSelectedIndex(i);
                    return;
                }
            }
            listBoxes.clearSelection();
            m_pokemonModel.setBox(null);
        }
    }

    public PokemonBox getSelectedBox() {
        Object obj = listBoxes.getSelectedValue();
        if (obj == null)
            return null;
        return (PokemonBox)obj;
    }

    public Pokemon getSelectedPokemon() {
        int selected = tblPokemon.getSelectedRow();
        if (selected < 0 || selected >= tblPokemon.getRowCount()) {
            return null;
        }

        return m_pokemonModel.getPokemonAt(selected).pokemon;
    }

    // This is so that we can rig it with drag and drop support beforehand
    public JList getTeamList(List<TeamBuilderForm> forms) {
        m_forms = forms;
        DefaultListModel pModel = new DefaultListModel();
        for (int i = 0; i < forms.size(); i++) {
             pModel.addElement(forms.get(i).getPokemon());
        }
        listTeam.setModel(pModel);
        return listTeam;
    }

    //On *nix systems, both Box and Pokemon loading allow for duplicates on
    //first load. This clears those duplicates from the list.
    //This list MUST be sorted before using this method
    private <T extends Comparable<T>> void clearDuplicates(List<T> list) {
        //Sorted lists allow us to do this in O(n)
        Iterator<T> iter = list.iterator();
        T previous = null;
        while (iter.hasNext()) {
            T current = iter.next();

            //this only happens once, but it makes the code cleaner
            if (previous == null) {
                previous = current;
                continue;
            }

            if (previous.compareTo(current) == 0)
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

        popupBoxes = new javax.swing.JPopupMenu();
        menuRenameBox = new javax.swing.JMenuItem();
        menuDeleteBox = new javax.swing.JMenuItem();
        popupPokemon = new javax.swing.JPopupMenu();
        menuRenamePokemon = new javax.swing.JMenuItem();
        menuDeletePokemon = new javax.swing.JMenuItem();
        scrollPokemon = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        listBoxes = new javax.swing.JList();
        btnNewBox = new javax.swing.JButton();

        menuRenameBox.setText("Rename");
        menuRenameBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRenameBoxActionPerformed(evt);
            }
        });
        popupBoxes.add(menuRenameBox);

        menuDeleteBox.setText("Delete");
        menuDeleteBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteBoxActionPerformed(evt);
            }
        });
        popupBoxes.add(menuDeleteBox);

        menuRenamePokemon.setText("Rename");
        menuRenamePokemon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRenamePokemonActionPerformed(evt);
            }
        });
        popupPokemon.add(menuRenamePokemon);

        menuDeletePokemon.setText("Delete");
        menuDeletePokemon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeletePokemonActionPerformed(evt);
            }
        });
        popupPokemon.add(menuDeletePokemon);

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(662, 352));

        listBoxes.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listBoxes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listBoxesMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                listBoxesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listBoxesMouseReleased(evt);
            }
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(btnNewBox, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(scrollPokemon, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNewBox))
                    .addComponent(scrollPokemon, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
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

        if (boxName == null) return;
        if (m_boxModel.getBox(boxName) != null) {
            JOptionPane.showMessageDialog(this, "This box already exists", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PokemonBox newBox = new PokemonBox(boxName, m_teamBuilder.getSpeciesList());
            m_boxModel.addBox(newBox);
            listBoxes.setSelectedValue(newBox, true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error making box", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnNewBoxActionPerformed

    private void menuDeleteBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeleteBoxActionPerformed
        PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want " +
                " to delete " + box.getName() + "?", "", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION)
            return;

        while (box.getSize() != 0) {
            box.removePokemonAt(box.getSize()-1);
        }

        //If the user has rigged it with duplicates, files may remain
        boolean badlyFormatted = true;
        File boxFile = box.getBoxFolder();
        for (File file : boxFile.listFiles()) {
            if (file.isDirectory() && file.listFiles().length > 0) {
                badlyFormatted = true;
            } else {
                file.delete();
            }
        }

        if (boxFile.delete()) {
            m_boxModel.removeBox(box);
        } else if (!badlyFormatted) {
            JOptionPane.showMessageDialog(this, "Error deleting box", "Error",
                JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "The box is badly formatted, so it couldn't be removed", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        m_boxModel.fireListChanged();
        m_pokemonModel.fireTableDataChanged();
        
        listBoxes.clearSelection();
    }//GEN-LAST:event_menuDeleteBoxActionPerformed

    private void menuRenameBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRenameBoxActionPerformed
        PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();
        String newName = JOptionPane.showInputDialog(this, "New name for "+box.getName()+":");
        if (newName == null || (newName = newName.trim()).equals(""))
            return;

        //Check for duplicates.
        PokemonBox previous = m_boxModel.getBox(newName);
        if(previous != null && previous != box) {
            JOptionPane.showMessageDialog(this,
                    "A box with this name already exists",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File oldFile = box.getBoxFolder();
        File newFile = new File(Preference.getBoxLocation() + "/" + newName);

        try {
            oldFile.renameTo(newFile);

            PokemonBox newBox = new PokemonBox(newName, m_teamBuilder.getSpeciesList());
            m_boxModel.removeBox(box);
            m_boxModel.addBox(newBox);
            listBoxes.setSelectedValue(newBox, true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error renaming box", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }       
    }//GEN-LAST:event_menuRenameBoxActionPerformed

    private void listBoxesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listBoxesMouseReleased
        listBoxesMouseClicked(evt);
    }//GEN-LAST:event_listBoxesMouseReleased

    private void listBoxesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listBoxesMouseClicked
        if (evt.isPopupTrigger()) {
            int index = listBoxes.locationToIndex(evt.getPoint());
            if (index >= 0 && index < m_boxModel.getSize()) {
                listBoxes.setSelectedIndex(index);
                popupBoxes.show(listBoxes, evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_listBoxesMouseClicked

    private void listBoxesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listBoxesMousePressed
        listBoxesMouseClicked(evt);
    }//GEN-LAST:event_listBoxesMousePressed

    private void menuRenamePokemonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRenamePokemonActionPerformed
        PokemonWrapper current = (PokemonWrapper)m_pokemonModel.getPokemonAt(
                tblPokemon.getSelectedRow());
        String newName = JOptionPane.showInputDialog(this, "New name for "+current.name+":");
        if (newName == null || (newName = newName.trim()).equals(""))
            return;

        PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();

        //Duplicates are fine if we're merely changing the case
        PokemonWrapper previous = box.getPokemon(newName);
        if (previous != null && previous.pokemon != current.pokemon) {
            JOptionPane.showMessageDialog(this, "A pokemon with this name already exists",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            box.removePokemon(current.name);
            box.addPokemon(newName, current.pokemon);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error renaming pokemon", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        int row = box.indexOf(newName);
        m_pokemonModel.fireTableDataChanged();
        tblPokemon.setRowSelectionInterval(row, row);
    }//GEN-LAST:event_menuRenamePokemonActionPerformed

    private void menuDeletePokemonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeletePokemonActionPerformed
        PokemonWrapper wrapper = (PokemonWrapper)m_pokemonModel.getPokemonAt(
                tblPokemon.getSelectedRow());
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want " +
                " to delete " + wrapper.name + "?", "", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION)
            return;

        PokemonBox box = (PokemonBox)listBoxes.getSelectedValue();

        try {
            box.removePokemon(wrapper.name);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting pokemon", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        m_pokemonModel.fireTableDataChanged();
        tblPokemon.clearSelection();
    }//GEN-LAST:event_menuDeletePokemonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNewBox;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList listBoxes;
    private javax.swing.JMenuItem menuDeleteBox;
    private javax.swing.JMenuItem menuDeletePokemon;
    private javax.swing.JMenuItem menuRenameBox;
    private javax.swing.JMenuItem menuRenamePokemon;
    private javax.swing.JPopupMenu popupBoxes;
    private javax.swing.JPopupMenu popupPokemon;
    private javax.swing.JScrollPane scrollPokemon;
    // End of variables declaration//GEN-END:variables

}
