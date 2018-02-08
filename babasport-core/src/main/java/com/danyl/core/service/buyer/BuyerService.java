package com.danyl.core.service.buyer;

import com.danyl.core.bean.user.Buyer;

public interface BuyerService {
    public Buyer selectBuyerByUserName(String username);
}