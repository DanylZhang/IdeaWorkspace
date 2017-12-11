package mybatis.many2many;

public interface StudentMapper {
    void add(Student student);
    Student get(Long id);
    Student getByTeacher(Long teaId);
}