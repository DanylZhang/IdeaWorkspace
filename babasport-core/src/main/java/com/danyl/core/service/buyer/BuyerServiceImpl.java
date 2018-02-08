package com.danyl.core.service.buyer;

import com.danyl.core.bean.user.Buyer;
import com.danyl.core.bean.user.BuyerQuery;
import com.danyl.core.dao.user.BuyerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    private BuyerDao buyerDao;

    @Override
    public Buyer selectBuyerByUserName(String username) {
        BuyerQuery buyerQuery = new BuyerQuery();
        buyerQuery.createCriteria().andUsernameEqualTo(username);
        List<Buyer> buyers = buyerDao.selectByExample(buyerQuery);
        if (buyers != null && buyers.size() > 0) {
            return buyers.get(0);
        }
        return null;
    }
}