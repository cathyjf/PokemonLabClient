/*
 * File:   Metaserver.java
 * Author: Catherine
 *
 * Created on October 3, 2010, 3:59 PM
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

package shoddybattleclient.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import shoddybattleclient.WelcomeWindow.ServerListEntry;

/**
 *
 * @author Catherine
 */
public class Metaserver {

    public static void queryServer(final String host, final int port,
            final ServerListEntry entry, final Runnable informUpdate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramPacket query = new DatagramPacket(
                            new byte[0], 0,
                            InetAddress.getByName(host), port);
                    DatagramSocket socket = new DatagramSocket();
                    socket.setSoTimeout(20000);
                    socket.send(query);
                    byte[] response = new byte[4];
                    query.setData(response);
                    query.setLength(response.length);
                    socket.receive(query);
                    entry.setUsers(ByteBuffer.wrap(response).getInt());
                    informUpdate.run();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }).start();
    }

}
