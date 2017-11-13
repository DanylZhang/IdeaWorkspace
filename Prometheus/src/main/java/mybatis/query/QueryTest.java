package mybatis.query;

import mybatis.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2017-6-21.
 */
public class QueryTest {
    @Test
    public void testInsert() throws IOException {
        User user = new User();
        user.setName("name");
        user.setAge(18);
        user.setEmail("123@wer");
        user.setBornDate(new Date());

        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.openSession();
            //sqlSession.insert("mybatis.UserMapper.batchAdd", userList);
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            mapper.add(user);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (sqlSession != null) {
                sqlSession.rollback();
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    public void testBatchInsert() throws IOException {

        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setName("name" + i);
            user.setAge(18);
            user.setEmail("123@wer");
            user.setBornDate(new Date());
            userList.add(user);
        }

        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.openSession();
            //sqlSession.insert("mybatis.UserMapper.batchAdd", userList);
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            mapper.batchAdd(userList);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (sqlSession != null) {
                sqlSession.rollback();
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    public void testUpdate() throws IOException {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setAge(18);
        user.setBornDate(new Date());

        SqlSession sqlSession = null;
        try {
            sqlSession = MybatisUtil.openSession();
            //sqlSession.update("mybatis.UserMapper.update", user);
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            mapper.update(user);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (sqlSession != null) {
                sqlSession.rollback();
            }
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Test
    public void testGet() throws Exception {
        SqlSession sqlSession = MybatisUtil.openSession();
        //User user = sqlSession.selectOne("mybatis.UserMapper.get", 1L);
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.get(1L);
        System.out.println(user);
    }

    @Test
    public void testDelete() {
        SqlSession sqlSession = MybatisUtil.openSession();
//        sqlSession.delete("mybatis.UserMapper.delete", 1L);
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        mapper.delete(1L);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testList() {
        SqlSession sqlSession = MybatisUtil.openSession();
//        List<User> users = sqlSession.selectList("mybatis.UserMapper.list");
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = mapper.list();
        System.out.println(users);
    }

    @Test
    public void testPage() {
        IUserService userService = new UserServiceImpl();
        UserQueryObject qo = new UserQueryObject();
        qo.setCurrentPage(1);
        qo.setPageSize(2);
        qo.setBeginAge(18);
        PageResult result = userService.query(qo);
        result.getResult();
    }

    @Test
    public void testLogin() {
        Map<String, Object> root = new HashMap<>();
        SqlSession sqlSession = MybatisUtil.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        root.put("username", "name");
        root.put("email", "123@wer");
        User u = mapper.login(root);
        System.out.println(u);
    }

    @Test
    public void testLogin2() {
        SqlSession sqlSession = MybatisUtil.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.login2("name", "123@wer");
        System.out.println(user);
    }
}