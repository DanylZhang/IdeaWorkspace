package dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017-6-3.
 */
public class TransactionInvocationHandler implements InvocationHandler {
    private Object target;
    private TransactionManager txManager;

    public TransactionInvocationHandler(Object target, TransactionManager txManager) {
        this.target = target;
        this.txManager = txManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().contains("save")) {
            txManager.begin();
            try {
                Object ret = method.invoke(target, args);
                txManager.commit();
                return ret;
            } catch (Exception e) {
                txManager.rollback();
            }
        } else {
            return method.invoke(target, args);
        }
        return null;
    }
}