package springboot.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 异步任务串行化辅助类
 * @Date 2024/8/7 15:58
 * @Version V1.0.0
 * @Author zdd55
 */
public class AsyncCompleteFutureHelper {
    private static final Logger logger = LoggerFactory.getLogger(AsyncCompleteFutureHelper.class);

    public static final Map<String, CompletableFuture<?>> FUTURE_MAP = new ConcurrentHashMap<>();

    public static void addFuture(String id, Runnable runnable) {
        CompletableFuture<?> completableFuture = FUTURE_MAP.get(id);
        if (null == completableFuture) {
            completableFuture = CompletableFuture
                    .runAsync(runnable)
                    .whenComplete((r, e) -> {
                        logger.info("id {} future runAsync执行完毕", id);
                        FUTURE_MAP.remove(id);
                    })
                    .exceptionally(e -> {
                        FUTURE_MAP.remove(id);
                        logger.error("id future异常", e);
                        return null;
                    });
            FUTURE_MAP.put(id, completableFuture);
        } else {
            completableFuture
                    .thenRunAsync(runnable)
                    .whenComplete((r, e) -> {
                        logger.info("id {} future thenRunAsync 执行完毕", id);
                        FUTURE_MAP.remove(id);
                    })
                    .exceptionally(e -> {
                        FUTURE_MAP.remove(id);
                        logger.error("id future异常", e);
                        return null;
                    });
        }
    }

    public static void removeFuture(String dictId) {
        CompletableFuture<?> completableFuture = FUTURE_MAP.get(dictId);
        if (null != completableFuture) {
            if (!completableFuture.isDone()) {
                completableFuture.cancel(true);
            }
            FUTURE_MAP.remove(dictId);
        }
    }

}
