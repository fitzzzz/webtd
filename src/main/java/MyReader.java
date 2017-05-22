import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by DavidLANG on 18/05/2017.
 */
public class MyReader {

    InputStream is;

    public MyReader(String path) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        this.is = classloader.getResourceAsStream(path);
    }

    public String read() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));;
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
        return  out.toString();
    }

}
