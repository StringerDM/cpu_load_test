package ru.dimasys.cputest;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@Component
public class CpuBoundLoad {
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public CpuBoundLoad() {
        if (threadMXBean.isThreadCpuTimeSupported() && !threadMXBean.isThreadCpuTimeEnabled()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
        }
    }

    public long burnCpu(long targetMillis) {
        long targetNanos = targetMillis * 1_000_000;
        long startCpuNanos = threadMXBean.getCurrentThreadCpuTime();

        long waste = 0;
        while ((threadMXBean.getCurrentThreadCpuTime() - startCpuNanos) < targetNanos) {
            waste += System.nanoTime();
//            waste += wasteSomeCpu(waste);
        }

        long usedCpuNanos = threadMXBean.getCurrentThreadCpuTime() - startCpuNanos;
        return usedCpuNanos / 1_000_000;
    }

    private long wasteSomeCpu(long seed) {
        double x = seed;
        for (int i = 0; i < 100; i++) {
            x = Math.pow(Math.sin(x), 2.0) + Math.sqrt(x + 1);
        }
        return (long) x;
    }
}
