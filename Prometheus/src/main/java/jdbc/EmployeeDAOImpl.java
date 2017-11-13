package jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2017-6-12.
 */
public class EmployeeDAOImpl extends JdbcDaoSupport implements IEmployeeDAO {
    @Override
    public void save(Employee e) {
        this.getJdbcTemplate().update("INSERT INTO employee (name,password) VALUES (?,?);", e.getName(), e.getPassword());
    }

    @Override
    public void update(Employee e) {
        this.getJdbcTemplate().update("UPDATE employee SET name=?,password=? WHERE id=?;", e.getName(), e.getPassword(), e.getId());
    }

    @Override
    public void delete(Long id) {
        this.getJdbcTemplate().update("DELETE FROM employee WHERE id=?;", id);
    }

    @Override
    public Employee get(Long id) {
        Employee employee = this.getJdbcTemplate().query("SELECT * FROM employee WHERE id=?", new ResultSetExtractor<Employee>() {
            @Override
            public Employee extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()){
                    Employee e = new Employee();
                    e.setId(resultSet.getLong("id"));
                    e.setName(resultSet.getString("name"));
                    e.setPassword(resultSet.getString("password"));
                    return e;
                }
                return null;
            }
        }, id);
        return employee;
    }

    @Override
    public List<Employee> list() {
//        List<Employee> es = this.jdbcTemplate.query("SELECT * FROM employee;", new ResultSetExtractor<List<Employee>>() {
//            @Override
//            public List<Employee> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
//                List<Employee> es = new ArrayList<Employee>();
//                while (resultSet.next()){
//                    Employee e = new Employee();
//                    e.setId(resultSet.getLong("id"));
//                    e.setName(resultSet.getString("name"));
//                    e.setPassword(resultSet.getString("password"));
//                    es.add(e);
//                }
//                return es;
//            }
//        });
        List<Employee> es =this.getJdbcTemplate().query("SELECT * FROM employee;", new RowMapper<Employee>() {
            @Override
            public Employee mapRow(ResultSet resultSet, int i) throws SQLException {
                Employee e = new Employee();
                e.setId(resultSet.getLong("id"));
                e.setName(resultSet.getString("name"));
                e.setPassword(resultSet.getString("password"));
                return e;
            }
        });
        return es;
    }
}