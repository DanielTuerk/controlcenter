package net.wbz.moba.controlcenter.communication.serial;

import net.wbz.moba.controlcenter.communication.api.Device;
import net.wbz.moba.controlcenter.communication.api.DeviceAccessException;
import net.wbz.moba.controlcenter.communication.api.InputModule;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class InputEncoderModule implements InputModule {

    private InputStream inputStream = null;
    private final byte address;
    private final boolean[] bits = {false, false, false, false, false, false, false, false};

    public InputEncoderModule(byte address) {
        this.address = (byte) (address | 128);


    }


    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        foo2(50);
//        foo(50);
//        while (true) {
//
//            foo(10);
//
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//            try {
//                outputStream.write(new byte[]{(byte) (10+128), 1});
//                outputStream.flush();
//            } catch (IOException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//            foo(10);
//            foo(50);
//
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//
//
//        }
//
////            input = ByteStreams.toByteArray(inputStream);
////
////            for(byte b : input) {
////                System.out.print(b);
////            }

    }

    private void foo2(int adr) {


        int antwort = 1000; //Wenn Rückgabe > Byte-Bereich, dann Fehler
        try {
            adr = (byte) adr;
            outputStream.write(adr);
            outputStream.write(0);
            outputStream.flush();
            antwort = inputStream.read();
            System.out.println(antwort);
        } catch (Exception e) {
        }

    }


    private OutputStream outputStream;

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean getBitState(Device.BIT bit) throws DeviceAccessException {
        switch (bit) {
            case BIT_1:
                return bits[0];
            case BIT_2:
                return bits[1];
            case BIT_3:
                return bits[2];
            case BIT_4:
                return bits[3];
            case BIT_5:
                return bits[4];
            case BIT_6:
                return bits[5];
            case BIT_7:
                return bits[6];
            case BIT_8:
                return bits[7];
        }
        throw new DeviceAccessException("invalid BIT " + bit.name());
    }

//    @Override
//    public void sendData(byte data) {
//        //To change body of implemented methods use File | Settings | File Templates.
//        try {
////            outputStream.write(new byte[]{address, data});
//            //128 + adresse, daten byte
////            outputStream.write(new byte[]{(byte) 208, 32});
////            outputStream.write(new byte[]{(byte) 208, 2});
////            outputStream.write(new byte[]{(byte) 208, 16});
////            outputStream.write(new byte[]{(byte) 208, 2});
////            outputStream.write(new byte[]{(byte) 208, 48});
////            outputStream.write(new byte[]{(byte) 208, 0});
//
//
//        } catch (IOException e) {
//        }
//    }


}
