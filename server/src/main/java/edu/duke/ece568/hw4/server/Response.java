package edu.duke.ece568.hw4.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

public class Response {
    Document doc;
    Element rootElement;

    public Response() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.doc = factory.newDocumentBuilder().newDocument();
        this.rootElement = doc.createElement("results");
    }

    public void errorMsg(String res) {
        Element error = doc.createElement("error");
        error.setTextContent(res);
        rootElement.appendChild(error);
    }

    public void createAccount(String id, String res) {
        if (res == null) {
            Element created = doc.createElement("created");
            created.setAttribute("id", id);
            rootElement.appendChild(created);
        } else {
            Element error = doc.createElement("error");
            error.setAttribute("id", id);
            error.setTextContent(res);
            rootElement.appendChild(error);
        }
    }

    public void createPosition(String id, String sym, String res, boolean state) {
        if (!state) {
            Element error = doc.createElement("error");
            error.setAttribute("sym", sym);
            error.setAttribute("id", id);
            error.setTextContent("Share and account can not be added twice");
            rootElement.appendChild(error);
        } else if (res == null) {
            Element created = doc.createElement("created");
            created.setAttribute("sym", sym);
            created.setAttribute("id", id);
            rootElement.appendChild(created);
        } else {
            Element error = doc.createElement("error");
            error.setAttribute("sym", sym);
            error.setAttribute("id", id);
            error.setTextContent(res);
            rootElement.appendChild(error);
        }
    }

    // TODO: the order here is not correct

    public void transOrder(String sym, String amount, String limit, String transIDorRes) {
        if (transIDorRes.charAt(0) == '-') {
            Element errorOpened = doc.createElement("error");
            errorOpened.setAttribute("sym", sym);
            errorOpened.setAttribute("amount", String.valueOf(amount));
            errorOpened.setAttribute("limit", String.valueOf(limit));
            errorOpened.setTextContent(transIDorRes.substring(1));
            rootElement.appendChild(errorOpened);
        } else {
            Element opened = doc.createElement("opened");
            opened.setAttribute("sym", sym);
            opened.setAttribute("amount", String.valueOf(amount));
            opened.setAttribute("limit", String.valueOf(limit));
            opened.setAttribute("id", transIDorRes);
            rootElement.appendChild(opened);
        }
    }

    public void transQueryError(String transID, String message) {
        Element status = doc.createElement("status");
        status.setAttribute("id", transID);
        Element error = doc.createElement("error");
        error.setAttribute("id", transID);
        error.setTextContent(message);
        rootElement.appendChild(status);
        status.appendChild(error);

    }

    public void transCancelError(String transID, String message) {
        Element canceled = doc.createElement("canceled");
        canceled.setAttribute("id", transID);
        Element error = doc.createElement("error");
        error.setAttribute("id", transID);
        error.setTextContent(message);
        rootElement.appendChild(canceled);
        canceled.appendChild(error);
    }

    public void transQuery(String transId, String share, Execution execution, List<Execution> list) {
        Element status = doc.createElement("status");
        status.setAttribute("id", String.valueOf(transId));
        Element open = doc.createElement("open");
        open.setAttribute("shares", String.valueOf(share));
        status.appendChild(open);
        // Create the canceled element for status
        if (execution != null) {
            Element canceled = doc.createElement("canceled");
            canceled.setAttribute("shares", String.valueOf(execution.getAmount()));
            canceled.setAttribute("time", execution.getDate().toString());
            status.appendChild(canceled);
        }

        // Create the executed element for status
        if (list != null) {
            for (Execution e : list) {
                Element executed = doc.createElement("executed");
                executed.setAttribute("shares", String.valueOf(e.getAmount()));
                executed.setAttribute("price", String.valueOf(e.getPrice()));
                executed.setAttribute("time", e.getDate().toString());
                status.appendChild(executed);
            }
        }

        rootElement.appendChild(status);
    }

    public void transQuery(String transId, Execution execution, List<Execution> list) {
        Element status = doc.createElement("status");
        status.setAttribute("id", String.valueOf(transId));
        // Create the canceled element for status
        if (execution != null) {
            Element canceled = doc.createElement("canceled");
            canceled.setAttribute("shares", String.valueOf(execution.getAmount()));
            canceled.setAttribute("time", execution.getDate().toString());
            status.appendChild(canceled);
        }

        // Create the executed element for status
        if (list != null) {
            for (Execution e : list) {
                Element executed = doc.createElement("executed");
                executed.setAttribute("shares", String.valueOf(e.getAmount()));
                executed.setAttribute("price", String.valueOf(e.getPrice()));
                executed.setAttribute("time", e.getDate().toString());
                status.appendChild(executed);
            }
        }

        rootElement.appendChild(status);
    }

    public void transCancel(String id, Execution cancel, List<Execution> list) {
        Element canceledElement = doc.createElement("canceled");
        canceledElement.setAttribute("id", String.valueOf(id));

        // Create the canceled element for canceled
        if (cancel != null) {
            Element canceledCanceled = doc.createElement("canceled");
            canceledCanceled.setAttribute("shares", String.valueOf(cancel.getAmount()));
            canceledCanceled.setAttribute("time", cancel.getDate().toString());
            canceledElement.appendChild(canceledCanceled);
        }
        if (list != null) {
            for (Execution e : list) {
                // Create the executed element for canceled
                Element executedCanceled = doc.createElement("executed");
                executedCanceled.setAttribute("shares", String.valueOf(e.getAmount()));
                executedCanceled.setAttribute("price", String.valueOf(e.getPrice()));
                executedCanceled.setAttribute("time", e.getDate().toString());
                canceledElement.appendChild(executedCanceled);
            }
        }

        rootElement.appendChild(canceledElement);
    }

    public String output() throws TransformerException {
        doc.appendChild(rootElement);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // Create the source object
        DOMSource source = new DOMSource(doc);

        // Create the output object
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }
}