package transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017-6-16.
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AccountServiceImpl implements IAccountService {
    @Autowired
    private IAccountDAO dao;

    public void setDao(IAccountDAO dao) {
        this.dao = dao;
    }

    @Override
    public void transin(Long id, double amount) {
        dao.transin(id, amount);
    }

    @Override
    public void transout(Long id, double amount) {
        dao.transout(id, amount);
    }

    @Override
    public void transfer(Long inid, Long outid, double amount) {
        this.transin(inid, amount);
        this.transout(outid, amount);
    }
}
