/*
 * File:   ServerLink.java
 * Author: Catherine
 *
 * Created on April 10, 2009, 2:55 AM
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
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * An instance of this class acts as the client's link to the Shoddy Battle 2
 * server.
 *
 * @author Catherine
 */
public class ServerLink extends Thread {

    public static abstract class MessageHandler {
        /**
         * Handle a message from the server by reading values from the
         * DataInputStream. The underlying InputStream is a byte array, not
         * a socket, so the method is unable to ruin the connection with
         * the server.
         */
        public abstract void handle(ServerLink link, DataInputStream is)
                throws IOException;
    }

    /**
     * Messages _received_ from the server that need to be handeld by the
     * client.
     *
     * Note that the codes from this enum MUST match the codes from the
     * OutMessage::TYPE enum in the server.
     *
     * Note also that handlers are NOT invoked on the Swing/AWT event
     * processing thread, so if any code invokes Swing methods, it must do so
     * through the Swing event queue class.
     */
    public static class ServerMessage {
        static {
            m_map = new HashMap<Integer, ServerMessage>();
            
            new ServerMessage(0, new MessageHandler() { // WELCOME_MESSAGE
                // int32  : server version
                // string : server name
                // string : welcome message
                public void handle(ServerLink link, DataInputStream is)
                        throws IOException {
                    int version = is.readInt();
                    String name = is.readUTF();
                    String welcome = is.readUTF();

                    System.out.println("Received WELCOME_MESSAGE.");
                    System.out.println("Server version: " + version);
                    System.out.println("Server name: " + name);
                    System.out.println("Welcome message: " + welcome);
                }
            });
            // add additional messages here
        }

        private static Map<Integer, ServerMessage> m_map;
        private MessageHandler m_handler;
        ServerMessage(int code, MessageHandler handler) {
            m_handler = handler;
            m_map.put(code, this);
        }
        public void handle(ServerLink link, DataInputStream is)
                throws IOException {
            m_handler.handle(link, is);
        }
        public static ServerMessage getMessage(int code) {
            return m_map.get(code);
        }
    }

    private Socket m_socket;
    private DataInputStream m_input;
    private DataOutputStream m_output;

    public ServerLink(String host, int port)
            throws IOException, UnknownHostException {
        m_socket = new Socket(InetAddress.getByName(host), port);
        m_input = new DataInputStream(m_socket.getInputStream());
        m_output = new DataOutputStream(m_socket.getOutputStream());
    }

    @Override
    public void run() {
        // protocol is simple:
        //     byte type : type of message
        //     int32 length : length of message body
        //     byte[length] : message body
        while (true) {
            try {
                int type = m_input.read();
                int length = m_input.readInt();
                byte[] body = new byte[length];
                m_input.readFully(body);

                // find the right handler to call
                ServerMessage msg = ServerMessage.getMessage(type);
                if (msg == null) {
                    // unknown message type - but we can skip over it and live
                    System.out.println("Unkown message type: " + type);
                    continue;
                }

                // call the handler
                DataInputStream stream = new DataInputStream(
                        new ByteArrayInputStream(body));
                try {
                    msg.handle(this, stream);
                } catch (IOException e) {
                    /** This one is less important -- block had the wrong
                     * number of bytes most likely. The next message will
                     * probably cause an error, but for now we don't need to
                     * die.
                     */
                    
                }

            } catch (IOException e) {
                // fatal error - exit the while loop
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ServerLink link = new ServerLink("localhost", 8446);
        link.run(); // block
    }

}
