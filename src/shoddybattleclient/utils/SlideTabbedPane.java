package shoddybattleclient.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SingleSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The standard JTabbedPane does not support dragging tabs to change tab order
 * or to remove tabs. As well, JTabbedPane does not support custom components
 * in Java 5 or below. Therefore, I decided to reimplement JTabbedPane to
 * fulfill what I need.
 *
 * @author Carlos Fernandez
 */
public class SlideTabbedPane extends JPanel {

    private class Tab extends JPanel {
        public int index;

        public String title;
        public String tipText;
        public Icon icon;
        public Component tabComponent;
        public Component component;
        public boolean slidable = true;

        // The component to render.
        private Component m_renderComponent;
        
        // An optimization construct
        private Component m_prevComponent;
        
        public int deltaX = 0;
        private Insets m_insets =  new Insets(3, 6, 3, 6);

        public Tab() {
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            this.setOpaque(false);

            TabListener listener = new TabListener(this);
            this.addMouseListener(listener);
            this.addMouseMotionListener(listener);
            this.addMouseWheelListener(listener);
        }

        public void update() {
            if ((m_renderComponent == null) ||
                    (m_renderComponent != m_prevComponent)) {
                if (tabComponent == null) {
                    m_renderComponent = new JLabel(title);
                    m_renderComponent.setForeground(
                            SlideTabbedPane.this.getForeground());
                } else {
                    m_renderComponent = tabComponent;
                }

                this.removeAll();
                this.add(m_renderComponent);
            }

            if (tabComponent == null) {
                JLabel label = (JLabel)m_renderComponent;
                if (!label.getText().equals(title)) {
                    label.setText(title);
                }
                label.setIcon(icon);
            }
        }

        @Override
        public Insets getInsets() {
            return m_insets;
        }
        
        @Override
        public String getToolTipText() {
            return tipText;
        }

        @Override
        public void paintComponent(Graphics g) {
            boolean isSelected = (this.index == getSelectedIndex());

            Color c = m_panel.getBackground();
            if (!isSelected) {
                int red = (int)(c.getRed() * 0.8);
                int green = (int)(c.getGreen() * 0.8);
                int blue = (int)(c.getBlue() * 0.8);
                c = new Color(red, green, blue);
            }
            g.setColor(c);

            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.GRAY);
            g.drawLine(0, 0, getWidth() - 1, 0);
            g.drawLine(0, 0, 0, getHeight() - 1);
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    private class TabTopPanel extends JPanel {
        private class TabTopLayout implements LayoutManager {
            public void addLayoutComponent(String name, Component comp) {}
            public void removeLayoutComponent(Component comp) {}

            @Override
            public void layoutContainer(Container parent) {
                int x = 0;
                int maxHeight = 0;
                for (Tab tab : m_tabs) {
                    int height = tab.getPreferredSize().height;
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                }

                for (Tab tab : m_tabs) {
                    Dimension d = tab.getPreferredSize();
                    tab.setBounds(x + tab.deltaX, 0, d.width, maxHeight);
                    x += d.width;
                }
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                int width = 0;
                int height = 0;
                for (Tab tab : m_tabs) {
                    Dimension d = tab.getPreferredSize();
                    if (d.height > height) {
                        height = d.height;
                    }
                    width += d.width;
                }
                return new Dimension(width, height);
            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return minimumLayoutSize(parent);
            }
        }
        public TabTopPanel() {
            setLayout(new TabTopLayout());
            setOpaque(false);
        }

        public void setSelected(int index) {
            Tab t = m_tabs.get(index);
            remove(t);
            add(t);
        }

        public void update() {
            removeAll();

            if (getSelectedIndex() != -1) {
                add(m_tabs.get(getSelectedIndex()));
            }
            for (Tab tab : m_tabs) {
                if (getSelectedIndex() == tab.index) {
                    continue;
                }
                add(tab);
            }
            revalidate();
            repaint();
        }
    }

