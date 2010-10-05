/*
 * SpriteDownloader.java
 *
 * Created on August 26, 2010

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

package shoddybattleclient.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import shoddybattleclient.Preference;
import shoddybattleclient.utils.SwingWorker;
import shoddybattleclient.utils.bzip2.CBZip2InputStream;
import shoddybattleclient.utils.tar.TarEntry;
import shoddybattleclient.utils.tar.TarInputStream;

/**
 *
 * @author ben
 */
public class SpriteDownloader extends SwingWorker<Void, Integer> {
    public enum SpriteLink {
        NA ("N/A", ""),
        DP ("DP", "http://shoddybattle.com/sprites/dp.tar.bz2"),
        PLATINUM ("Platinum", "http://shoddybattle.com/sprites/platinum.tar.bz2"),
        BW ("Black/White", "http://shoddybattle.com/sprites/bw.tar.bz2");
        private String m_str;
        private String m_url;
        SpriteLink(String str, String url) {
            m_str = str;
            m_url = url;
        }
        public String getUrl() {
            return m_url;
        }
        @Override
        public String toString() {
            return m_str;
        }
    }

    public interface DownloadListener {
        void informFinished(boolean success);
    }

    private JFrame m_parent;
    private InputStream m_is;
    private int m_max;
    private int m_total = 0;
    private boolean m_succeeded;
    private ArrayList<DownloadListener> m_listeners =
            new ArrayList<DownloadListener>();
    public SpriteDownloader(JFrame parent, InputStream is, int max) {
        m_parent = parent;
        m_is = is;
        m_max = max;
    }

    public void addDownloadListener(DownloadListener listener) {
        m_listeners.add(listener);
    }
    
    @Override
    protected Void doInBackground() {
        m_succeeded = false;
        try {
            try {
                //discard first two bytes to make the bzip library work
                m_is.read();
                m_is.read();
                TarInputStream tar = new TarInputStream(
                        new CBZip2InputStream(m_is));
                TarEntry entry;
                while ((entry = tar.getNextEntry()) != null) {
                    File file = new File(
                            new File(Preference.getSpriteLocation()),
                            entry.getName());
                    if (file.exists()) {
                        JOptionPane.showMessageDialog(m_parent, "A package with"
                                + " this name is already installed.");
                        m_is.close();
                        tar.close();
                        return null;
                    }
                    if (entry.isDirectory()) {
                        file.mkdirs();
                    } else {
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        byte[] bytes = new byte[512];
                        int length;
                        while ((length = tar.read(bytes)) != -1) {
                            out.write(bytes, 0, length);
                            m_total += length;
                            int progress = (int)(100.0 * m_total / m_max);
                            if (progress > 100) progress = 100;
                            setProgress(progress);
                        }
                        out.flush();
                        out.close();
                    }
                }
                tar.close();
            } finally {
                m_is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        m_succeeded = true;
        return null;
    }

    @Override
    public void done() {
        for (DownloadListener listener : m_listeners) {
            listener.informFinished(m_succeeded);
        }
    }
}
