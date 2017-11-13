package dynamicProxy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2017-6-3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/DynamicProxyTest-context.xml")
public class DynamicProxyTest {
    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private IDepartmentService departmentService;

    @Autowired
    private TransactionManager txManager;

    @Test
    public void test() {
        IEmployeeService o = (IEmployeeService) Proxy.newProxyInstance(employeeService.getClass().getClassLoader(), new Class[]{IEmployeeService.class}, new TransactionInvocationHandler(employeeService, txManager));
        o.save(new Employee());
    }

    @Test
    public void test1() {
        IDepartmentService o = (IDepartmentService) Proxy.newProxyInstance(departmentService.getClass().getClassLoader(), new Class[]{IDepartmentService.class}, new TransactionInvocationHandler(departmentService, txManager));
        o.save(new Department());
    }

    @Test
    public void test2() {
        IEmployeeService em = (IEmployeeService) Enhancer.create(employeeService.getClass(), new Class[]{}, new LogInvocationHandler(employeeService));
        em.save(new Employee());
        em.update(new Employee());
    }
}