package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;


import static org.junit.Assert.assertEquals;

public class ParserTest {
    @Test
    public void testParser() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String xml = "172\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<create>\n" +
                "  <account id=\"123456\" balance=\"1000\"/>\n" +
                "  <symbol sym=\"SPY\">\n" +
                "    <account id=\"123456\">100000</account>\n" +
                "  </symbol>\n" +
                "</create>";
        Parser p = new Parser();


        String xml2 = "222\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<transactions id=\"123456\">\n" +
                "  <order sym=\"TLSA\" amount=\"100\" limit=\"100\"/>\n" +
                "  <order sym=\"TLSA\" amount=\"100\" limit=\"101\"/>\n" +
                "  <order sym=\"TLSA\" amount=\"100\" limit=\"102\"/>\n" +
                "</transactions>";
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();

        p.findRoot(xml2, session);

        session.close();
    }
}
