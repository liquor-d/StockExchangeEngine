package edu.duke.ece568.hw4.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final ServerSocket listener;
    Socket clientSkt;

    /**
     * @param port is the server port, should as same as client port
     * @throws IOException when there are input or output exceptions
     */
    public Server(int port) throws IOException {
        this.listener = new ServerSocket(port);
    }

    /**
     * @throws IOException when there are input or output exceptions
     */
    public void connectClient() throws IOException {
        this.clientSkt = this.listener.accept();
    }

    public Socket getClientSkt() {
        return clientSkt;
    }

    public void closeSocket() throws IOException {
        listener.close();
    }


}
