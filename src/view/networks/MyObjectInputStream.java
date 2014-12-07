package view.networks;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.networks.serializators.Serializator;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;

/**
 * @author Alexander Vlasov
 */
public class MyObjectInputStream implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private InputStream in;
    private byte[] data;
    private volatile boolean transferComplete;
    private boolean closed;
    private BlockingQueue queue;

    public MyObjectInputStream(InputStream in, BlockingQueue queue) {
        this.in = in;
        queue.clear();
        this.queue = queue;
    }

    @Override
    public void run() {
        transferComplete = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error("Interrupt connection", e);
        }
        while (!transferComplete) {

            try {
                int value = in.read();
                if (value == -1) {
                    closed = true;
                    continue;
                }
                byte code = (byte) value;
                int len = Serializator.getLength(code);
                byte[] length = new byte[0];
                if (len == -1) {
                    length = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        value = in.read();
                        if (value == -1) {
                            closed = true;
                            continue;
                        }
                        length[i] = (byte) value;
                    }
                    len = Serializator.getLength(length);
                }
                data = new byte[len];
                data[0] = code;
                System.arraycopy(length, 0, data, 1, length.length);
                for (int i = 1 + length.length; i < len; i++) {
                    value = in.read();
                    if (value == -1) {
                        closed = true;
                        continue;
                    }
                    data[i] = (byte) value;
                }
                Object received = Serializator.build(data);
                logger.info("Принял " + received);
                put(received);
                if (received == NetworkSpecial.Disconnect) {
                    break;
                }

            } catch (SocketTimeoutException | SocketException e) {
                logger.error("Connection reset", e);
                closed = true;
                put(NetworkSpecial.LostConnection);
                break;
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        logger.info("ends working");
        close();
    }

    private void close() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void put(Object o) {
        try {
            queue.put(o);
        } catch (InterruptedException e) {
            logger.error("Interrupted", e);
            e.printStackTrace();
        }
    }
}
