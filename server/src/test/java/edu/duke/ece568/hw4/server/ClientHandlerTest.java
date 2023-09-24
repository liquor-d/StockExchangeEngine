package edu.duke.ece568.hw4.server;

import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientHandlerTest {
    private static final int PORT = 12345;
    @Test
    public void testReceiveActions() throws IOException, ClassNotFoundException {
        Server server = new Server(PORT);
        Socket client = new Socket("localhost", PORT);
        server.connectClient();
        ClientHandler ch = new ClientHandler(server.getClientSkt());
        String expected = "test Action";
        DataOutputStream dataOut = new DataOutputStream(client.getOutputStream());
        dataOut.writeUTF(expected);
        String res = ch.receiveTextMsg();
        assertEquals(expected, res);
        // Teardown
        client.close();
        ch.closeSocket();
        server.closeSocket();
    }


    @Test
    public void testSendTextMsg() throws IOException {
        Server server = new Server(PORT);
        Socket client = new Socket("localhost", PORT);
        server.connectClient();
        ClientHandler ch = new ClientHandler(server.getClientSkt());
        String expected = "test Action";
        ch.sendTextMsg(expected);
        DataInputStream dataIn = new DataInputStream(client.getInputStream());
        String res = dataIn.readUTF();

        assertEquals(expected, res);

        ch.closeSocket();
        server.closeSocket();
        client.close();
    }
}
