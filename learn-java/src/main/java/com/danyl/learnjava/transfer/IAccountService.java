package com.danyl.learnjava.transfer;

/**
 * Created by Administrator on 2017-6-16.
 */
public interface IAccountService {
    void transin(Long id, double amount);
    void transout(Long id, double amount);
    void transfer(Long inid, Long outid, double amount);
}
