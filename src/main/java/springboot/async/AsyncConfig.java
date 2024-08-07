package springboot.async;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Auther zdd
 * @Date 2024/08/07 16:08
 * @Version v1.0.0
 * @Description 异步线程配置类
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // 设置核心线程数
        executor.setMaxPoolSize(3); // 设置最大线程数
        executor.setQueueCapacity(2000); // 设置队列容量
        executor.setThreadNamePrefix("taskExecutor-"); // 设置线程名称前缀
        executor.setThreadGroupName("AsyncOperationGroup"); // 设置线程组名称
        executor.initialize(); // 初始化线程池
        return executor;
    }
}
