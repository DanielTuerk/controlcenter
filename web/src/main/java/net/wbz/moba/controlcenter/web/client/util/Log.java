package net.wbz.moba.controlcenter.web.client.util;

/**
 * Logger for the browser java script console.
 */
public class Log {

    public static native void info(String text)
        /*-{
          console.log(text);
        }-*/;

    public static native void error(String text)
        /*-{
          console.error(text);
        }-*/;

    public static native void warn(String text)
        /*-{
          console.warn(text);
        }-*/;

    public static native void debug(String text)
        /*-{
          console.debug(text);
        }-*/;
}
