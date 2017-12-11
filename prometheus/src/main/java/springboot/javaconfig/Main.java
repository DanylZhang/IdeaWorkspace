package springboot.javaconfig;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        //通过Java配置来实例化Spring容器
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);

        //在Spring容器中获取Bean对象
        UserService userService = ctx.getBean(UserService.class);

        //调用对象中的方法
        List<User> userList = userService.queryUserList();
        for (User user : userList) {
            System.out.println(user);
        }

        //销毁该容器
        ctx.destroy();
    }
}