package ru.dimasys.cputest;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class CpuLoadAsyncExecutor {
    private final CpuBoundLoad cpuBoundLoad;
    private final ExecutorService pool;

    public CpuLoadAsyncExecutor(CpuBoundLoad cpuBoundLoad) {
        this.pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.cpuBoundLoad = cpuBoundLoad;
    }

    public long executeAsyncCpuBurn(long targetCpuMillis) throws Exception {
        Future<Long> future = pool.submit(() -> cpuBoundLoad.burnCpu(targetCpuMillis));
        return future.get();
    }

}
