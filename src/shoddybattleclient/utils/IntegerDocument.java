/* IntegerDocument.java
 *
 * Created on Sunday May 30, 2010, 3:28 PM
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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * This class is meant to restrict integer related input. The owner of the document
 * is required in order to enforce non-annoying minimum enforcement behavior. If
 * you don't want minimum enforcement, leave the constructor owners null.
 * @author Carlos
 */
public class IntegerDocument extends PlainDocument {

    private class IntegerFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            try {
                int num = Integer.parseInt(owner.getText());
                if (num < min)
                    owner.setText("" + min);
            } catch (NumberFormatException ex) {
                owner.setText(""+defaultValue);
            }
        }
    }

    private int min;
    private int max;
    private int defaultValue;
    private JTextComponent owner;

    public IntegerDocument(JTextComponent owner) {
        this(0, Integer.MAX_VALUE, owner);
    }

    public IntegerDocument(int max, JTextComponent owner) {
        this(0, max, owner);
    }

    public IntegerDocument(int min, int max, JTextComponent owner) {
        this(min, max, max, owner);
    }

    /**
     * Constructs a new Integer Document
     * @param min The minimum enforced value
     * @param max The maximum enforced value
     * @param defaultValue The enforced value for blank text boxes
     * @param owner The owner of this document
     */
    public IntegerDocument(int min, int max, int defaultValue, JTextComponent owner) {
        //TODO: Someone who knows more programming than me decide on the this.var = var
        this.min = min;
        this.max = max;
        this.defaultValue = defaultValue;
        this.owner = owner;
        if(owner != null)
            owner.addFocusListener(new IntegerFocusListener());
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
        //No support for negative numbers
        str = str.replaceAll("-", "");

        //Obtain the complete new string
        String previousStr = this.getText(0, getLength());
        String newStr = previousStr.substring(0, offs) + str
                        + previousStr.substring(offs, getLength());
        try {
            Integer num = Integer.parseInt(newStr);
            if (num <= max) {
                super.insertString(offs, str, a);
            } else {
                super.remove(0, getLength());
                super.insertString(0, ""+max, a);
            }

            //Strip all starting 0s (up until the length is one)
            int count = 0;
            while(count < (newStr.length() - 1) && newStr.charAt(count) == '0')
                count++;
            super.remove(0, count);
        } catch (NumberFormatException ex) {}
    }
}
