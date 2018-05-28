package com.danyl.learnjava.staticProxy;

/**
 * Created by Administrator on 2017-6-3.
 */
public class EmployeeTransactionServiceImpl implements IEmployeeService {
    private IEmployeeService target;

    public EmployeeTransactionServiceImpl(IEmployeeService target) {
        this.target = target;
    }

    @Override
    public void save(Employee e) {
        System.out.println("sessionFactory.getCurrentSession()");
        System.out.println("session.getTransaction().begin()");
        target.save(e);
        System.out.println("session.getTransaction().commit()");
    }

    @Override
    public void update(Employee e) {
        System.out.println("sessionFactory.getCurrentSession()");
        System.out.println("session.getTransaction().begin()");
        target.update(e);
        System.out.println("session.getTransaction().commit()");
    }
}
