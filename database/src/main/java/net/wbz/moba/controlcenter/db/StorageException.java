package net.wbz.moba.controlcenter.db;

/**
 * Exception for storage access.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class StorageException extends Exception {

    private static final long serialVersionUID = 1L;

    public StorageException(String msg) {
        super(msg);
    }
}
