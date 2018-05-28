package com.danyl.learnjava.transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * Created by Administrator on 2017-6-16.
 */
@Repository
public class AccountDAOImpl implements IAccountDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void transin(Long id, double amount) {
        this.jdbcTemplate.update("UPDATE account SET balance=balance+? WHERE id=?;", amount, id);
    }

    @Override
    public void transout(Long id, double amount) {
        this.jdbcTemplate.update("update account set balance = balance-? where id=?;", amount, id);
    }
}
