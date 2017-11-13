package mybatis.one2many;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017-6-22.
 */
public interface DepartmentMapper {
    Department get(Long id);

    void add(Department department);

    void updateRelation(@Param("deptId") Long deptId,@Param("empId") Long empId);
}