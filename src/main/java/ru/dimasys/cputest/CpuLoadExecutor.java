package ru.dimasys.cputest;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class CpuLoadExecutor {

    private final ExecutorService pool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public CpuLoadExecutor() {
        if (threadMXBean.isThreadCpuTimeSupported() && !threadMXBean.isThreadCpuTimeEnabled()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
        }
    }

    public long executeBlockingCpuBurn(long targetCpuMillis) throws Exception {
        Future<Long> future = pool.submit(() -> burnCpu(targetCpuMillis));
        return future.get(); // ждем результат
    }

    private long burnCpu(long targetMillis) {
        long targetNanos = targetCpuMillisToNanos(targetMillis);
        long startCpuNanos = threadMXBean.getCurrentThreadCpuTime();

        long waste = 0;
        while ((threadMXBean.getCurrentThreadCpuTime() - startCpuNanos) < targetNanos) {
            waste += System.nanoTime();
//            waste += wasteSomeCpu(waste);
        }

        long usedCpuNanos = threadMXBean.getCurrentThreadCpuTime() - startCpuNanos;
        return usedCpuNanos / 1_000_000; // вернуть в миллисекундах
    }

    private long targetCpuMillisToNanos(long cpuMillis) {
        return cpuMillis * 1_000_000;
    }

    private long wasteSomeCpu(long seed) {
        // Достаточно тяжёлая операция, чтобы CPU не простаивал
        double x = seed;
        for (int i = 0; i < 100; i++) {
            x = Math.pow(Math.sin(x), 2.0) + Math.sqrt(x + 1);
        }
        return (long) x;
    }
}
