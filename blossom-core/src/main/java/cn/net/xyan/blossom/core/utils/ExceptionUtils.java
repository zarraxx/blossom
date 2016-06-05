package cn.net.xyan.blossom.core.utils;

import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by zarra on 16/6/4.
 */
public class ExceptionUtils {
    static public String errorString(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    static public void traceError(Throwable e, Logger logger) {
        logger.error("\r\n" + errorString(e) + "\r\n");

    }
}
