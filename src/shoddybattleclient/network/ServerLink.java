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
import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import shoddybattleclient.ServerConnect;

/**
 * An instance of this class acts as the client's link to the Shoddy Battle 2
 * server.
 *
 * @author Catherine
 */
public class ServerLink extends Thread {

    /**
     * Enum representing different status changes
     * that a user can have
     */
    public static enum Status {
        ONLINE,
        OFFLINE,
        AWAY,
        RETURN,
        BATTLE_START,
        BATTLE_END
    }

    /**
     * Messages sent by the client to the server.
     */
    public static class OutMessage extends ByteArrayOutputStream {
        protected final DataOutputStream m_stream = new DataOutputStream(this);
        public OutMessage(int type) {
            try {
                m_stream.write(type);
                m_stream.writeInt(0); // insert in 0 for size for now
            } catch (IOException e) {
                
            }
        }
        @Override
        public byte[] toByteArray() {
            byte[] bytes = super.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            buffer.putInt(1, bytes.length - 5);
            return bytes;
        }
    }

    public static class RequestChallengeMessage extends OutMessage {
        public RequestChallengeMessage(String user) {
            super(0); // see network.cpp
            try {
                m_stream.writeUTF(user);
            } catch (Exception e) {

            }
        }
    }

    public static class ChallengeResponseMessage extends OutMessage {
        public ChallengeResponseMessage(byte[] response) {
            super(1);

            try {
                m_stream.write(response, 0, 16);
            } catch (Exception e) {

            }
        }
    }

