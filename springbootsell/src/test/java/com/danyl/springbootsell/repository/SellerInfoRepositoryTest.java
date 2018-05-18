package com.danyl.springbootsell.repository;

import com.danyl.springbootsell.entity.SellerInfo;
import com.danyl.springbootsell.utils.KeyUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SellerInfoRepositoryTest {

    @Autowired
    private SellerInfoRepository repository;

    @Test
    public void addSellerInfo() {
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setId(KeyUtil.genUniqueKey());
        sellerInfo.setOpenid("ofKqt0er_ElO0UQqIZ3LXuKVJJxU");
        sellerInfo.setUsername("danyl");
        sellerInfo.setPassword("123");
        SellerInfo save = repository.save(sellerInfo);
        Assert.assertNotNull("保存成功", save);
    }
}