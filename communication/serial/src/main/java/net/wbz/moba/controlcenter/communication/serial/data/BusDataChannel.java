package net.wbz.moba.controlcenter.communication.serial.data;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.wbz.moba.controlcenter.communication.api.BusDataReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Deque;
import java.util.concurrent.*;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class BusDataChannel {
    private static final Logger log = LoggerFactory.getLogger(BusDataChannel.class);
    private static final long DELAY = 1000L;
//    private static final long DELAY = 5000L;

    private final Deque<AbstractSerialAccessTask> queue = new LinkedBlockingDeque<>();
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService serialTaskExecutor;

    private final OutputStream outputStream;
    private final InputStream inputStream;

    private final BusDataReceiver receiver;

    public BusDataChannel(InputStream inputStream, OutputStream outputStream, BusDataReceiver receiver) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.receiver=receiver;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("serial-io-executor-%d").build();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
        serialTaskExecutor = Executors.newSingleThreadExecutor(namedThreadFactory);
        start();
    }

    private void start() {
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
//                log.debug("start access");
                AbstractSerialAccessTask task;
                if (!queue.isEmpty()) {
                    task = queue.poll();
                } else {
                    task = new ReadBlockTask(inputStream, outputStream, receiver);
                }
//                log.debug("execute access task:" + task.getClass().getSimpleName());
                try {
                    serialTaskExecutor.submit(task).get();
                } catch (InterruptedException e) {
                    log.error("serial access interrupted", e);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    log.error("execution error of serial access", e);
                }
//                log.debug("finished access");
            }
        }, 0L, DELAY, TimeUnit.MILLISECONDS);
    }

    public void send(BusData busData) {
        queue.push(new WriteTask(inputStream, outputStream, busData));
    }

    public void shutdownNow() {
        serialTaskExecutor.shutdownNow();
        scheduledExecutorService.shutdownNow();
    }
}
