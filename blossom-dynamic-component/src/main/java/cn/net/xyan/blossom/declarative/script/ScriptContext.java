package cn.net.xyan.blossom.declarative.script;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zarra on 16/6/10.
 */
public class ScriptContext {

    String source;

    RuntimeContext runtimeContext;

    Scriptable scope;

    Component root;

    DesignContext dc;

    Logger logger = LoggerFactory.getLogger(ScriptContext.class);


    public static Document parseXML(InputStream html) {
        try {
            Document doc = Jsoup.parse(html, "UTF-8", "", Parser.htmlParser());
            return doc;
        } catch (IOException e) {
            throw new DesignException("The html document cannot be parsed.");
        }
    }

    public void load(String xml)  {

        if (getRuntimeContext() == null) {
            this.runtimeContext = new RuntimeContext();
        }

        //only need know the tag with id;
        byte[] buffer = xml.getBytes();

        InputStream inputStream  = new ByteArrayInputStream(buffer);
        Document doc = parseXML(inputStream);

        Elements fieldElements = doc.getElementsByAttribute("id");

        for (Element element : fieldElements) {
            String id = element.attr("id");
            Component c = dc.getComponentById(id);
            runtimeContext.put(id,c);
            logger.info(id);

        }

        runtimeContext.put("__root__",getRoot());

        Elements scriptEles = doc.getElementsByTag("script");

        if(scriptEles.size()>1){
            throw new UnsupportedOperationException("script tag must be only one");
        }

        if (scriptEles.size() == 0)
            return;

        Element scriptE = scriptEles.first();


        String source = scriptE.html();

        this.source = source;

        runScript(source);

    }

    public void runScript(String source){
        try {
            Context cx = Context.enter();
            scope = cx.initStandardObjects();

            for (String key : runtimeContext.keySet()){
                Object v = runtimeContext.get(key);
                scope.put(key,scope,v);
            }

            Object result = cx.evaluateString(scope, source, null, 1, null);


        }catch (Throwable e){
            String msg = e.getMessage();

            Notification.show(msg, Notification.Type.ERROR_MESSAGE);
        }finally {
            Context.exit();
        }
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public DesignContext getDc() {
        return dc;
    }

    public void setDc(DesignContext dc) {
        this.dc = dc;
    }

    public Component getRoot() {
        return root;
    }

    public void setRoot(Component root) {
        this.root = root;
    }
}
