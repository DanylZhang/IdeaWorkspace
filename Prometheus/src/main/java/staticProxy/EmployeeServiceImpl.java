package staticProxy;

/**
 * Created by Administrator on 2017-6-3.
 */
public class EmployeeServiceImpl implements IEmployeeService{
    @Override
    public void save(Employee e) {
        System.out.println("dao.save(e)");
    }

    @Override
    public void update(Employee e) {
        System.out.println("dao.update(e)");
    }
}
