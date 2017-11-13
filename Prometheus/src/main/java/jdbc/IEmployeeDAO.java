package jdbc;

import java.util.List;

/**
 * Created by Administrator on 2017-6-12.
 */
public interface IEmployeeDAO {
    void save(Employee e);

    void update(Employee e);

    void delete(Long id);

    Employee get(Long id);

    List<Employee> list();
}
