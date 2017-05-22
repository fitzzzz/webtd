import com.sun.xml.internal.ws.api.ResourceLoader;
import org.json.JSONArray;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;

/**
 * Created by DavidLANG on 04/05/2017.
 */
public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        MyReader myReader = new MyReader("runner.json");
        String line = myReader.read();
        Interpreter interpreter = new Interpreter(line);
        interpreter.interprete();
    }

}
