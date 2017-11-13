package dynamicProxy;

import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017-6-6.
 */
@Component
public class DepartmentServiceImpl implements IDepartmentService {
    @Override
    public void save(Department d) {
        System.out.println("保存department");
    }

    @Override
    public void update(Department d) {
        System.out.println("dao.update(department)");
    }
}