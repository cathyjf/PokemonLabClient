/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shoddybattleclient.utils;
import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import shoddybattleclient.LobbyWindow;

/**
 *
 * @author ben
 */
public class HTMLPane extends JTextPane {

    private static final int MAXIMUM_LINES = 150;

    private String m_user;
    private int m_lines = 0;
    private int m_actualLines = 0;

    public HTMLPane(String name) {
        super();
        m_user = name;
        setContentType("text/html");
        setEditable(false);
        setBackground(Color.WHITE);
        setOpaque(true);
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet css = new StyleSheet();
        try {
            css.importStyleSheet(new File("resources/main.css").toURL());
        } catch (MalformedURLException ex) {
            System.out.println("Failed to load style sheet");
        }
        kit.setStyleSheet(css);
        setEditorKit(kit);

        this.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    LobbyWindow.viewWebPage(e.getURL());
                }
            }
        });
    }

    /**
     * Encode HTML entities.
     * Copied from www.owasp.org.
     */
    public static String htmlEntityEncode(String s) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= '0' && c <= '9')) {
                buf.append(c);
            } else {
                buf.append("&#" + (int)c + ";");
            }
        }
        return buf.toString();
    }

    public void addMessage(String user, String message) {
        if (message == null) return;
        message = htmlEntityEncode(message);
        StringBuffer buffer = new StringBuffer();
        //Date d = new Date();
        //SimpleDateFormat f = new SimpleDateFormat("H:mm:ss");
        //buffer.append("[");
        //buffer.append(f.format(d));
        //buffer.append("] ");
        if (user != null) {
            String style = user.equals(m_user) ? "self" : "others";
            buffer.append("<font class=\"");
            buffer.append(style);
            buffer.append("\">");
            buffer.append(htmlEntityEncode(user));
            buffer.append("</font>");
            buffer.append(": ");
            buffer.append(message);
        }
        String msg = new String(buffer);
        msg = msg.replaceAll("&#32;", " ")
                        .replaceAll("\\b([^ ]*&#58;&#47;&#47;[^ ]+)",
                            "<a href=\"$1\">$1</a>");
        msg = "<div>" + msg + "</div>";
        HTMLDocument doc = (HTMLDocument)getDocument();
        HTMLEditorKit kit = (HTMLEditorKit)getEditorKit();
        try {
            kit.insertHTML(doc, doc.getLength(), msg, 1, 0, HTML.Tag.DIV);
            if (++m_lines > MAXIMUM_LINES) {
                int position = 0;
                int index;
                while (true) {
                    index = doc.getText(position, doc.getLength()).indexOf("\n");
                    if (index != 0) {
                        position += index;
                        break;
                    } else {
                        position += 1;
                    }
                }
                doc.remove(0, position);
            }
        } catch (Exception e) {

        }
        
        //scroll only if we are already at the bottom
        JScrollBar vbar = ((JScrollPane)this.getParent().getParent()).getVerticalScrollBar();
        if ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum()) {
            this.setCaretPosition(doc.getLength());
        } else {

        }
    }

    private void clear() {
        setText("");
        m_lines = 0;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("test htmlpane");
        frame.setSize(500, 300);
        frame.setLayout(new java.awt.GridLayout(1, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final HTMLPane pane = new HTMLPane("Ben");
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(pane);
        frame.add(scrollPane);
        pane.addMessage("Ben", "I am a polymath");
        pane.addMessage("Catherine", "I agree wholeheartedly");
        pane.addMessage("Catherine", "check out http://cathyisnotapolymath.com");
        frame.setVisible(true);
        /*new Thread(new Runnable() {
            public void run() {
                for (int i = 0; ; i++) {
                    pane.addMessage("spammer", String.valueOf(i));
                    synchronized (this) {
                        try {
                            wait(100);
                        } catch (InterruptedException ex) {

                        }
                    }
                }
            }
        }).run();*/
        
    }
}