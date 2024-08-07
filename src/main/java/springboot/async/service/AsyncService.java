package springboot.async.service;

import java.util.concurrent.Future;

public interface AsyncService {
    /**
     * 不拿异步执行的结果
     * @param time
     * @return
     * @throws InterruptedException
     */
    String test(int time) throws InterruptedException;

    /**
     * 拿到异步执行的结果
     * @param time
     * @return
     * @throws InterruptedException
     */
    Future<String> testResult(int time) throws InterruptedException;
}
