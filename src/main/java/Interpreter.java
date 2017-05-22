import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by DavidLANG on 18/05/2017.
 */
public class Interpreter {

    private JSONArray jsonArray;
    private int position;
    private Document document;
    private Element root;

    public Interpreter(String line) throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        document = dBuilder.newDocument();
        root = document.createElement("trace");
        document.appendChild(root);
        this.jsonArray = new JSONArray(line);
        this.position = 1;
    }


    public void interprete() throws FileNotFoundException, UnsupportedEncodingException, TransformerException {
        this.interpreteContext();
        if (!this.TraceCountainsError())
            this.intrepetreRequestAnswer();
        else
            this.interpreteError();
        this.writeDocument();
    }

    private void interpreteError() {
        JSONObject requestJSON = jsonArray.getJSONObject(jsonArray.length() - 1);
        JSONObject data = requestJSON.getJSONObject("data");

        Element error = document.createElement("error");
        Element name = document.createElement("name");
//        JSONArray jsonArray = data.getJSONArray("exception");
        name.appendChild(document.createTextNode((String) data.getJSONArray("exception").get(0)));
        error.appendChild(name);
        root.appendChild(error);
    }


    private void writeDocument() throws FileNotFoundException, UnsupportedEncodingException, TransformerException {
        DOMSource domSource = new DOMSource(document);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(new File("trace.xml"));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        DOMImplementation domImpl = document.getImplementation();
        DocumentType doctype = domImpl.createDocumentType("doctype", "",
                "trace.dtd");

        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
        transformer.transform(domSource, result);
    }

    private void intrepetreRequestAnswer() {
        Element requestanswer;
        for (int i = 1; i < jsonArray.length(); i +=2) {
            requestanswer = document.createElement("requestanswer");
            requestanswer.appendChild(interpreteRequest(i));
            requestanswer.appendChild(interpreteAnswner(i + 1));
            root.appendChild(requestanswer);
        }
    }

    private Element interpreteRequest(int position) {
        JSONObject requestJSON = jsonArray.getJSONObject(position);
        JSONObject data = requestJSON.getJSONObject("data");

        Element request = document.createElement("request");
        Element name = document.createElement("nom_action");
        name.appendChild(document.createTextNode(data.getString("action")));
        request.appendChild(name);
        return request;
    }

    private Element interpreteAnswner(int position) {
        JSONObject answerJSON = jsonArray.getJSONObject(position);
        JSONObject data = answerJSON.getJSONObject("data");

        Element answer = document.createElement("answer");
        Element cost = document.createElement("cost");
        cost.appendChild(document.createTextNode(String.valueOf(data.getInt("cost"))));
        answer.appendChild(cost);
        return answer;
    }

    private void interpreteContext() {
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        JSONObject data = jsonObject.getJSONObject("data");

        Element context = document.createElement("context");
        root.appendChild(context);
        Element budget  = document.createElement("budget");
        context.appendChild(budget);
        int budgetNumber = data.getInt("budget");
        budget.appendChild(document.createTextNode(String.valueOf(budgetNumber)));
    }

    private boolean TraceCountainsError() {
        JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
        JSONObject data = jsonObject.getJSONObject("data");

        return data.has("exception");
    }



}
