/*
 * MoveTableModel.java
 *
 * Created on March 21, 2007, 5:20 PM
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2007  Catherine Fitzpatrick
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

package shoddybattleclient.utils;
import javax.swing.table.*;
import java.util.*;
import shoddybattleclient.shoddybattle.PokemonMove;

/**
 *
 * @author Catherine
 * @author Ben
 */
public class MoveTableModel extends AbstractTableModel {
    
    private TableRow[] m_row;
    private ArrayList<String> m_selected = new ArrayList<String>();
    
    private static class TableRow {
        private Boolean m_enabled = new Boolean(false);
        private String m_category;
        private String m_move;
        private String m_type;
        private Integer m_pp;
        private Integer m_power;
        private Integer m_accuracy;
        
        public TableRow(String category,
                String move,
                String type,
                int pp,
                int power,
                int accuracy) {
            m_category = category;
            m_move = move;
            m_type = type;
            m_pp = new Integer(pp);
            m_power = new Integer(power);
            m_accuracy = new Integer(accuracy);
        }

        public boolean equals(TableRow t2) {
            return m_move.equals(t2.m_move);
        }
    }
    
    /**
     * Get a value from the table at (i, j).
     */
    public Object getValueAt(int i, int j) {
        TableRow row = m_row[i];
        switch (j) {
            case 0: return row.m_enabled;
            case 1: return row.m_move;
            case 2: return row.m_category;
            case 3: return row.m_type;
            case 4: return row.m_pp;
            case 5: return row.m_power;
            case 6: return (row.m_accuracy == 0) ? "---" : row.m_accuracy;
        }
        assert false;
        return null;
    }
    
    public String getColumnName(int col) {
        switch (col) {
            case 0: return "Selected?";
            case 1: return "Name";
            case 2: return "Class";
            case 3: return "Type";
            case 4: return "PP";
            case 5: return "Power";
            case 6: return "Accuracy";
        }
        assert false;
        return null;
    }
    
    public int getColumnCount() {
        return 7;
    }
    
    public int getRowCount() {
        return m_row.length;
    }
    
    @Override
    public Class getColumnClass(int c) {
         return getValueAt(0, c).getClass();
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == 0);
    }
     
    @Override
    public void setValueAt(Object value, int i, int j) {
        TableRow row = m_row[i];
        switch (j) {
            case 0:
                Boolean bol = (Boolean)value;
                boolean b = bol.booleanValue();
                if (b) {
                    if (m_selected.size() < 4) {
                        row.m_enabled = bol;
                        m_selected.add(row.m_move);
                    }
                } else {
                    Iterator itr = m_selected.iterator();
                    while (itr.hasNext()) {
                        String name = (String)itr.next();
                        if (name.equals(row.m_move)) {
                            itr.remove();
                            break;
                        }
                    }
                    for (int k = 0; k < m_row.length; ++k) {
                        if (m_row[k].m_move.equals(row.m_move)) {
                            m_row[k].m_enabled = new Boolean(false);
                            fireTableCellUpdated(k, 0);
                        }
                    }
                }
                break;
            case 1:
                row.m_move = (String)value;
                break;
            case 2:
                row.m_category = (String)value;
                break;
            case 3:
                row.m_type = (String)value;
                break;
            case 4:
                row.m_pp = (Integer)value;
                break;
            case 5:
                row.m_power = (Integer)value;
                break;
            case 6:
                try {
                    int acc = (Integer)value;
                    row.m_accuracy = acc;
                } catch (Exception e) {
                    row.m_accuracy = 0;
                }
            default:
                assert false;
        }
        fireTableCellUpdated(i, j);
    }
    
    /**
     * Set the moves that have been selected.
     */
    public void setSelectedMoves(String[] moves) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < moves.length; i++) {
            if (moves[i] != null) {
                list.add(moves[i]);
            }
        }
        m_selected = new ArrayList<String>(list);
        Set<String> set = new HashSet<String>(list);
        for (int i = 0; i < m_row.length; ++i) {
            m_row[i].m_enabled = new Boolean(set.contains(m_row[i].m_move));
            fireTableCellUpdated(i, 0);
        }
    }
    
    /**
     * Get the moves that have been selected.
     */
    public String[] getSelectedMoves() {
        return (String[])m_selected.toArray(new String[m_selected.size()]);
    }
    
    /**
     * Creates a new instance of MoveTableModel
     */
    public MoveTableModel(ArrayList<PokemonMove> moveList, String[] moves) {
        ArrayList<TableRow> list = new ArrayList<TableRow>();
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < moves.length; i++) {
            if (set.contains(moves[i])) {
                continue;
            }
            PokemonMove m = getMove(moves[i], moveList);
            set.add(moves[i]);
            if (m == null) {
                continue;
            }
            TableRow row = new TableRow(
                    m.damageClass, m.name, m.type, m.pp, m.power, m.accuracy);
            if (!set.contains(row)) list.add(row);
        }
        Collections.sort(list, new Comparator<TableRow>() {
            public int compare(TableRow o1, TableRow o2) {
                    TableRow t1 = (TableRow)o1;
                    TableRow t2 = (TableRow)o2;
                    return t1.m_move.compareToIgnoreCase(t2.m_move);
                }
            });
        m_row = (TableRow[])list.toArray(new TableRow[list.size()]);
    }

    // Finds a move in an ArrayList by name
    private PokemonMove getMove(String name, ArrayList<PokemonMove> moveList) {
        for (PokemonMove m : moveList) {
            if (m.name.equals(name)) {
                return m;
            }
        }
        return null;
    }
    
}
