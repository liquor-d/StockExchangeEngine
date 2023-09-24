package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;

public class Parser {
    DocumentBuilderFactory factory;
    DecimalFormat df = new DecimalFormat("#.#####");
    int length = 0;

    public Parser() {
        this.factory = DocumentBuilderFactory.newInstance();
    }

    public String findRoot(String xmlString, Session session) throws ParserConfigurationException, TransformerException {
        Response response = new Response();
        String[] lines = xmlString.split("\n");
        if (lines.length == 0) {
            response.errorMsg("XML string is empty.");
            return response.output();
        }
        int firstLine = lines[0].length() + 1;
        int len = Integer.parseInt(lines[0], 10);
        String request = xmlString.substring(firstLine);
        if (len != request.length()) {
            response.errorMsg("Number of lines in XML string does not match first line.");
            return response.output();
        }
        try {
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(request)));
            doc.getDocumentElement().normalize();
            String root = doc.getDocumentElement().getNodeName();
            if (root.equals("create")) {
                parserCreate(request, response, session);
                return (response.output());
            } else if (root.equals("transactions")) {
                String accountId = doc.getFirstChild().getAttributes().getNamedItem("id").getNodeValue();
                int accountIdInt = Integer.parseInt(accountId);
                parserTrans(request, accountIdInt, response, session);
                return (response.output());
            } else {
                response.errorMsg("Invalid XML tag");
                return response.output();
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            response.errorMsg("Parsing XML string error: " + e.getMessage());
            return response.output();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

    }

    private String parserTrans(String request, int accountId, Response response, Session session) throws ParserConfigurationException, IOException, SAXException {
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(request)));
        doc.getDocumentElement().normalize();
        Node nodeTrans = doc.getChildNodes().item(0);
        NodeList nodeList;
        ExecutionManager em = new ExecutionManager();
        if (nodeTrans.hasChildNodes()) {
            nodeList = nodeTrans.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeName().equals("order")) {
                    boolean isBuy = true;
                    String symbol = ((Element) node).getAttribute("sym");
                    String amount = ((Element) node).getAttribute("amount");
                    String limit = ((Element) node).getAttribute("limit");
                    double limitDouble;
                    if (limit.charAt(0) == '-') {
                        isBuy = false;
                        limitDouble = Double.parseDouble(df.format(Double.parseDouble(limit.substring(1))));
                    } else {
                        limitDouble = Double.parseDouble(df.format(Double.parseDouble(limit)));
                    }
                    String result = em.addOrder(limitDouble, Double.parseDouble(amount), symbol, accountId, isBuy, session);
                    response.transOrder(symbol, amount, limit, result);
                } else if (node.getNodeName().equals("query")) {
                    String id = ((Element) node).getAttribute("id");
                    if (em.isQueryValid(Integer.parseInt(id), session)) {
                        Execution cancel = em.queryCancelExecution(Integer.parseInt(id), session);
                        List<Execution> list = em.queryExecuteExecution(Integer.parseInt(id), session);
                        double share;
                        if (cancel != null) {
                            share = 0;
                            response.transQuery(id, df.format(share), cancel, list);
                        } else {
                            share = em.queryOrderShare(Integer.parseInt(id), session);
                            if (share == 0) {
                                response.transQuery(id, cancel, list);
                            } else {
                                response.transQuery(id, df.format(share), cancel, list);
                            }
                        }
                    } else {
                        response.transQueryError(id, "Not a valid TRANS_ID");
                    }
                } else if (node.getNodeName().equals("cancel")) {
                    String id = ((Element) node).getAttribute("id");
                    String message = em.addCancelExecution(Integer.parseInt(id), session);
                    if (message == null) {
                        Execution cancel = em.queryCancelExecution(Integer.parseInt(id), session);
                        List<Execution> list = em.queryExecuteExecution(Integer.parseInt(id), session);
                        response.transCancel(id, cancel, list);
                    } else {
                        response.transCancelError(id, message);
                    }

                }

            }
        }
        return null;
    }

    public void parserCreate(String request, Response response, Session session) throws ParserConfigurationException, IOException, SAXException {
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(request)));
        doc.getDocumentElement().normalize();
        if (doc.hasChildNodes()) {
            HashSet<String> ids = new HashSet<>();
            readNodes(doc.getChildNodes(), response, session, true, ids);
        }
    }

    public void readNodes(NodeList nodeList, Response response, Session session, boolean state, HashSet<String> ids) {
        CreateManager createManager = new CreateManager();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("account")) {
                    Node parentNode = node.getParentNode();
                    if (parentNode != null && parentNode.getNodeName().equals("symbol")) {
                        String symbol = ((Element) parentNode).getAttribute("sym");
                        String accountIdTemp = ((Element) node).getAttribute("id");
                        String share = node.getTextContent();
                        String res;
                        if (ids.contains(accountIdTemp)) {
                            res = createManager.addPosition(symbol, Double.parseDouble(share), Integer.parseInt(accountIdTemp), session);
                        } else {
                            res = "Account ID doesn't match!";
                        }
                        response.createPosition(accountIdTemp, symbol, res, state);
                    } else {
                        String accountId = ((Element) node).getAttribute("id");
                        String balance = ((Element) node).getAttribute("balance");
                        String res = createManager.addAccount(Integer.parseInt(accountId), Double.parseDouble(balance), session);
                        if (res != null) {
                            state = false;
                        } else {
                            state = true;
                            ids.add(accountId);
                        }
                        response.createAccount(accountId, res);
                    }
                }
                if (node.hasChildNodes()) {
                    readNodes(node.getChildNodes(), response, session, state, ids);
                }
            }

        }
    }


}
