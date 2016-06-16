package cn.net.xyan.blossom.script.test;

import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by zarra on 16/6/9.
 */
public class Help {
    public static String readLargerTextFileAlternate(URI url) throws IOException {

        StringBuffer sb = new StringBuffer();
        Path path = Paths.get(url);
        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))){
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line+"\n");
            }
        }
        return sb.toString();
    }

    public static String loadScript(String file) throws IOException, URISyntaxException {
        URL url = Help.class.getClassLoader().getResource(file);

        if (url!=null){
            return readLargerTextFileAlternate(url.toURI());
        }
        return null;
    }

    public static InputStream loadInputStream(String file) throws IOException {
        URL url = Help.class.getClassLoader().getResource(file);

        if (url!=null){
            return url.openStream();
        }
        return null;
    }

    public static void setupScope(Scriptable scope){

    }
}
