package com.danyl.learnjava.springhello;

/**
 * Created by Administrator on 2017-5-9.
 */
public class HelloWorld implements IHelloWorld {
    public HelloWorld() {
        System.out.println("==============");
    }

    @Override
    public void sayHello() {
        System.out.println("Hello spring man!");
    }
}
