package ru.dimasys.cputest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CpuLoadController {
    private final CpuLoadExecutor cpuLoadExecutor;

    public CpuLoadController(CpuLoadExecutor cpuLoadExecutor) {
        this.cpuLoadExecutor = cpuLoadExecutor;
    }

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Hello from Java");
    }

    @GetMapping("/load/{cpuMillis}")
    public ResponseEntity<String> load(@PathVariable long cpuMillis) throws Exception {
        long startWall = System.currentTimeMillis();

        long usedCpu = cpuLoadExecutor.executeBlockingCpuBurn(cpuMillis);

        long wallTime = System.currentTimeMillis() - startWall;

        return ResponseEntity.ok(String.format(
                "Target CPU: %d ms, Used CPU: %d ms, Wall time: %d ms",
                cpuMillis, usedCpu, wallTime
        ));
    }
}
