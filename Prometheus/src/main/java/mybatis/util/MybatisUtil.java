package mybatis.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017-6-21.
 */
public class MybatisUtil {
    private static MybatisUtil instance = new MybatisUtil();
    private SqlSessionFactory sqlSessionFactory;

    private MybatisUtil() {
        try {
//            Properties properties = new Properties();
//            properties.load(Resources.getResourceAsStream("db.properties"));
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSession openSession() {
        return instance.sqlSessionFactory.openSession();
    }
}
