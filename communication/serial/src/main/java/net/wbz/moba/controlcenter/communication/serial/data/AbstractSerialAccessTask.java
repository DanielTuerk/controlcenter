package net.wbz.moba.controlcenter.communication.serial.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * @author Daniel Tuerk (daniel.tuerk@w-b-z.com)
 */
abstract public class AbstractSerialAccessTask<T> implements Callable<T> {

    private final InputStream inputStream;
    private final OutputStream outputStream;

    public AbstractSerialAccessTask(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    protected InputStream getInputStream() {
        return inputStream;
    }

    protected OutputStream getOutputStream() {
        return outputStream;
    }
}
