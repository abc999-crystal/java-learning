package springboot.async.controller;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.async.AsyncCompleteFutureHelper;
import springboot.async.service.AsyncService;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/test")
public class TestAsyncController {

    @Resource
    private AsyncService asyncService;

    @GetMapping("/async1")
    public String test() {
        System.out.println("开始执行任务");
        String id = StrUtil.uuid();
        AsyncCompleteFutureHelper.addFuture(id, () -> {
            System.out.println("开始执行异步任务");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("异步任务执行结束");
        });
        System.out.println("任务执行结束");
        return id;
    }

    @GetMapping("/async2")
    public String test2() throws InterruptedException {
        List<String> resultList = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            resultList.add(asyncService.test(i));
        }
        return "ok";
    }

    @GetMapping("/async3")
    public List<String> testResult() throws ExecutionException, InterruptedException {
        List<String> resultList = new LinkedList<>();
        Future<String> stringFuture0 = asyncService.testResult(0);
        Future<String> stringFuture1 = asyncService.testResult(1);
        Future<String> stringFuture2 = asyncService.testResult(2);
        Future<String> stringFuture3 = asyncService.testResult(3);
        Future<String> stringFuture4 = asyncService.testResult(4);
        resultList.add(stringFuture0.get());
        resultList.add(stringFuture1.get());
        resultList.add(stringFuture2.get());
        resultList.add(stringFuture3.get());
        resultList.add(stringFuture4.get());
        return resultList;
    }

}
