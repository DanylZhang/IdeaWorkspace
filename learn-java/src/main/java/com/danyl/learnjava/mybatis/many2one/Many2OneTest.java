package com.danyl.learnjava.mybatis.many2one;

import com.danyl.learnjava.mybatis.util.MybatisUtil;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

/**
 * Created by Administrator on 2017-6-21.
 */
public class Many2OneTest {
    private EmployeeMapper employeeMapper;
    private DepartmentMapper departmentMapper;

    public Many2OneTest() {
        employeeMapper = MybatisUtil.openSession().getMapper(EmployeeMapper.class);
        departmentMapper = MybatisUtil.openSession().getMapper(DepartmentMapper.class);
    }

    @Test
    public void testMany2OneSave() {
        SqlSession sqlSession = MybatisUtil.openSession();
        departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
        employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
        Department department = new Department();
        department.setId(1);
        department.setName("technology");
        Employee employee = new Employee();
        employee.setName("DanylZhang");
        employee.setDepartment(department);

        departmentMapper.add(department);
        employeeMapper.add(employee);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testMany2OneGet() {
        long startTime = System.currentTimeMillis();

        SqlSession sqlSession = MybatisUtil.openSession();
        departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
        employeeMapper = sqlSession.getMapper(EmployeeMapper.class);

        //第一次查询
        Employee employee = employeeMapper.get(1L);
        System.out.println(employee.getDepartment().getName());
        sqlSession.close();

        //清除第一次查询的二级缓存
        sqlSession= MybatisUtil.openSession();
        Cache cache = sqlSession.getConfiguration().getCache("mybatis.many2one.EmployeeMapper");
        cache.clear();
        cache = sqlSession.getConfiguration().getCache("mybatis.many2one.DepartmentMapper");
        cache.clear();

        //第二次查询
        sqlSession= MybatisUtil.openSession();
        employeeMapper=sqlSession.getMapper(EmployeeMapper.class);
        employee = employeeMapper.get(1L);
        System.out.println(employee.getDepartment().getName());
        sqlSession.close();

//        List<Employee> es = employeeMapper.list();
//        for (Employee e : es) {
//            System.out.println(e.getDepartment().getName());
//            //清空mybatis一级缓存，一级缓存仅在当前sqlSession下有效
//            sqlSession.clearCache();
//        }
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) + "ms");
    }
}