package com.danyl.learnjava.dynamicProxy;

/**
 * Created by Administrator on 2017-6-3.
 */
public class TransactionManager {
    public void begin() {
        System.out.println("session.getTransaction().begin()");
    }
    public void commit(){
        System.out.println("session.getTransaction().commit()");
    }
    public void rollback(){
        System.out.println("session.getTransaction().rollback()");
    }
}
