package mybatis.query;

/**
 * Created by Administrator on 2017-6-23.
 */
public interface IUserService {
    public PageResult query(UserQueryObject qo);
}