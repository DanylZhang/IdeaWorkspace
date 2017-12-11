package JUnit5;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017-5-4.
 */
public class JUnit5Mock {
    public static int add(int... a) {
        int sum = 0;
        for (int i : a) {
            sum += i;
        }
        return sum;
    }

    private static double divide(int a, int b) {
        return a / b;
    }

    public static void main(String[] args) throws Exception {
        Method[] methods = EmployeeDAOTest.class.getMethods();

        List<Method> beforeMethods = Arrays.stream(methods).filter(method -> method.isAnnotationPresent(MyBefore.class)).collect(Collectors.toList());
        List<Method> afterMethods = Arrays.stream(methods).filter(method -> method.isAnnotationPresent(MyAfter.class)).collect(Collectors.toList());
        List<Method> testMethods = Arrays.stream(methods).filter(method -> method.isAnnotationPresent(MyTest.class)).collect(Collectors.toList());

        for (Method method : testMethods) {
            for (Method beforeMethod : beforeMethods) {
                beforeMethod.invoke(EmployeeDAOTest.class.newInstance());
            }
            method.invoke(EmployeeDAOTest.class.newInstance());
            for (Method afterMethod : afterMethods) {
                afterMethod.invoke(EmployeeDAOTest.class.newInstance());
            }
        }
    }

    @Test
    public void addTest() {
        Assert.assertEquals("测试断言是否成功！", 7, add(1, 2, 4));
    }

    @Test(expected = ArithmeticException.class)
    public void divideTest() {
        divide(1, 0);
    }

    @Test(timeout = 2000)
    public void testTimeOut() {
        long begin = System.currentTimeMillis();
        String str = "";
        for (int i = 0; i < 10000; i++) {
            str += i;
        }
        System.out.println(System.currentTimeMillis() - begin);
    }
}