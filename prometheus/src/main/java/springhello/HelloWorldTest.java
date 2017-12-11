package springhello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017-5-9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/HelloWorldTest-context.xml")
public class HelloWorldTest {
    private IHelloWorld helloWorld;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testCtx() {
        helloWorld = ctx.getBean("helloWorld", IHelloWorld.class);
        helloWorld.sayHello();
    }

    @Test
    public void testBf() {
        Resource resource = new ClassPathResource("HelloWorldTest-context.xml");
        BeanFactory beanFactory = new XmlBeanFactory(resource);
        helloWorld = beanFactory.getBean("helloWorld", IHelloWorld.class);
        helloWorld.sayHello();
    }
}