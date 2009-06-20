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

    //A table model that can be sorted on a column index
    public interface SortableTableModel {
        //sort this model based on this index, and if it should be reversed
        public void sort(int col, boolean reverse);
    }

    private Map<Integer, SortStatus> m_sortStatus = new HashMap<Integer, SortStatus>();

    public SortableJTable() {
        final JTableHeader header = this.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = header.getColumnModel().getColumnIndexAtX(e.getX());
                sort(idx);
                
            }
        });
    }

    // sorts the table by the specified column index
    private void sort(int col) {
        TableModel model = getModel();
        if (!(model instanceof SortableTableModel)) return;
        SortableTableModel sModel = (SortableTableModel)model;
        
        boolean reverse = false;
        if (m_sortStatus.containsKey(col)) {
            if (m_sortStatus.get(col).equals(SortStatus.SORTED_ASC)) {
                m_sortStatus.put(col, SortStatus.SORTED_DES);
                reverse = true;
            } else {
                m_sortStatus.put(col, SortStatus.SORTED_ASC);
            }
        } else {
            m_sortStatus.put(col, SortStatus.SORTED_ASC);
        }

        sModel.sort(col, reverse);
    }
}
