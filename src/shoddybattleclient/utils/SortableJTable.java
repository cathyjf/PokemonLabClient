/*
 * BattlePanel.java
 *
 * Created on 18-Jun-2009, 4:22:01 PM
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

package shoddybattleclient.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.util.*;

/**
 *
 * @author Benjamin
 */
public class SortableJTable extends JTable {

    private enum SortStatus {
        SORTED_ASC,
        SORTED_DES
    }

    private Map<Integer, SortStatus> m_sortStatus = new HashMap<Integer, SortStatus>();

    public SortableJTable() {
        final JTableHeader header = this.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = header.getColumnModel().getColumnIndexAtX(e.getX());
                SortableJTable.this.sort(idx);
            }
        });
    }

    // sorts the table by the specified column index
    private void sort(final int col) {
        final TableModel model = this.getModel();
        int numRows = model.getRowCount();
        int numCols = model.getColumnCount();
        List<Comparable[]> rows = new ArrayList<Comparable[]>();
        for (int i = 0; i < numRows; i++) {
            Comparable[] values = new Comparable[numCols];
            for (int j = 0; j < numCols; j++) {
                values[j] = (Comparable)model.getValueAt(i, j);
            }
            rows.add(values);
        }
        Collections.sort(rows, new Comparator<Comparable[]>() {
            public int compare(Comparable[] arg0, Comparable[] arg1) {
                Comparable val0 = arg0[col];
                Comparable val1 = arg1[col];
                if ("---".equals(val0)) val0 = new Integer(0);
                if ("---".equals(val1)) val1 = new Integer(0);
                int comp = val0.compareTo(val1);
                if (Boolean.class.equals(val0.getClass())
                        || Integer.class.equals(val0.getClass())) {
                    comp = -comp;
                }
                return comp;
            }
        });

        if (m_sortStatus.containsKey(col)) {
            if (m_sortStatus.get(col).equals(SortStatus.SORTED_ASC)) {
                m_sortStatus.put(col, SortStatus.SORTED_DES);
                Collections.reverse(rows);
            } else {
                m_sortStatus.put(col, SortStatus.SORTED_ASC);
            }
        } else {
            m_sortStatus.put(col, SortStatus.SORTED_ASC);
        }

        for (int i = 0; i < rows.size(); i++) {
            Comparable[] c = rows.get(i);
            for (int j = 0; j < c.length; j++) {
                model.setValueAt(c[j], i, j);
            }
        }
    }
}