    private class ComponentBorder implements Border {
        public Insets getBorderInsets(Component c) {
            return (Insets)UIManager.get("TabbedPane.contentBorderInsets");
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y,
                int width, int height) {
            int selectedId = getSelectedIndex();
            Rectangle selected = (selectedId != -1) ? getBoundsAt(selectedId) :
                new Rectangle();

            g.setColor(UIManager.getColor("TabbedPane.highlight"));

            // Draw highlight
            g.drawLine(x, y + 1, selected.x, y + 1);
            g.drawLine(selected.x + selected.width - 1, y + 1, width, y + 1);

            g.setColor(Color.GRAY);

            // Draw Top line
            g.drawLine(x, y, selected.x, y);
            g.drawLine(selected.x + selected.width - 1, y, width, y);

            g.drawLine(x, y, x, y + height);
            g.drawLine(x + width - 1, y, x + width - 1, y + height);
            g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
        }
    }

    private class TabListener extends MouseAdapter {
        Tab m_tab;
        Point m_origination = null;
        boolean m_startedMoving = false;;

        public TabListener(Tab tab) {
            m_tab = tab;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (m_origination == null) {
                return;
            }

            Point p = e.getLocationOnScreen();
            if (!m_startedMoving && (Math.abs(p.x - m_origination.x) < 10)) {
                return;
            }

            m_startedMoving = true;
            m_tab.deltaX = p.x - m_origination.x;

            Rectangle bounds = getBoundsAt(m_tab.index);
            int middleTab = bounds.x + (bounds.width / 2);

            // If the middle "crossed over" any other tab, then swap positions
            if (m_tab.deltaX != 0) {
                int direction = m_tab.deltaX / Math.abs(m_tab.deltaX);
                int i = m_tab.index + direction;
                int newIndex = -1;
                int shift = 0;
                while ((i >= 0) && (i < m_tabs.size())) {
                    if (!m_tabs.get(i).slidable) {
                        break;
                    }

                    bounds = getBoundsAt(i);
                    int middle = bounds.x + (bounds.width / 2);
                    if ( ((direction > 0) && (middleTab > middle)) ||
                            (direction < 0) && (middleTab < middle)) {
                        newIndex = i;
                        shift += bounds.width;
                    } else {
                        break;
                    }

                    i += direction;
                }

                if (newIndex != -1) {
                    m_origination.x += direction * shift;
                    m_tab.deltaX -= direction * shift;
                    moveTab(m_tab.index, newIndex);
                }
            }

            m_topPanel.revalidate();
            
            // The border needs to be repainted. As soon as I learn how to
            // repaint the border only, I'll do that
            m_panel.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setSelectedIndex(m_tab.index);

            if ((m_origination == null) && m_tab.slidable) {
                m_origination = e.getLocationOnScreen();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            m_origination = null;
            m_startedMoving = false;
            m_tab.deltaX = 0;
            m_topPanel.revalidate();

            // The border needs to be repainted. As soon as I learn how to
            // repaint the border only, I'll do that
            m_panel.repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int rotation = e.getWheelRotation();
            rotation /= Math.abs(rotation); //Set it to +/- one.

            int newSelected = getSelectedIndex() + rotation;
            if (newSelected >= 0 && newSelected < getTabCount()) {
                setSelectedIndex(newSelected);
            }
        }
    }

    private class TabChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            m_topPanel.update();
            updateComponent();
            fireStateChanged();
        }
    }

    private ArrayList<Tab> m_tabs = new ArrayList<Tab>();
    private ArrayList<ChangeListener> m_listeners =
            new ArrayList<ChangeListener>();
    private SingleSelectionModel m_model = new DefaultSingleSelectionModel();

    private TabTopPanel m_topPanel = new TabTopPanel();
    private JPanel m_panel = new JPanel();

    private ChangeListener m_listener = new TabChangeListener();