    public static class RegisterAccountMessage extends OutMessage {
        public RegisterAccountMessage(String user, String password) {
            super (2);

            try {
                m_stream.writeUTF(user);
                m_stream.writeUTF(password);
            } catch (Exception e) {

            }
        }
    }

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
     * Messages _received_ from the server that need to be handled by the
     * client.
     *
     * Note that the codes from this enum MUST match the codes from the
     * OutMessage::TYPE enum in the server.
     */
    public static class ServerMessage {
        static {
            m_map = new HashMap<Integer, ServerMessage>();

            // WELCOME_MESSAGE
            new ServerMessage(0, new MessageHandler() {
                // int32  : server version
                // string : server name
                // string : welcome message
                public void handle(ServerLink link, DataInputStream is)
                        throws IOException {
                    int version = is.readInt();
                    String name = is.readUTF();
                    String welcome = is.readUTF();

                    link.m_serverConnect =
                            new ServerConnect(link, name, welcome);
                    link.m_serverConnect.setVisible(true);

                    //System.out.println("Received WELCOME_MESSAGE.");
                    System.out.println("Server version: " + version);
                    //System.out.println("Server name: " + name);
                    //System.out.println("Welcome message: " + welcome);
                }
            });
            
            // PASSWORD_CHALLENGE
            new ServerMessage(1, new MessageHandler() {
                // byte[16] : the challenge
                public void handle(ServerLink link, DataInputStream is)
                        throws IOException {
                    byte[] challenge = new byte[16];
                    is.readFully(challenge);

                    // decrypt the challenge
                    try {
                        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

                        // pass 1
                        cipher.init(Cipher.DECRYPT_MODE, link.m_key[1]);
                        challenge = cipher.doFinal(challenge, 0, 16);

                        // pass 2
                        cipher.init(Cipher.DECRYPT_MODE, link.m_key[0]);
                        challenge = cipher.doFinal(challenge, 0, 16);

                        ByteBuffer buffer = ByteBuffer.wrap(challenge);
                        int r = buffer.getInt(0) + 1;
                        buffer.putInt(0, r);

                        // pass 1
                        cipher.init(Cipher.ENCRYPT_MODE, link.m_key[0]);
                        challenge = cipher.doFinal(challenge, 0, 16);

                        // pass 2
                        cipher.init(Cipher.ENCRYPT_MODE, link.m_key[1]);
                        challenge = cipher.doFinal(challenge, 0, 16);
                        
                        link.sendMessage(
                                new ChallengeResponseMessage(challenge));

                        // don't keep the keys in memory indefinitely
                        link.m_key[0] = null;
                        link.m_key[1] = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // REGISTRY_RESPONSE
            new ServerMessage(2, new MessageHandler() {
                // byte : type
                // string : details
                public void handle(ServerLink link, DataInputStream is)
                        throws IOException {
                    int type = is.readUnsignedByte();
                    String details = is.readUTF();

                    ServerConnect conn = link.m_serverConnect;

                    // see network.cpp for these values
                    switch (type) {
                        case 0:
                            conn.informNameUnavailable();
                            break;
                        case 1:
                            conn.informRegisterSuccess();
                            break;
                        case 2:
                            conn.informInvalidName();
                            break;
                        case 3:
                            conn.informNameTooLong();
                            break;
                        case 4:
                            conn.informNonexistentAccount();
                            break;
                        case 5:
                            conn.informFailedChallenge();
                            break;
                        case 6:
                            conn.informUserBanned(details);
                            break;
                    }
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

    private BlockingQueue<OutMessage> m_queue =
            new LinkedBlockingQueue<OutMessage>();
    private Socket m_socket;
    private DataInputStream m_input;
    private DataOutputStream m_output;
    protected SecretKeySpec[] m_key = new SecretKeySpec[2];
    private String m_name;
    private Thread m_messageThread;
    private ServerConnect m_serverConnect;

    public ServerLink(String host, int port)
            throws IOException, UnknownHostException {
        m_socket = new Socket(InetAddress.getByName(host), port);
        m_input = new DataInputStream(m_socket.getInputStream());
        m_output = new DataOutputStream(m_socket.getOutputStream());
    }

    public void registerAccount(String user, String password) {
        sendMessage(new RegisterAccountMessage(user, password));
    }

    public void attemptAuthentication(String user, String password) {
        m_name = user;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] key = digest.digest(password.getBytes("ISO-8859-1"));
            m_key[0] = new SecretKeySpec(key, 0, 16, "AES");
            m_key[1] = new SecretKeySpec(key, 16, 16, "AES");
            sendMessage(new RequestChallengeMessage(user));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to the server.
     * @param msg the message to send.
     */
    public void sendMessage(OutMessage msg) {
        try {
            m_queue.put(msg);
        } catch (InterruptedException e) {
            
        }
    }

    void spawnMessageQueue() {
        m_messageThread = new Thread(new Runnable() {
            public void run() {
                while (!interrupted()) {
                    OutMessage msg;
                    try {
                        msg = m_queue.take();
                    } catch (InterruptedException e) {
                        return; // end the thread
                    }
                    byte bytes[] = msg.toByteArray();
                    try {
                        m_output.write(bytes);
                    } catch (IOException e) {

                    }
                }
            }
        });
        m_messageThread.start();
    }

    public void close() {
        try {
            m_input.close();
        } catch (Exception e) {

        }
        try {
            m_output.close();
        } catch (Exception e) {

        }
    }

    /**
     * protocol is simple:
     *     byte type : type of message
     *     int32 length : length of message body
     *     byte[length] : message body
     */
    @Override
    public void run() {
        spawnMessageQueue();
        while (true) {
            try {
                int type = m_input.read();
                int length = m_input.readInt();
                byte[] body = new byte[length];
                m_input.readFully(body);

                // find the right handler to call
                final ServerMessage msg = ServerMessage.getMessage(type);
                if (msg == null) {
                    // unknown message type - but we can skip over it and live
                    System.out.println("Unkown message type: " + type);
                    continue;
                }

                // call the handler
                final DataInputStream stream = new DataInputStream(
                        new ByteArrayInputStream(body));
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            msg.handle(ServerLink.this, stream);
                        } catch (IOException e) {

                        }
                    }
                });

            } catch (IOException e) {
                // fatal error - exit the while loop
                break;
            }
        }

        // interrupt the message thread
        m_messageThread.interrupt();
    }

    public static void main(String[] args) throws Exception {
        ServerLink link = new ServerLink("localhost", 8446);
        link.attemptAuthentication("Catherine", "test");
        link.run(); // block
    }

}
