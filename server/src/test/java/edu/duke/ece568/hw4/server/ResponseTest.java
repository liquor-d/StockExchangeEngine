package edu.duke.ece568.hw4.server;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ResponseTest {
    @Test
    public void testCreateResponse() throws ParserConfigurationException, TransformerException {
        Response r = new Response();
        r.createAccount("123", "33333");
        String exp ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<results>\n" +
                "    <error id=\"123\">33333</error>\n" +
                "</results>\n";
        assertEquals(exp, r.output());
    }

    @Test
    void testOrder() throws ParserConfigurationException, TransformerException {
        Response r = new Response();
        r.transOrder("abc", "123", "11.1", "-error!");
        System.out.println(r.output());
        String exp1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<results>\n" +
                "    <error amount=\"123\" limit=\"11.1\" sym=\"abc\">error!</error>\n" +
                "</results>\n";
//        assertEquals(exp1, r.output());
        System.out.println(r.output());
        Response r2 = new Response();
        r2.transOrder("abc", "123", "11.1", "12345");
//        System.out.println(r2.output());
        String exp2 ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<results>\n" +
                "    <opened amount=\"123\" id=\"12345\" limit=\"11.1\" sym=\"abc\"/>\n" +
                "</results>\n";
//        assertEquals(exp2, r2.output());
    }

    @Test
    public void testError() throws ParserConfigurationException, TransformerException {
        Response r = new Response();
        r.transCancelError("12333", "error!!");
        String exp = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><results><canceled id=\"12333\"><error id=\"12333\">error!!</error></canceled></results>";
//        assertEquals(exp, r.output());
        Response r2 = new Response();
        String exp2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><results><status id=\"12333\"><error id=\"12333\">error!!</error></status></results>";
        r2.transQueryError("12333", "error!!");
//        assertEquals(exp2, r2.output());

    }

    @Test
    public void testQuery() throws ParserConfigurationException, TransformerException {
        Response r = new Response();
        Account a = new Account(123, 1000);
        BuySellOrder order = new BuySellOrder(123,123,a,"AA",true);
        Execution e = new Execution("buy", 13, 10, order );
        List<Execution> list = new ArrayList<>();
        list.add(e);
        r.transQuery("123","123", e, list);
        String exp = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><results><status id=\"123\"><open shares=\"123\"/><canceled shares=\"10.0\" time=\"Thu Apr 06 00:51:32 EDT 2023\"/><executed price=\"13.0\" shares=\"10.0\" time=\"Thu Apr 06 00:51:32 EDT 2023\"/></status></results>";
//        assertEquals(exp, r.output());
    }

}
