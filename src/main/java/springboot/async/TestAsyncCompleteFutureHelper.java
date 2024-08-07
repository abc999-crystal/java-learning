package springboot.async;

import cn.hutool.core.util.StrUtil;
import org.apache.tomcat.jni.Time;

public class TestAsyncCompleteFutureHelper {
    public static void main(String[] args) {
        System.out.println("开始执行任务");
        String id = StrUtil.uuid();
        AsyncCompleteFutureHelper.addFuture(id, () -> {
            System.out.println("开始执行异步任务");
            Time.sleep(6);
            System.out.println("异步任务执行结束");
        });
        System.out.println("任务执行结束");
    }
}
