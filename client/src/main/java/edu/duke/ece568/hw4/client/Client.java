package edu.duke.ece568.hw4.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//TODO: how to handle the input integer? how to do  graded tes*ng infrastructure?

public class Client {
    final private Socket serverSkt;

    /**
     * @param hostname is the server host name
     * @param port     is the server port, should as same as Server's port
     * @throws IOException when there are input or output exceptions
     */

    public Client(String hostname, int port) throws IOException {
        this.serverSkt = new Socket(hostname, port);
    }

    /**
     * client receive a string from server
     *
     * @return the text message received from server
     * @throws IOException when there are input or output exceptions
     */
    public String receiveTextMsg() throws IOException {
        DataInputStream dataIn = new DataInputStream(this.serverSkt.getInputStream());
        return dataIn.readUTF();
    }

    public void sendTextMsg(String sendMsg) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(this.serverSkt.getOutputStream());
        dataOut.writeUTF(sendMsg); // send a string to the client
    }

    /**
     * close the client socket
     *
     * @throws IOException when there are input or output exceptions
     */
    public void closeClient() throws IOException {
        this.serverSkt.close();
    }

}

