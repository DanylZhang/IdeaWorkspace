package com.danyl.learnjava.mybatis.one2many;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-6-27.
 */
public class Department {
    private Long id;
    private String name;
    private List<Employee> employees=new ArrayList<Employee>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}