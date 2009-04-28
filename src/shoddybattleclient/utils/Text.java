/* Text.java
 *
 * Created April 22, 2009
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
import java.io.*;
import java.util.*;

/**
 * Reads and allows for retrieval of text from a .lang file
 * @author ben
 */
public class Text {
    private static List<HashMap<Integer, String>> m_text = new ArrayList<HashMap<Integer, String>>();

    static {
        // TODO: remove this (it is temporary)
        loadText("english.lang");
    }

    public static String getText(int cat, int id, String[] args) {
        String text = m_text.get(cat).get(id);
        for (int i = 0; i < args.length; i++) {
            String match = "\\$" + (i + 1);
            text = text.replaceAll(match, args[i]);
        }
        return text;
    }

    public static String getText(int cat, int id) {
        return getText(cat, id, new String[0]);
    }

    public static void loadText(String file) {
        Scanner s = null;
        try {
            s = new Scanner(new File(Text.class.getResource("../languages/" + file).getFile()));
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load language file");
            return;
        }
        int category =  -1;
        int lineNumber = 0;
        while (s.hasNextLine()) {
            lineNumber++;
            String line = s.nextLine();
            line = line.trim();
            if ("".equals(line)) continue;
            
            int pos = line.indexOf("//");
            if (pos == 0) continue;
            if (pos != -1) {
                line = line.substring(0, pos - 1);
            }
            if (line.charAt(0) == '[') {
                if (line.indexOf(']') == -1) {
                    informError(lineNumber);
                    continue;
                }
                category++;
                m_text.add(new HashMap<Integer, String>());
                continue;
            }
            pos = line.indexOf(':');
            if (pos == -1) {
                informError(lineNumber);
                continue;
            }
            int id;
            try {
                id = Integer.parseInt(line.substring(0, pos));
            } catch (NumberFormatException e) {
                informError(lineNumber);
                continue;
            }
            String str = line.substring(pos + 1).trim();
            m_text.get(category).put(id, str);
        }
    }

    private static void informError(int line) {
        System.out.println("Syntax error on line " + line);
    }

    public static void main(String[] args) {
        System.out.println(Text.getText(12, 2, new String[] {"Bearzly", "2"}));
    }

}
