package springboot.async.service.impl;

import cn.hutool.core.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import springboot.async.service.AsyncService;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class AsyncServiceImpl implements AsyncService {

    private  final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Override
    @Async("taskExecutor")
    public String test(int time) throws InterruptedException {
        Thread.sleep(2000);
        String result = String.format("----这是第 %s 次,发送email", time);
        logger.info(result);
        return result;
    }

    @Override
    @Async("taskExecutor")
    public Future<String> testResult(int time) throws InterruptedException {
        int i = RandomUtil.randomInt(0, 1000);
        String result = String.format("这是第 %s 次,睡眠 %s ms", time, i);
        TimeUnit.MILLISECONDS.sleep(i);
        logger.info(result);
        return AsyncResult.forValue(result);
    }
}
