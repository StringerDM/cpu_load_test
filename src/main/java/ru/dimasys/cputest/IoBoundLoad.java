package ru.dimasys.cputest;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import static java.lang.System.in;

@Component
public class IoBoundLoad {
    private final byte[] buffer;

    public IoBoundLoad() {
        int bufferSize = 1024 * 1024;
        byte[] buffer = new byte[bufferSize];
        new Random().nextBytes(buffer);

        this.buffer = buffer;
    }

    public long ioBoundWait(long waitMillis) throws IOException {
        long start = System.currentTimeMillis();
        try {
            InputStream in = createGeneratedStream();

            int bytesRead = 0;
            while (System.currentTimeMillis() - start < waitMillis) {

                int read = in.read(buffer);
                if (read == -1) {
                    // Если поток закончился — создаём новый генератор
                    in = createGeneratedStream();
                    read = in.read(buffer);
                }
                bytesRead += read;

            }
        } catch (IOException e) {
            // Обработка ошибок чтения
            return System.currentTimeMillis() - start;
        } finally {
            in.close();
        }

        return System.currentTimeMillis() - start;
    }

    private InputStream createGeneratedStream() {
        return new ByteArrayInputStream(buffer);
    }
}
