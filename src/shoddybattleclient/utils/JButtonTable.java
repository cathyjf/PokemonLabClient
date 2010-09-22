/*
 * JTableButtonMouseListener.java
 *
 * This code was found at http://www.devx.com/getHelpOn/10MinuteSolution/20425.
 * It was written by Daniel F. Savarese.
 *
 * I, Catherine Fitzpatrick, have made a trivial change by adding a subclass
 * of JTable for easier use of the code.
 */
package shoddybattleclient.utils;

import java.awt.Color;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.Component;

/**
 *
 * @author Daniel F. Savarese
 * @author Catherine James Fitzpatrick
 */
public class JButtonTable extends SortableJTable {

    /**
     * Set up this table so that it can be used to render JButtons in cells.
     */
    public JButtonTable() {
        setDefaultRenderer(String.class, new RightAlignRenderer());
        setDefaultRenderer(JButton.class,
                new JTableButtonRenderer(
                getDefaultRenderer(JButton.class)));
        JTableButtonMouseListener listener = new JTableButtonMouseListener(this);
        addMouseListener(listener);
        addMouseMotionListener(listener);
    }
}

/**
 * Renders components that are numerical or --- to be right aligned
 * @author Benjamin Gwin
 */
class RightAlignRenderer implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object val,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel(val.toString());
        label.setFont(label.getFont().deriveFont(12.0f));
        if ((val instanceof Integer) || "---".equals(val)) {
            label.setHorizontalAlignment(JLabel.RIGHT);
        }
        label.setForeground(table.getForeground());
        return label;
    }
}

/**
 *
 * @author Daniel F. Savarese
 */
class JTableButtonRenderer implements TableCellRenderer {

    private TableCellRenderer __defaultRenderer;

    public JTableButtonRenderer(TableCellRenderer renderer) {
        __defaultRenderer = renderer;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Component) {
            return (Component) value;
        }
        return __defaultRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
    }
}

/**
 *
 * @author Daniel F. Savarese
 */
class JTableButtonMouseListener implements MouseListener, MouseMotionListener {

    private JTable __table;
    private JButton previousButton;

    private JButton __getButton(MouseEvent e) {
        TableColumnModel columnModel = __table.getColumnModel();
        int column = columnModel.getColumnIndexAtX(e.getX());
        int row = e.getY() / __table.getRowHeight();
        Object value;

        if (row >= __table.getRowCount() || row < 0
                || column >= __table.getColumnCount() || column < 0) {
            return null;
        }

        value = __table.getValueAt(row, column);

        if (!(value instanceof JButton))
            return null;

        return (JButton) value;
    }

    public JTableButtonMouseListener(JTable table) {
        __table = table;
    }

    public void mouseClicked(MouseEvent e) {
        JButton button = __getButton(e);

        if(button == null)
            return;

        MouseEvent buttonEvent =
                    (MouseEvent) SwingUtilities.convertMouseEvent(__table, e, button);
        button.dispatchEvent(buttonEvent);
        __table.repaint();
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {
        if(previousButton != null) {
            previousButton.dispatchEvent(new MouseEvent(previousButton, MouseEvent.MOUSE_EXITED,
                        e.getWhen(), e.getModifiers(), 5, 5, 0, false));
        }

        previousButton = null;
        __table.repaint();
    }

    public void mousePressed(MouseEvent e) {
        mouseClicked(e);
    }

    public void mouseReleased(MouseEvent e) {
        mouseClicked(e);
    }

    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        JButton button = __getButton(e);

        if(button == previousButton)
            return;

        if(previousButton != null) {
            previousButton.dispatchEvent(new MouseEvent(previousButton, MouseEvent.MOUSE_EXITED,
                    e.getWhen(), e.getModifiers(), 5, 5, 0, false));
        }

        if (button != null) {
            button.dispatchEvent(new MouseEvent(button, MouseEvent.MOUSE_ENTERED,
                    e.getWhen(), e.getModifiers(), 5, 5, 0, false));
        }
        
        __table.repaint();
        previousButton = button;
    }
}
