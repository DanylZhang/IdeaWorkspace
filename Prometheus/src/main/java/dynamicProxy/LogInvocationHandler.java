package dynamicProxy;

import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Administrator on 2017-6-6.
 */
public class LogInvocationHandler implements InvocationHandler {
    private Object target;

    public LogInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("日志————操作时间：" + new Date() + "：当前你调用了" + method + "方法，传入的参数是：" + Arrays.toString(args));
        return method.invoke(target, args);
    }
}