    public SlideTabbedPane() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        m_panel.setLayout(new BorderLayout());
        m_panel.setBorder(new ComponentBorder());

        super.add(m_topPanel, BorderLayout.NORTH);
        super.add(m_panel);
        this.setModel(m_model);

        setDefaultColors();
    }

    private void setDefaultColors() {
        setBackground(UIManager.getColor("TabbedPane.background"));
        setForeground(UIManager.getColor("TabbedPane.foreground"));
    }

    public void addChangeListener(ChangeListener l) {
        m_listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        m_listeners.remove(l);
    }

    public ChangeListener[] getChangeListeners() {
        ChangeListener[] listeners = new ChangeListener[m_listeners.size()];
        for (int i = 0; i < listeners.length; i++) {
            listeners[i] = m_listeners.get(i);
        }
        return listeners;
    }

    public void fireStateChanged() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener listener : m_listeners) {
            listener.stateChanged(evt);
        }
    }


    public void insertTab(String title, Icon icon, Component component,
            String tip, boolean slidable, int index) {
        Tab tab = new Tab();
        tab.title = title;
        tab.tipText = tip;
        tab.icon = icon;
        tab.component = component;
        tab.slidable = slidable;
        tab.index = index;

        tab.update();
        m_tabs.add(index, tab);

        m_topPanel.update();
        if (m_tabs.size() == 1) {
            this.setSelectedIndex(0);
        }
    }

    public void insertTab(String title, Icon icon, Component component,
            String tip, int index) {
        insertTab(title, icon, component, tip, true, index);
    }

    public void addTab(String title, Icon icon, Component component,
            String tip, boolean slidable) {
        insertTab(title, icon, component, tip, slidable, m_tabs.size());
    }

    public void addTab(String title, Icon icon, Component component,
            String tip) {
        addTab(title, icon, component, tip, true);
    }

    public void addTab(String title, Icon icon, Component component) {
        addTab(title, icon, component, null);
    }

    public void addTab(String title, Component component) {
        addTab(title, null, component, null);
    }

    public Component add(Component component) {
        addTab(component.getName(), null, component, null);
        return component;
    }

    public Component add(String title, Component component) {
        addTab(title, null, component, null);
        return component;
    }

    public Component add(Component component, int index) {
        insertTab(component.getName(), null, component, null, index);
        return component;
    }


    public void remove(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }

        m_tabs.remove(index);
        for (int i = index; i < m_tabs.size(); i++) {
            m_tabs.get(i).index -= 1;
        }

        int selected = this.getSelectedIndex();
        if (selected == index) {
            if (getTabCount() == 0) {
                m_model.clearSelection();
            } else if(getTabCount() == selected) {
                setSelectedIndex(selected - 1);
            }
        } else if (selected > index) {
            this.setSelectedIndex(selected - 1);
        }

        m_topPanel.update();
        m_panel.repaint();
    }

    public void remove(Component component) {
        for (int i = 0; i < m_tabs.size(); i++) {
            Component c = m_tabs.get(i).component;
            if (c != null && c.equals(component)) {
                removeTabAt(i);
            }
        }
    }

    public void removeTabAt(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        remove(index);
        /* TODO: From the Javadock: Removes the tab at index. After the
         component associated with index is removed, its visibility is reset
         to true to ensure it will be visible if added to other containers. */
    }

    public void removeAll() {
        m_tabs.clear();
        this.setSelectedIndex(-1);
        m_topPanel.update();
    }


    public int getTabCount() {
        return m_tabs.size();
    }


    public String getTitleAt(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        return m_tabs.get(index).title;
    }

    public String getToolTipTextAt(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        return m_tabs.get(index).tipText;
    }

    public Icon getIconAt(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        return m_tabs.get(index).icon;
    }

    public Component getTabComponentAt(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        return m_tabs.get(index).tabComponent;
    }

    public Component getComponentAt(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        return m_tabs.get(index).component;
    }


    public void setTitleAt(int index, String title) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        m_tabs.get(index).title = title;

        m_tabs.get(index).update();
        m_topPanel.revalidate();
    }

    public void setToolTipTextAt(int index, String toolTipText) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        m_tabs.get(index).tipText = toolTipText;
    }

    public void setIconAt(int index, Icon icon) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        m_tabs.get(index).icon = icon;

        m_tabs.get(index).update();
        m_topPanel.revalidate();
    }

    public void setTabComponentAt(int index, Component component) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        m_tabs.get(index).tabComponent = component;

        m_tabs.get(index).update();
        m_topPanel.revalidate();
    }

    public void setComponentAt(int index, Component component) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        m_tabs.get(index).component = component;
        if (this.getSelectedIndex() == index) {
            updateComponent();
        }
    }

    public void setTabSlidable(int index, boolean slidable) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        m_tabs.get(index).slidable = slidable;
    }


    public int indexOfTab(String title) {
        for (int i = 0; i < m_tabs.size(); i++) {
            if (m_tabs.get(i).title.equals(title)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfTab(Icon icon) {
        for (int i = 0; i < m_tabs.size(); i++) {
            if (m_tabs.get(i).icon.equals(icon)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfTabComponent(Component component) {
        for (int i = 0; i < m_tabs.size(); i++) {
            if (m_tabs.get(i).tabComponent == null) {
                continue;
            }
            if (m_tabs.get(i).tabComponent.equals(component)) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfComponent(Component component) {
        for (int i = 0; i < m_tabs.size(); i++) {
            if (m_tabs.get(i).component.equals(component)) {
                return i;
            }
        }
        return -1;
    }


    public void setModel(SingleSelectionModel model) {
        m_model.removeChangeListener(m_listener);
        m_model = model;
        m_model.addChangeListener(m_listener);
    }

    public SingleSelectionModel getModel() {
        return m_model;
    }


    public void setSelectedIndex(int index) {
        if ((index < -1) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        m_model.setSelectedIndex(index);
    }

    public int getSelectedIndex() {
        return m_model.getSelectedIndex();
    }

    public Component getSelectedComponent() {
        return m_tabs.get(getSelectedIndex()).component;
    }

    public void setSelectedComponent(Component c) {
        for (Tab tab : m_tabs) {
            if (tab.component == c) {
                this.setSelectedIndex(tab.index);
            }
        }
        this.updateComponent();
    }
    

    public int indexAtLocation(int x, int y) {
        Component c = getComponentAt(x, y);
        if (c instanceof Tab) {
            return ((Tab)c).index;
        }
        return -1;
    }

    public Rectangle getBoundsAt(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException();
        }
        Tab tab = m_tabs.get(index);
        return tab.getBounds();
    }

    private void moveTab(int oldIndex, int newIndex) {
        int shift = newIndex - oldIndex;
        shift /= Math.abs(shift);

        Tab temp = m_tabs.get(oldIndex);
        temp.index = newIndex;
        m_tabs.remove(oldIndex);
        m_tabs.add(newIndex, temp);

        for (int i = oldIndex; i != newIndex; i += shift) {
            m_tabs.get(i).index -= shift;
        }

        this.setSelectedIndex(temp.index);
    }

    private void updateTab(int index) {
        m_tabs.get(index).update();
    }

    private void updateComponent() {
        int selected = getSelectedIndex();
        m_topPanel.repaint();

        m_panel.removeAll();
        if (getSelectedIndex() == -1) {
            return;
        }
        
        m_panel.add(m_tabs.get(selected).component);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                m_panel.revalidate();
                m_panel.repaint();
            }
        });
    }

    @Override
    public Component[] getComponents() {
        Component[] components = new Component[getTabCount()];
        for (int i = 0; i < m_tabs.size(); i++) {
            components[i] = m_tabs.get(i).component;
        }
        return components;
    }
}
