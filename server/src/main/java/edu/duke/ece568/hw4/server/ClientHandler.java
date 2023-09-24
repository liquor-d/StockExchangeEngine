package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    Socket clientSkt;

    public ClientHandler(Socket client) {
        this.clientSkt = client;
    }

    /**
     * @param sendMsg is the text message needs to be sent to client
     * @throws IOException when there are input or output exceptions
     */
    public void sendTextMsg(String sendMsg) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(clientSkt.getOutputStream());
        dataOut.writeUTF(sendMsg); // send a string to the client
    }

    public String receiveTextMsg() throws IOException {
        DataInputStream dataIn = new DataInputStream(clientSkt.getInputStream());
        return dataIn.readUTF();
    }


    /**
     * @throws IOException when there are input or output exceptions
     */
    public void closeSocket() throws IOException {
        clientSkt.close();
    }

    @Override
    public void run() {
        try {
            String xml = receiveTextMsg();
            Parser parser = new Parser();
            Database a = new Database();
            a.init();
            SessionFactory sessionFactory = Database.getSessionFactory();
            Session session = sessionFactory.openSession();
            String message = parser.findRoot(xml, session);
            sendTextMsg(message);

            session.close();
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

    }
}
