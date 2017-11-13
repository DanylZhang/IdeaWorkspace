package mybatis.one2many;

/**
 * Created by Administrator on 2017-6-22.
 */
public interface EmployeeMapper {
    Employee get(Long id);

    void add(Employee employee);
}