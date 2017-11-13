package springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springmvc.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class DataController {

    //最原始方式
    @RequestMapping("/data1")
    public ModelAndView data1(HttpServletRequest httpServletRequest) {
        httpServletRequest.setAttribute("msg", "下雨了");
        ModelAndView mv = new ModelAndView("data.jsp");
        return mv;
    }

    //通过ModelAndView方式
    @RequestMapping("/data2")
    public ModelAndView data2() {
        ModelAndView mv = new ModelAndView("data.jsp");
        mv.addObject("msg", "走走走");
        mv.addObject("明天来");//默认的key:数据类型(全小写)string
        mv.addObject(new Date());//key:date
        mv.addObject(new User("a", "123"));
        mv.addObject(new User("b", "234"));//后面的值覆盖前面的值
        return mv;
    }

    //直接返回对象，需要配置视图解析器
    @RequestMapping("/data3")
    @ModelAttribute("myUser")
    public User data3() {
        return new User("user", "123");//响应的视图 前缀+请求路径+后缀 /WEB-INF/view/data3.jsp
    }

    @RequestMapping("/data4")
    public String data4(Model model){
        model.addAttribute("msg","data4");
        return "show";//响应的视图 前缀+返回值+后缀 /WEB-INF/view/show.jsp
    }

    @RequestMapping("/data5")
    public String data5(Model model){
        model.addAttribute("key","data5");
        return "forward:index.jsp";//响应的视图 去掉前后缀，只有返回值:index.jsp 转发方式，将请求转发给另外一个视图返回，地址栏不改变
    }
    @RequestMapping("/data6")
    public String data6(Model model){
        model.addAttribute("key","data6");
        return "redirect:index.jsp";//响应的视图 去掉前后缀，只有返回值:index.jsp 重定向方式，将请求重定向到index.jsp，地址栏发生改变
    }
}