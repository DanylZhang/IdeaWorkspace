package com.danyl.learnjava.mybatis.many2many;

import com.danyl.learnjava.mybatis.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class Many2ManyTest {
    private StudentMapper studentMapper;
    private TeacherMapper teacherMapper;

    public Many2ManyTest(){
        studentMapper = MybatisUtil.openSession().getMapper(StudentMapper.class);
        teacherMapper = MybatisUtil.openSession().getMapper(TeacherMapper.class);
    }

    @Test
    public void testSave(){
        SqlSession sqlSession = MybatisUtil.openSession();
        studentMapper = sqlSession.getMapper(StudentMapper.class);
        teacherMapper = sqlSession.getMapper(TeacherMapper.class);

        Student student1 = new Student();
        student1.setName("danylzhang");
        Student student2 = new Student();
        student2.setName("zhangdanyl");

        Teacher teacher1 = new Teacher();
        teacher1.setName("error");
        teacher1.getStudents().add(student1);
        teacher1.getStudents().add(student2);
        Teacher teacher2 = new Teacher();
        teacher2.setName("warning");
        teacher2.getStudents().add(student1);
        teacher2.getStudents().add(student2);

        teacherMapper.add(teacher1);
        teacherMapper.add(teacher2);
        studentMapper.add(student1);
        studentMapper.add(student2);

        for (Student s:teacher1.getStudents()){
            teacherMapper.resovleRelation(teacher1.getId(),s.getId());
        }
        for (Student s:teacher2.getStudents()){
            teacherMapper.resovleRelation(teacher2.getId(),s.getId());
        }

        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testGet(){
        SqlSession sqlSession = MybatisUtil.openSession();
        teacherMapper= sqlSession.getMapper(TeacherMapper.class);
        Teacher teacher = teacherMapper.get(1L);
        System.out.println(teacher.getName());
        System.out.println(teacher.getStudents());
        sqlSession.close();
    }
}