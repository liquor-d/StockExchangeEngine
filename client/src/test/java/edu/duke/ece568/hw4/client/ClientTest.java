package edu.duke.ece568.hw4.client;

import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {
    @Test
    public void testReceiveTextMsg() throws IOException {
        // Set up the server socket
        ServerSocket serverSocket = new ServerSocket(12345);
        // Set up the client instance
        Client client = new Client("localhost", 12345);
        Socket serverSkt = serverSocket.accept();
        String expected = "test Action";
        DataOutputStream dataOut = new DataOutputStream(serverSkt.getOutputStream());
        dataOut.writeUTF(expected);
        String res = client.receiveTextMsg();
        assertEquals(expected, res);
        serverSkt.close();
        client.closeClient();
        serverSocket.close();
    }

    @Test
    public void testSendTextMsg() throws IOException {
        // Set up the server socket
        ServerSocket serverSocket = new ServerSocket(12345);
        // Set up the client instance
        Client client = new Client("localhost", 12345);
        Socket serverSkt = serverSocket.accept();
        String expected = "test Action";
        client.sendTextMsg(expected);
        DataInputStream dataIn = new DataInputStream(serverSkt.getInputStream());
        String res = dataIn.readUTF();
        
        assertEquals(expected, res);
        serverSkt.close();
        client.closeClient();
        serverSocket.close();
    }

}
