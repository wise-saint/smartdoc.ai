package ai.smartdoc.garage.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService globalExecutor() {
        int corePoolSize = 8;
        int maxPoolSize = 16;
        int timeUnit = 60;
        int taskQueueSize = 100;

        ThreadFactory threadFactory = runnable -> {
            Thread t = new Thread(runnable);
            t.setName("global-worker-" + t.getId());
            return t;
        };

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                timeUnit, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(taskQueueSize),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy() // Fallback if queue full
        );
    }
}
