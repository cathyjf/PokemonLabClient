/*
 * CheckBoxList.java
 *
 * Created on June 3, 2010, 1:48:11 PM
 *
 * This file is a part of Shoddy Battle 2.
 * Copyright (C) 2010  Catherine Fitzpatrick and Benjamin Gwin
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author ben
 */
public class ClauseList extends JList {

    public static class Clause {
        public String name;
        public String description;
        public boolean selected = false;
        public Clause(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    public static class ClauseListModel extends AbstractListModel {

        private List<Clause> m_data;

        public ClauseListModel(List<Clause> clauses) {
            m_data = clauses;
        }

        @Override
        public int getSize() {
            return m_data.size();
        }

        @Override
        public Object getElementAt(int index) {
            return m_data.get(index);
        }

        public boolean[] getSelected() {
            boolean[] ret = new boolean[m_data.size()];
            for (int i = 0; i < m_data.size(); i++) {
                Clause c = m_data.get(i);
                ret[i] = c.selected;
            }
            return ret;
        }

        public void toggleSelected(int index) {
            Clause c = m_data.get(index);
            c.selected = !c.selected;
            this.fireContentsChanged(this, index, index);
        }
    }

    private static class ClauseListRenderer extends JCheckBox implements
                                                            ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
            Clause c = (Clause)value;
            setEnabled(!((ClauseList)list).m_disabled);
            setText(c.name);
            setToolTipText("<html>" + c.description + "</html>");
            setSelected(c.selected);
            setBackground(
                isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(
                isSelected ? list.getSelectionForeground() : list.getForeground());
            setFocusPainted(cellHasFocus);
            return this;
        }
    }

    private boolean m_disabled = true;

    public ClauseList() {
        this.setCellRenderer(new ClauseListRenderer());
        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (ClauseList.this.m_disabled) return;
                int index = ClauseList.this.locationToIndex(e.getPoint());
                ClauseListModel clm = (ClauseListModel)ClauseList.this.getModel();
                clm.toggleSelected(index);
            }
        });
    }

    public void setDisabled(boolean disable) {
        m_disabled = disable;
    }
}
