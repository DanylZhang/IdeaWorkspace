package mybatis.many2one;

import java.util.List;

/**
 * Created by Administrator on 2017-6-22.
 */
public interface EmployeeMapper {
    Employee get(Long id);

    void add(Employee employee);

    List<Employee> list();

    List<Employee> autoCompleteList();
}