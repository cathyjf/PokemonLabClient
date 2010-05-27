/* RestrictiveDocument.java
 *
 * Created on Sunday November 08, 2009, 12:41 PM
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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Carlos
 */
public class RestrictiveDocument extends PlainDocument {

    private String[] filter;
    private int max;

    /**
     * Creates a new document that limits the kind of user import.
     * @param max The maximum amount of characters the document supports
     * @param filter A list of disallowed characters, which may be regular expressions
     */
    public RestrictiveDocument(int max, String[] filter) {
        this.max = max;
        this.filter = filter;
    }

    public RestrictiveDocument(int max) {
        this(max, new String[0]);
    }

    public RestrictiveDocument(String[] filter) {
        this(-1, filter);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {

        for(String f : filter) {
            str = str.replaceAll(f, "");
        }
        
        if(max > 0) {
            if(getLength() + str.length() > max) {
                str = str.substring(0, max-getLength());
            }
        }

        super.insertString(offs, str, a);
    }
}
