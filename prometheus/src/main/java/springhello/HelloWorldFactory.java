package springhello;

/**
 * Created by Administrator on 2017-5-9.
 */
public class HelloWorldFactory {
    public static HelloWorld create() {
        return new HelloWorld();
    }
}
