package net.wbz.moba.controlcenter.web.client.util;

/**
 * Created by Daniel on 27.01.14.
 */
public class Log {
    public static native void console(String text)
/*-{
    console.log(text);
}-*/;
}
