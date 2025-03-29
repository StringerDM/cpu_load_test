package ru.dimasys.cputest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoadController {
    private final CpuBoundLoad cpuBoundLoad;
    private final IoBoundLoad ioBoundLoad;
    private final CpuLoadAsyncExecutor cpuLoadAsyncExecutor;

    public LoadController(CpuBoundLoad cpuBoundLoad, IoBoundLoad ioBoundLoad,
                          CpuLoadAsyncExecutor cpuLoadAsyncExecutor) {
        this.cpuBoundLoad = cpuBoundLoad;
        this.ioBoundLoad = ioBoundLoad;
        this.cpuLoadAsyncExecutor = cpuLoadAsyncExecutor;
    }

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Hello from Java");
    }

    @GetMapping("/load/{cpuMillis}")
    public ResponseEntity<String> load(
            @PathVariable long cpuMillis,
            @RequestParam(required = false, defaultValue = "true") boolean isAsync,
            @RequestParam(required = false, defaultValue = "0") long await) throws Exception {

        long startWall = System.currentTimeMillis();

        long usedCpu;
        if (isAsync) {
            usedCpu = cpuLoadAsyncExecutor.executeAsyncCpuBurn(cpuMillis);
        } else {
            usedCpu = cpuBoundLoad.burnCpu(cpuMillis);
        }

        long waitTime = 0;
        if (await > 0) {
            waitTime = ioBoundLoad.ioBoundWait(await);
        }


        long wallTime = System.currentTimeMillis() - startWall;

        return ResponseEntity.ok(String.format(
                "Target CPU: %d ms, Async: %s, Target wait %d ms, Used CPU: %d ms, Wait time %d ms, Wall time: %d ms",
                cpuMillis, isAsync, await, usedCpu, waitTime, wallTime
        ));
    }
}
