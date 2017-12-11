package mybatis.one2many;

import mybatis.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

/**
 * Created by Administrator on 2017-6-21.
 */
public class One2ManyTest {
    private EmployeeMapper employeeMapper;
    private DepartmentMapper departmentMapper;

    public One2ManyTest() {
        employeeMapper = MybatisUtil.openSession().getMapper(EmployeeMapper.class);
        departmentMapper = MybatisUtil.openSession().getMapper(DepartmentMapper.class);
    }

    @Test
    public void testOne2ManySave() {
        SqlSession sqlSession = MybatisUtil.openSession();
        departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
        employeeMapper = sqlSession.getMapper(EmployeeMapper.class);

        Employee e1 = new Employee();
        e1.setName("e1");
        Employee e2 =new Employee();
        e2.setName("e2");

        Department department = new Department();
        department.setId(1L);
        department.setName("technology");
        department.getEmployees().add(e1);
        department.getEmployees().add(e2);

        departmentMapper.add(department);
        employeeMapper.add(e1);
        employeeMapper.add(e2);
        for (Employee e: department.getEmployees()){
            departmentMapper.updateRelation(department.getId(),e.getId());
        }
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testOne2ManyGet() {
        long startTime = System.currentTimeMillis();

        SqlSession sqlSession = MybatisUtil.openSession();
        departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
        employeeMapper = sqlSession.getMapper(EmployeeMapper.class);

        Department department = departmentMapper.get(1L);
        System.out.println(department.getName());
        System.out.println(department.getEmployees());

        long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) + "ms");
    }
}